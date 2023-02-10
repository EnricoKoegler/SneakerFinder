package com.example.sneakerfinder.repo;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.example.sneakerfinder.data.ClassificationResult;
import com.example.sneakerfinder.db.AppDb;
import com.example.sneakerfinder.db.dao.ShoeDao;
import com.example.sneakerfinder.db.entity.Shoe;
import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanResult;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoeAndScan;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoeScanResults;
import com.example.sneakerfinder.exception.NotFoundException;
import com.example.sneakerfinder.helper.SneakersClassifier;
import com.example.sneakerfinder.helper.ThreadHelper;
import com.example.sneakerfinder.network.RestClient;
import com.example.sneakerfinder.network.aws.dto.ShoeCropping;
import com.example.sneakerfinder.network.sneaks.SneaksAdapter;
import com.example.sneakerfinder.network.sneaks.dto.SneaksProduct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * The {@link ShoeRepository} is the single-source-of-truth for the app.
 * Here the {@link AppDb} is accessed and the {@link RestClient} is used to provide information
 * to the different {@link androidx.lifecycle.ViewModel} of the app.
 * Also the {@link SneakersClassifier} is used to make predictions locally on the users device.
 */
public class ShoeRepository {
    private final RestClient restClient;

    private final ShoeDao shoeDao;

    private SneakersClassifier classifier;

    /**
     * Sets up all data-sources for the repository.
     */
    public ShoeRepository(Application application) {
        restClient = RestClient.getInstance();

        AppDb appDb = AppDb.getDatabase(application);
        shoeDao = appDb.getShoeDao();

        try {
            classifier = SneakersClassifier.getInstance(application);
        } catch (IOException e) {
            Log.e("SneakersClassifier", "Model or lables could not be loaded");
        }
    }

    // Parameters for shoe classification
    private static final boolean USE_ONLINE_RECOGNITION = false;
    private static final boolean USE_ONLINE_IMAGE_CROPPING = true;

    /**
     * Number of results to store in the {@link AppDb} for each successful classification.
     */
    private static final int NUMBER_OF_RESULTS = 10;

    /**
     * These thresholds are used to estimate the quality of a {@link ShoeScan}.
     * Therefore only the top-result of a {@link ShoeScan} is taken into account.
     *
     * Given a ShoeScan with accuracy p of the top result the result quality of the ShoeScan
     * is determined like following:
     *
     * p < LOW_ACCURACY_THRESHOLD:                              ShoeScan.RESULT_QUALITY_NO_RESULT
     * LOW_ACCURACY_THRESHOLD < p < HIGH_ACCURACY_THRESHOLD:    ShoeScan.RESULT_QUALITY_LOW
     * HIGH_ACCURACY_THRESHOLD < p:                             ShoeScan.RESULT_QUALITY_HIGH
     */
    private static final float LOW_ACCURACY_THRESHOLD = 0.2f;
    private static final float HIGH_ACCURACY_THRESHOLD = 0.5f;

    /**
     * Holds information about the current shoeScan which is given to the UI as progress indicator.
     */
    private final MutableLiveData<ShoeScan> currentShoeScan = new MutableLiveData<>();

    /**
     * @return LiveData of the current processed {@link ShoeScan}.
     */
    public LiveData<ShoeScan> getCurrentShoeScan() {
        return currentShoeScan;
    }

    /**
     * @param shoeScan to be stored in the database. Afterwards the recognition process is
     *                 started for this ShoeScan.
     */
    public void storeShoeScanAndRecognizeShoe(ShoeScan shoeScan) {
        ThreadHelper.getExecutor().execute(() -> {
            shoeScan.resultQuality = ShoeScan.RESULT_QUALITY_ERROR;
            shoeScan.shoeScanId = shoeDao.insertShoeScan(shoeScan);

            recognizeShoe(shoeScan);
        });
    }

    /**
     * @param shoeScanId for which the recognition should be done.
     *
     * No shoe scan is inserted in the database here, but the existing shoe scan gets updated
     * accordingly.
     */
    public void retryShoeScan(long shoeScanId) {
        ThreadHelper.getExecutor().execute(() -> {
            ShoeScan shoeScan = shoeDao.getShoeScanById(shoeScanId);

            if (shoeScan == null) {
                // Provide information for the UI
                ShoeScan fakeShoeScan = new ShoeScan();
                fakeShoeScan.resultQuality = ShoeScan.RESULT_QUALITY_ERROR;
                currentShoeScan.postValue(fakeShoeScan);
            } else if (shoeScan.resultQuality == ShoeScan.RESULT_QUALITY_ERROR) {
                recognizeShoe(shoeScan);
            } else {
                // Use postValue instead of setValue because of background thread
                currentShoeScan.postValue(shoeScan);
            }
        });
    }

