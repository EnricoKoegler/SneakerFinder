package com.example.sneakerfinder.repo;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.example.sneakerfinder.db.AppDb;
import com.example.sneakerfinder.db.dao.ShoeDao;
import com.example.sneakerfinder.db.entity.Shoe;
import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanResult;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoeAndScan;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoeScanResults;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoes;
import com.example.sneakerfinder.exception.LowAccuracyResultException;
import com.example.sneakerfinder.exception.NotFoundException;
import com.example.sneakerfinder.helper.ThreadHelper;
import com.example.sneakerfinder.ml.ClassificationResult;
import com.example.sneakerfinder.ml.SneakersClassifier;
import com.example.sneakerfinder.network.RestClient;
import com.example.sneakerfinder.network.aws.dto.ShoeRecognitionResult;
import com.example.sneakerfinder.network.aws.dto.ShoeRecognitionResultObject;
import com.example.sneakerfinder.network.sneaks.SneaksAdapter;
import com.example.sneakerfinder.network.sneaks.dto.SneaksProduct;

import org.checkerframework.checker.units.qual.A;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

    public void recognizeShoe(ShoeScan shoeScan, ShoeRecognitionCallback cb) {
        ThreadHelper.getExecutor().execute(() -> {
            shoeScan.shoeScanId = shoeDao.insertShoeScan(shoeScan);

            boolean success = false;
            try {
                if (USE_ONLINE_RECOGNITION) {
                    recognizeShoeOnline(shoeScan);
                    success = true;
                }
            } catch (Exception e) {
                Log.d("REST", "Could not recognize shoe online");
            }

            try {
                if (!success) recognizeShoeOffline(shoeScan);

                ThreadHelper.getHandler().post(() -> cb.onRecognitionComplete(shoeScan.shoeScanId, RecognitionQuality.HIGH));
            } catch (LowAccuracyResultException e) {
                ThreadHelper.getHandler().post(() -> cb.onRecognitionComplete(shoeScan.shoeScanId, RecognitionQuality.NO_SHOE_RECOGNIZED));
            } catch (Exception e) {
                ThreadHelper.getHandler().post(() -> cb.onError(shoeScan.shoeScanId));
            }
        });
    }

    private void recognizeShoeOnline(ShoeScan shoeScan) throws IOException {
        Bitmap bitmap = loadAndRescaleImage(shoeScan.scanImageFilePath);

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

        if (result.objects.get(0).confidence < RECOGNITION_THRESHOLD) {
            throw new LowAccuracyResultException();
        } else {
            List<ShoeRecognitionResultObject> objects = result.objects;
            for (int i = 0; i < objects.size(); i++) {
                ShoeRecognitionResultObject o = objects.get(i);
                processRecognitionResult(shoeScan.shoeScanId, o.title, o.confidence, i == 0);
            }
        }
    }

    private static final int NUMBER_OF_RESULTS = 10;
    private static final float RECOGNITION_THRESHOLD = 0.6f;
    private void recognizeShoeOffline(ShoeScan shoeScan) {
        if (classifier == null) throw new NotFoundException();

        Bitmap bitmap = BitmapFactory.decodeFile(shoeScan.scanImageFilePath);
        List<ClassificationResult> classificationResults = classifier.classify(bitmap);

        // Get results with maximum accuracy
        Collections.sort(classificationResults);

        if (classificationResults.get(0).getAccuracy() < RECOGNITION_THRESHOLD) {
            throw new LowAccuracyResultException();
        } else {
            for (int i = 0; i < NUMBER_OF_RESULTS; i++) {
                ClassificationResult r = classificationResults.get(i);
                processRecognitionResult(shoeScan.shoeScanId, r.getClassName(), r.getAccuracy(), i == 0);
                Log.d("RESULT", r.toString());
            }
        }
    }

    // TODO: Add transaction
    private void processRecognitionResult(long shoeScanId, String shoeName,
                                          float confidence, boolean isTopResult) {
        Shoe shoe;
        try {
            shoe = searchShoe(shoeName);

        } catch (Exception e) {
            shoe = new Shoe();
            shoe.name = shoeName;

            Log.e("REST", "Shoe could not be retrieved: " + shoeName);
        }
        shoe.shoeId = shoeDao.insertShoe(shoe);

        ShoeScanResult scanResult =
                new ShoeScanResult(shoe.shoeId, shoeScanId, confidence, isTopResult);
        shoeDao.insertShoeScanResult(scanResult);
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
    public static final int IMAGE_QUALITY = 70;

    private Bitmap loadAndRescaleImage(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        double scaleFactor =  1d / Math.sqrt((double) (bitmap.getWidth() * bitmap.getHeight()) / (double) IMAGE_PIXELS);
        bitmap = Bitmap.createScaledBitmap(
                bitmap,
                (int) (bitmap.getWidth() * scaleFactor),
                (int) (bitmap.getHeight() * scaleFactor),
                true
        );

        return bitmap;
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

    public enum RecognitionQuality {NO_SHOE_RECOGNIZED, LOW, HIGH}
    public interface ShoeRecognitionCallback {
        void onRecognitionComplete(long shoeScanId, RecognitionQuality quality);
        void onError(long shoeScanId);
    }
}
