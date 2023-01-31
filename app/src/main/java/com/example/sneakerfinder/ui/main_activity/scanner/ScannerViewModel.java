package com.example.sneakerfinder.ui.main_activity.scanner;

import android.app.Application;

import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.repo.ShoeRepository;

import java.util.Date;

import androidx.lifecycle.AndroidViewModel;

public class ScannerViewModel extends AndroidViewModel {
    private boolean photoCaptureInProgress;

    public ScannerViewModel(Application application) {
        super(application);
    }

    public boolean isPhotoCaptureInProgress() {
        return photoCaptureInProgress;
    }

    public void setPhotoCaptureInProgress(boolean photoCaptureInProgress) {
        this.photoCaptureInProgress = photoCaptureInProgress;
    }
}