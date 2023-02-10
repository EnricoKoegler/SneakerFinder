package com.example.sneakerfinder.ui.scan_processing;

import android.app.Application;

import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanResult;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.example.sneakerfinder.repo.ShoeRepository;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ScanProcessingViewModel extends AndroidViewModel {
    private final ShoeRepository shoeRepository;

    public ScanProcessingViewModel(Application application) {
        super(application);
        shoeRepository = new ShoeRepository(application);
    }

    public void recognizeShoe(String pathToImage) {
        ShoeScan shoeScan = new ShoeScan();
        shoeScan.scanImageFilePath = pathToImage;
        shoeScan.scanDate = new Date();

        shoeRepository.storeShoeScanAndRecognizeShoe(shoeScan);
    }

    public void retryShoeScan(long shoeScanId) {
        shoeRepository.retryShoeScan(shoeScanId);
    }

    public LiveData<ShoeScanResult> getTopResult(long shoeScanId) {
        return shoeRepository.getTopResult(shoeScanId);
    }

    public LiveData<List<ShoeScanResultWithShoe>> getSimilarShoes(long shoeScanId) {
        return shoeRepository.getSimilarShoes(shoeScanId);
    }

    public LiveData<ShoeScan> getCurrentShoeScan() {
        return shoeRepository.getCurrentShoeScan();
    }
}
