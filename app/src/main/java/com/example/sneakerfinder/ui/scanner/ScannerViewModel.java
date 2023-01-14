package com.example.sneakerfinder.ui.scanner;

import android.graphics.Bitmap;

import androidx.lifecycle.ViewModel;

public class ScannerViewModel extends ViewModel {
    private boolean photoCaptureInProgress;
    private Bitmap capturePreviewBitmap;

    public boolean isPhotoCaptureInProgress() {
        return photoCaptureInProgress;
    }

    public void setPhotoCaptureInProgress(boolean photoCaptureInProgress) {
        this.photoCaptureInProgress = photoCaptureInProgress;
    }

    public void setCapturePreviewBitmap(Bitmap capturePreviewBitmap) {
        this.capturePreviewBitmap = capturePreviewBitmap;
    }

    public Bitmap getCapturePreviewBitmap() {
        return capturePreviewBitmap;
    }
}