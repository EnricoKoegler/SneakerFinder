package com.example.sneakerfinder.ui.scan_result;

import android.app.Application;

import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.example.sneakerfinder.repo.ShoeRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class ScanResultViewModel extends AndroidViewModel {
    private final ShoeRepository shoeRepository;
    private final MutableLiveData<Long> scanId = new MutableLiveData<>();

    public ScanResultViewModel(@NonNull Application application) {
        super(application);
        shoeRepository = new ShoeRepository(application);
    }

    public void setScanId(long scanId) {
        this.scanId.setValue(scanId);
    }

    public LiveData<ShoeScan> getShoeScan() {
        return Transformations.switchMap(scanId, scanIdValue -> {
            if (scanIdValue == null) return new MutableLiveData<>();
            else return shoeRepository.getShoeScan(scanIdValue);
        });
    }

    public LiveData<List<ShoeScanResultWithShoe>> getShoeScanResults() {
        return Transformations.switchMap(scanId, scanIdValue -> {
            if (scanIdValue == null) return new MutableLiveData<>();
            else return shoeRepository.getShoeScanResults(scanIdValue);
        });
    }
}
