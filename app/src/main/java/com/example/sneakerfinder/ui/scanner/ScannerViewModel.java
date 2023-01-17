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

    public void shoeScanFinished(ShoeRecognitionCallback cb) {
        currentShoeScan.scanDate = new Date();

        shoeRepository.recognizeShoe(currentShoeScan, new ShoeRepository.ShoeRecognitionCallback() {
            @Override
            public void onRecognitionComplete(ShoeScanWithShoeScanResults scanResults) {
                cb.onRecognitionComplete(scanResults);
            }

            @Override
            public void onError() {
                cb.onError();
            }
        });
    }

    public String getCurrentShoeScanImagePath() {
        return currentShoeScan.scanImageFilePath;
    }

    public interface ShoeRecognitionCallback {
        void onRecognitionComplete(ShoeScanWithShoeScanResults scanResults);
        void onError();
    }
}