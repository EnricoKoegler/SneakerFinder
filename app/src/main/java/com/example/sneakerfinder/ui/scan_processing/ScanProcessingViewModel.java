package com.example.sneakerfinder.ui.scan_processing;

import android.app.Application;

import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanResult;
import com.example.sneakerfinder.repo.ShoeRepository;

import java.util.Date;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ScanProcessingViewModel extends AndroidViewModel {
    private final ShoeRepository shoeRepository;

    private final MutableLiveData<RecognitionState> recognitionStateLD = new MutableLiveData<>(RecognitionState.PROCESSING);

    private final MutableLiveData<String> scanImageFilePathLD = new MutableLiveData<>();
    private long mShoeScanId;

    public ScanProcessingViewModel(Application application) {
        super(application);
        shoeRepository = new ShoeRepository(application);
    }

    public void recognizeShoe(String pathToImage) {
        ShoeScan shoeScan = new ShoeScan();
        shoeScan.scanImageFilePath = pathToImage;
        scanImageFilePathLD.setValue(pathToImage);
        shoeScan.scanDate = new Date();

        shoeRepository.recognizeShoe(shoeScan, new ShoeRepository.ShoeRecognitionCallback() {
            @Override
            public void onRecognitionComplete(long shoeScanId, ShoeRepository.RecognitionQuality quality) {
                mShoeScanId = shoeScanId;

                RecognitionState state;
                switch (quality) {
                    case LOW: state = RecognitionState.LOW_ACCURACY_RESULT; break;
                    case HIGH: state = RecognitionState.HIGH_ACCURACY_RESULT; break;
                    case NO_SHOE_RECOGNIZED: default: state = RecognitionState.NO_RESULT; break;
                }
                recognitionStateLD.setValue(state);
            }

            @Override
            public void onError(long shoeScanId) {
                recognitionStateLD.setValue(RecognitionState.ERROR);
            }
        });
    }

    public LiveData<RecognitionState> getRecognitionState() {
        return recognitionStateLD;
    }

    public LiveData<ShoeScanResult> getTopResult() {
        return shoeRepository.getTopResult(mShoeScanId);
    }

    public long getShoeScanId() {
        return mShoeScanId;
    }

    public LiveData<String> getScanImagePath() {
        return scanImageFilePathLD;
    }
    public enum RecognitionState {PROCESSING, NO_RESULT, LOW_ACCURACY_RESULT, HIGH_ACCURACY_RESULT, ERROR}
}
