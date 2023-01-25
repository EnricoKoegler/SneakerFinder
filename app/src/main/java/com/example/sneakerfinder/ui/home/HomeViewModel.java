package com.example.sneakerfinder.ui.home;

import android.app.Application;

import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.repo.ShoeRepository;
import com.example.sneakerfinder.ui.scanner.ScannerViewModel;

import java.util.Date;

import androidx.lifecycle.AndroidViewModel;

public class HomeViewModel extends AndroidViewModel {
    private ShoeRepository shoeRepository;

    public HomeViewModel(Application application) {
        super(application);

        shoeRepository = new ShoeRepository(application);
    }

    public void recognizeShoe(String filePath, ShoeRepository.ShoeRecognitionCallback cb) {
        ShoeScan scan = new ShoeScan();
        scan.scanDate = new Date();
        scan.scanImageFilePath = filePath;

        shoeRepository.recognizeShoe(scan, new ShoeRepository.ShoeRecognitionCallback() {
            @Override
            public void onRecognitionComplete(long shoeScanId, ShoeRepository.RecognitionQuality quality) {
                cb.onRecognitionComplete(shoeScanId, quality);
            }

            @Override
            public void onError(long shoeScanId) {
                cb.onError(shoeScanId);
            }
        });
    }
}