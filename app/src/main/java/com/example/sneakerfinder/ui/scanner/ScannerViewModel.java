package com.example.sneakerfinder.ui.scanner;

import android.app.Application;

import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoeScanResults;
import com.example.sneakerfinder.repo.ShoeRepository;

import java.util.Date;

import androidx.lifecycle.AndroidViewModel;

public class ScannerViewModel extends AndroidViewModel {
    private final ShoeRepository shoeRepository;
    private boolean photoCaptureInProgress;
    private ShoeScan currentShoeScan;

    public ScannerViewModel(Application application) {
        super(application);
        shoeRepository = new ShoeRepository(application);
    }

    public boolean isPhotoCaptureInProgress() {
        return photoCaptureInProgress;
    }

    public void setPhotoCaptureInProgress(boolean photoCaptureInProgress) {
        this.photoCaptureInProgress = photoCaptureInProgress;
    }

    public void startShoeScan(String pathToImage) {
        currentShoeScan = new ShoeScan();
        currentShoeScan.scanImageFilePath = pathToImage;
    }

    public void shoeScanFinished(ShoeRepository.ShoeRecognitionCallback cb) {
        currentShoeScan.scanDate = new Date();

        shoeRepository.recognizeShoe(currentShoeScan, new ShoeRepository.ShoeRecognitionCallback() {
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

    public String getCurrentShoeScanImagePath() {
        return currentShoeScan.scanImageFilePath;
    }
}