    /**
     * @param shoeScan to perform the recognition for
     */
    private void recognizeShoe(ShoeScan shoeScan) {
        shoeScan.resultQuality = ShoeScan.RESULT_QUALITY_PROCESSING;
        currentShoeScan.postValue(shoeScan);

        Bitmap bitmap = BitmapFactory.decodeFile(shoeScan.scanImageFilePath);

        if (USE_ONLINE_IMAGE_CROPPING) {
            try {
                bitmap = cropImageOnline(bitmap);
            } catch (Exception e) {
                Log.e("REST", "Online image cropping did not work: " + e.getMessage());
            }
        }

        List<ClassificationResult> classificationResults = null;
        try {
            if (USE_ONLINE_RECOGNITION) {
                classificationResults = recognizeShoeOnline(bitmap);
            }
        } catch (Exception e) {
            Log.e("REST", "Could not recognize shoe online: " + e.getMessage());
        }

        try {
            if (classificationResults == null)
                classificationResults = recognizeShoeOffline(bitmap);
        } catch (Exception e) {
            Log.e("REST", "Could not recognize shoe offline, cancelling: " + e.getMessage());
            shoeScan.resultQuality = ShoeScan.RESULT_QUALITY_ERROR;
            shoeDao.updateShoeScan(shoeScan);
            currentShoeScan.postValue(shoeScan);
            return;
        }

        // Sort classification results with accuracy descending
        Collections.sort(classificationResults);

        float topResultAccuracy = classificationResults.get(0).getAccuracy();

        if (topResultAccuracy < LOW_ACCURACY_THRESHOLD) shoeScan.resultQuality = ShoeScan.RESULT_QUALITY_NO_RESULT;
        else if (topResultAccuracy < HIGH_ACCURACY_THRESHOLD) shoeScan.resultQuality = ShoeScan.RESULT_QUALITY_LOW;
        else shoeScan.resultQuality = ShoeScan.RESULT_QUALITY_HIGH;

        try {
            // Tries to find information for all the Sneakers using the API
            // and inserting the found information into the database.
            if (topResultAccuracy > LOW_ACCURACY_THRESHOLD) {
                for (int i = 0; i < NUMBER_OF_RESULTS; i++) {
                    ClassificationResult clResult = classificationResults.get(i);
                        processRecognitionResult(
                                shoeScan.shoeScanId,
                                clResult.getClassName(),
                                clResult.getAccuracy(),
                                i == 0 && shoeScan.resultQuality == ShoeScan.RESULT_QUALITY_HIGH
                        );

                }
            }
        } catch (IOException e) {
            Log.e("REST", "No connection to SneaksAPI");
            shoeScan.resultQuality = ShoeScan.RESULT_QUALITY_ERROR;
        }

        shoeDao.updateShoeScan(shoeScan);

        // Successful recognition, give result to UI
        currentShoeScan.postValue(shoeScan);
    }

    /**
     * Processes one single classification result.
     * Looks for additional data and inserts everything into the database.
     * @throws IOException if the server is not reachable
     */
    private void processRecognitionResult(long shoeScanId, String shoeName,
                                          float confidence, boolean isTopResult) throws IOException {
        try {
            Shoe shoe = searchShoe(shoeName);

            Shoe savedShoe = shoeDao.getShoeByStyleId(shoe.styleId);
            if (savedShoe == null) shoe.shoeId = shoeDao.insertShoe(shoe);
            else shoe.shoeId = savedShoe.shoeId;

            ShoeScanResult scanResult =
                    new ShoeScanResult(shoe.shoeId, shoeScanId, confidence, isTopResult);
            shoeDao.insertShoeScanResult(scanResult);
        } catch (NotFoundException e) {
            Log.e("REST", "Shoe not found in SneaksAPI: " + shoeName);
        }
    }

    /**
     * @param bitmap to be cropped online
     * @return cropped bitmap
     * @throws IOException if the server is not accessible
     */
    private Bitmap cropImageOnline(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, stream);

        String base64String = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

        Response<ShoeCropping> response =
                restClient.getAWSConnection().cropShoe(RequestBody.create(
                        MediaType.parse("text/plain"),
                        base64String
                )).execute();

        ShoeCropping result = response.body();
        if (!response.isSuccessful() ||
                result == null ||
                result.result == null ||
                result.result.imageBase64 == null) throw new IOException();

