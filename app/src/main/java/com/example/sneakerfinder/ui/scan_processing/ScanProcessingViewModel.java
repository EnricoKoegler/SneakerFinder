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
    private ShoeScan mShoeScan;

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
            public void onRecognitionComplete(ShoeScan shoeScan) {
                mShoeScan = shoeScan;

                RecognitionState state;
                switch (shoeScan.resultQuality) {
                    case ShoeScan.RESULT_QUALITY_NO_RESULT: state = RecognitionState.NO_RESULT; break;
                    case ShoeScan.RESULT_QUALITY_LOW: state = RecognitionState.LOW_ACCURACY_RESULT; break;
                    case ShoeScan.RESULT_QUALITY_HIGH: state = RecognitionState.HIGH_ACCURACY_RESULT; break;
                    case ShoeScan.RESULT_QUALITY_ERROR: default: state = RecognitionState.ERROR; break;
                }
                recognitionStateLD.setValue(state);
            }

            @Override
            public void onError(ShoeScan shoeScan) {
                recognitionStateLD.setValue(RecognitionState.ERROR);
            }
        });
    }

    public LiveData<RecognitionState> getRecognitionState() {
        return recognitionStateLD;
    }

    public void setRecognitionState(RecognitionState state) {
        recognitionStateLD.setValue(state);
    }

    public LiveData<ShoeScanResult> getTopResult() {
        return shoeRepository.getTopResult(mShoeScan.shoeScanId);
    }

    public long getShoeScanId() {
        return mShoeScan.shoeScanId;
    }

    public LiveData<String> getScanImagePath() {
        return scanImageFilePathLD;
    }
    public enum RecognitionState {PROCESSING, NO_RESULT, LOW_ACCURACY_RESULT, HIGH_ACCURACY_RESULT, ERROR}
}
