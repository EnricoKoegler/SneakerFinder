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
import androidx.lifecycle.Transformations;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class ShoeRepository {
    private final RestClient restClient;

    private final ShoeDao shoeDao;

    private SneakersClassifier classifier;

    public ShoeRepository(Application application) {
        restClient = RestClient.getInstance(application);

        AppDb appDb = AppDb.getDatabase(application);
        shoeDao = appDb.getShoeDao();

        try {
            classifier = SneakersClassifier.getInstance(application);
        } catch (IOException e) {
            Log.e("SneakersClassifier", "Model or lables could not be loaded");
        }
    }

    private static final boolean USE_ONLINE_RECOGNITION = false;
    private static final boolean USE_ONLINE_IMAGE_CROPPING = true;

    private static final int NUMBER_OF_RESULTS = 10;
    private static final float LOW_ACCURACY_THRESHOLD = 0.2f;
    private static final float HIGH_ACCURACY_THRESHOLD = 0.6f;


    public void recognizeShoe(ShoeScan shoeScan, ShoeRecognitionCallback cb) {
        ThreadHelper.getExecutor().execute(() -> {
            Bitmap bitmap = BitmapFactory.decodeFile(shoeScan.scanImageFilePath);

            if (USE_ONLINE_IMAGE_CROPPING) {
                try {
                    bitmap = cropImageOnline(bitmap);
                } catch (Exception e) {
                    Log.e("REST", "Online image cropping did not work");
                }
            }

            List<ClassificationResult> classificationResults = null;
            try {
                if (USE_ONLINE_RECOGNITION) {
                    classificationResults = recognizeShoeOnline(bitmap);
                }
            } catch (Exception e) {
                Log.e("REST", "Could not recognize shoe online");
            }

            try {
                if (classificationResults == null)
                    classificationResults = recognizeShoeOffline(bitmap);
            } catch (Exception e) {
                Log.e("REST", "Could not recognize shoe offline, cancelling");
                shoeScan.resultQuality = ShoeScan.RESULT_QUALITY_ERROR;
                shoeScan.shoeScanId = shoeDao.insertShoeScan(shoeScan);
                ThreadHelper.getHandler().post(() -> cb.onError(shoeScan));
                return;
            }

            // Sort classification results with accuracy descending
            Collections.sort(classificationResults);

            float topResultAccuracy = classificationResults.get(0).getAccuracy();

            if (topResultAccuracy < LOW_ACCURACY_THRESHOLD) shoeScan.resultQuality = ShoeScan.RESULT_QUALITY_NO_RESULT;
            else if (topResultAccuracy < HIGH_ACCURACY_THRESHOLD) shoeScan.resultQuality = ShoeScan.RESULT_QUALITY_LOW;
            else shoeScan.resultQuality = ShoeScan.RESULT_QUALITY_HIGH;

            shoeScan.shoeScanId = shoeDao.insertShoeScan(shoeScan);

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

            ThreadHelper.getHandler().post(() -> cb.onRecognitionComplete(shoeScan));
        });
    }

    private void processRecognitionResult(long shoeScanId, String shoeName,
                                                    float confidence, boolean isTopResult) {
        Shoe shoe;
        try {
            shoe = searchShoe(shoeName);

            Shoe savedShoe = shoeDao.getShoeByStyleId(shoe.styleId);
            if (savedShoe == null) shoe.shoeId = shoeDao.insertShoe(shoe);
            else shoe.shoeId = savedShoe.shoeId;

            ShoeScanResult scanResult =
                    new ShoeScanResult(shoe.shoeId, shoeScanId, confidence, isTopResult);
            shoeDao.insertShoeScanResult(scanResult);
        } catch (Exception e) {
            Log.e("REST", "Shoe could not be retrieved: " + shoeName);
        }
    }

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

    private List<ClassificationResult> recognizeShoeOffline(Bitmap bitmap) throws NotFoundException {
        if (classifier == null) throw new NotFoundException();

        return classifier.classify(bitmap);
    }

    @NonNull
    private Shoe searchShoe(String searchString) throws IOException {
        Response<List<SneaksProduct>> response =
                restClient.getSneaksConnection().getSneakers(searchString, 1).execute();

        List<SneaksProduct> sneaksProducts = response.body();

        if (!response.isSuccessful() || sneaksProducts == null || sneaksProducts.size() == 0) {
            Log.d("REST", "Shoe could not be found in SneaksAPI: " + searchString);
            throw new NotFoundException();
        }

        return SneaksAdapter.getShoe(sneaksProducts.get(0));
    }

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

    public LiveData<List<ShoeScanResultWithShoe>> getShoeScanResults(long shoeScanId) {
        return shoeDao.getShoeScanResultsWithShoes(shoeScanId);
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

    public interface ShoeRecognitionCallback {
        void onRecognitionComplete(ShoeScan shoeScan);
        void onError(ShoeScan shoeScan);
    }
}