        byte[] data = Base64.decode(result.result.imageBase64, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    /**
     * Was earlier used to perform shoe classification online.
     */
    private List<ClassificationResult> recognizeShoeOnline(Bitmap bitmap) throws IOException, NotFoundException {
        throw new IOException();
        /*bitmap = rescaleBitmap(bitmap);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, stream);

        String base64String = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

        Response<ShoeRecognitionResult> response =
                restClient.getAWSConnection().recognizeShoe(RequestBody.create(
                        MediaType.parse("image/jpeg"),
                        base64String
                )).execute();

        ShoeRecognitionResult result = response.body();
        if (!response.isSuccessful() || result == null) throw new NotFoundException();

        List<ClassificationResult> classificationResults = new ArrayList<>();
        for (ShoeRecognitionResultObject o: result.objects) {
            classificationResults.add(new ClassificationResult(-1, o.title, o.confidence));
        }
        return classificationResults;*/
    }

    /**
     * @param bitmap to be classified
     * @return list of results of the classification
     * @throws NotFoundException if the classifier was not found/initialized correctly
     */
    private List<ClassificationResult> recognizeShoeOffline(Bitmap bitmap) throws NotFoundException {
        if (classifier == null) throw new NotFoundException();

        return classifier.classify(bitmap);
    }

    /**
     * @param searchString e.g. shoes name
     * @return first {@link Shoe} matching the searchString
     * @throws IOException if server could not be accessed
     * @throws NotFoundException is no shoe was found for the given searchString
     */
    @NonNull
    private Shoe searchShoe(String searchString) throws IOException, NotFoundException {
        Response<List<SneaksProduct>> response =
                restClient.getSneaksConnection().getSneakers(searchString, 1).execute();

        List<SneaksProduct> sneaksProducts = response.body();

        if (!response.isSuccessful() || sneaksProducts == null || sneaksProducts.size() == 0) {
            Log.d("REST", "Shoe could not be found in SneaksAPI: " + searchString);
            throw new NotFoundException();
        }

        return SneaksAdapter.getShoe(sneaksProducts.get(0));
    }

    // Bitmap rescaling, used for online shoe recognition
    public static final int IMAGE_PIXELS = 5 * 1024 * 1024;
    public static final int IMAGE_QUALITY = 80;

    private Bitmap rescaleBitmap(Bitmap bitmap) {
        double scaleFactor =  1d / Math.sqrt((double) (bitmap.getWidth() * bitmap.getHeight()) / (double) IMAGE_PIXELS);
        return Bitmap.createScaledBitmap(
                bitmap,
                (int) (bitmap.getWidth() * scaleFactor),
                (int) (bitmap.getHeight() * scaleFactor),
                true
        );
    }

    /**
     * @return list of all {@link ShoeScanWithShoeScanResults} from the database.
     *
     * This mapping necessary because room doesn't support additional attributes for
     * n-to-n relationships.
     */
    public LiveData<List<ShoeScanWithShoeScanResults>> getShoeScansWithShoeScanResults() {
        return Transformations.map(shoeDao.getShoeScanResultsWithShoesAndScans(), scanResultList -> {
            Map<ShoeScan, List<ShoeScanResultWithShoe>> map = new HashMap<>();

            for (ShoeScanResultWithShoeAndScan rs: scanResultList) {
                ShoeScanResultWithShoe scanResultWithShoe = new ShoeScanResultWithShoe(rs.shoeScanResult, rs.shoe);
                if (map.containsKey(rs.shoeScan)) {
                    map.get(rs.shoeScan).add(scanResultWithShoe);
                } else {
                    List<ShoeScanResultWithShoe> shoeScanResultWithShoes = new ArrayList<>();
                    shoeScanResultWithShoes.add(scanResultWithShoe);
                    map.put(rs.shoeScan, shoeScanResultWithShoes);
                }
            }

            List<ShoeScanWithShoeScanResults> results = new ArrayList<>();
            for (Map.Entry<ShoeScan, List<ShoeScanResultWithShoe>> entry: map.entrySet()) {
                Collections.sort(entry.getValue()); // Sort by accuracy
                results.add(new ShoeScanWithShoeScanResults(entry.getKey(), entry.getValue()));
            }

            Collections.sort(results); // Sort by date
            return results;
        });
    }

    public LiveData<List<ShoeScanResultWithShoe>> getSimilarShoes(long shoeScanId) {
        return shoeDao.getSimilarShoes(shoeScanId);
    }

    public LiveData<List<ShoeScanResultWithShoe>> getRecommendedShoes() {
        return shoeDao.getRecommendedShoes();
    }

    public LiveData<ShoeScanResultWithShoeAndScan> getShoeScanResult(long shoeScanId, long shoeId) {
        return shoeDao.getShoeScanResult(shoeScanId, shoeId);
    }

    public LiveData<ShoeScanResult> getTopResult(long shoeScanId) {
        return shoeDao.getTopResult(shoeScanId);
    }

    public void deleteShoeScanAndResults(long shoeScanId) {
        ThreadHelper.getExecutor().execute(() -> shoeDao.deleteShoeScanAndResults(shoeScanId));
    }
}
