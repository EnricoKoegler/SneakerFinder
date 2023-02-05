package com.example.sneakerfinder.ui.scan_result;

import android.app.Application;

import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoeAndScan;
import com.example.sneakerfinder.repo.ShoeRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ScanResultViewModel extends ViewModel {
    private final ShoeRepository shoeRepository;
    private final long shoeScanId;
    private final long shoeId;

    public ScanResultViewModel(ShoeRepository shoeRepository, long shoeScanId, long shoeId) {
        this.shoeRepository = shoeRepository;
        this.shoeScanId = shoeScanId;
        this.shoeId = shoeId;
    }

    public LiveData<ShoeScanResultWithShoeAndScan> getShoeScanResult() {
        return shoeRepository.getShoeScanResult(shoeScanId, shoeId);
    }

    static class Factory implements ViewModelProvider.Factory {
        private final Application application;
        private final long shoeScanId;
        private final long shoeId;

        public Factory(Application application, long shoeScanId, long shoeId) {
            this.application = application;
            this.shoeScanId = shoeScanId;
            this.shoeId =  shoeId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ScanResultViewModel(new ShoeRepository(application), shoeScanId, shoeId);
        }
    }
}
