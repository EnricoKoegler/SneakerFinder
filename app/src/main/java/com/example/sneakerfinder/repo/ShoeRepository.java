package com.example.sneakerfinder.repo;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.sneakerfinder.db.AppDb;
import com.example.sneakerfinder.db.dao.ShoeDao;
import com.example.sneakerfinder.db.entity.Shoe;
import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanResult;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoeScanResults;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoes;
import com.example.sneakerfinder.helper.ThreadHelper;
import com.example.sneakerfinder.network.RestClient;
import com.example.sneakerfinder.network.model.ShoeRecognitionResult;
import com.example.sneakerfinder.network.model.ShoeRecognitionResultObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class ShoeRepository {
    private final RestClient restClient;

    private final ShoeDao shoeDao;

    public ShoeRepository(Application application) {
        restClient = RestClient.getInstance(application);

        AppDb appDb = AppDb.getDatabase(application);
        shoeDao = appDb.getShoeDao();
    }

    public static final int IMAGE_PIXELS = 5 * 1024 * 1024;
    public static final int IMAGE_QUALITY = 70;

    public void recognizeShoe(ShoeScan shoeScan, ShoeRecognitionCallback cb) {
        ThreadHelper.getExecutor().execute(() -> {
            Bitmap bitmap = loadAndRescaleImage(shoeScan.scanImageFilePath);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, stream);

            try {
                Response<ShoeRecognitionResult> response =
                        restClient.getConnection().recognizeShoe(RequestBody.create(
                                MediaType.parse("image/jpeg"),
                                stream.toByteArray()
                        )).execute();

                ShoeRecognitionResult result = response.body();
                if (response.isSuccessful() && result != null) {
                    ShoeScanWithShoeScanResults results = processRecognitionResult(shoeScan, result);
                    ThreadHelper.getHandler().post(
                            () -> cb.onRecognitionComplete(results)
                    );
                } else {
                    ThreadHelper.getHandler().post(cb::onError);
                }
            } catch (IOException e) {
                ThreadHelper.getHandler().post(cb::onError);
            }
        });
    }

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

    // TODO: Add transaction
    private ShoeScanWithShoeScanResults processRecognitionResult(ShoeScan shoeScan, ShoeRecognitionResult result) {
        ShoeScanWithShoeScanResults shoeScanWithShoeScanResults = new ShoeScanWithShoeScanResults();
        shoeScanWithShoeScanResults.shoeScanResults = new ArrayList<>();

        shoeScan.shoeScanId = shoeDao.insertShoeScan(shoeScan);
        shoeScanWithShoeScanResults.shoeScan = shoeScan;

        for (ShoeRecognitionResultObject o: result.objects) {
            Shoe shoe = new Shoe(o.title, null, o.price, o.onlineStoreUrl, o.thumbnailUrl);
            shoe.shoeId = shoeDao.insertShoe(shoe);

            ShoeScanResult scanResult =
                    new ShoeScanResult(shoe.shoeId, shoeScan.shoeScanId, o.confidence,
                            result.objects.get(0) == o);
            shoeDao.insertShoeScanResult(scanResult);

            ShoeScanResultWithShoe shoeScanResultWithShoe = new ShoeScanResultWithShoe();
            shoeScanResultWithShoe.shoeScanResult = scanResult;
            shoeScanResultWithShoe.shoe = shoe;

            shoeScanWithShoeScanResults.shoeScanResults.add(shoeScanResultWithShoe);
        }

        return shoeScanWithShoeScanResults;
    }

    public LiveData<List<ShoeScanWithShoes>> getShoeScans() {
        return shoeDao.getAllShoeScans();
    }

    public LiveData<List<ShoeScanResultWithShoe>> getShoeScanResults(long shoeScanId) {
        return shoeDao.getShoeScanResults(shoeScanId);
    }

    public interface ShoeRecognitionCallback {
        void onRecognitionComplete(ShoeScanWithShoeScanResults scanResults);
        void onError();
    }
}
