package com.example.sneakerfinder.ui.similar_shoes;

import android.app.Application;

import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.example.sneakerfinder.repo.ShoeRepository;
import com.example.sneakerfinder.ui.scan_result.ScanResultViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SimilarShoesViewModel extends ViewModel {
    private final ShoeRepository shoeRepository;
    private final Long shoeScanId;
    public SimilarShoesViewModel(ShoeRepository shoeRepository, Long shoeScanId) {
        this.shoeRepository = shoeRepository;
        this.shoeScanId = shoeScanId;
    }

    public LiveData<List<ShoeScanResultWithShoe>> getSimilarShoes() {
        if (shoeScanId == null) return shoeRepository.getRecommendedShoes();
        else return shoeRepository.getSimilarShoes(shoeScanId);
    }

    static class Factory implements ViewModelProvider.Factory {
        private final Application application;
        private final Long shoeScanId;

        public Factory(Application application, Long shoeScanId) {
            this.application = application;
            this.shoeScanId = shoeScanId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new SimilarShoesViewModel(new ShoeRepository(application), shoeScanId);
        }
    }
}
