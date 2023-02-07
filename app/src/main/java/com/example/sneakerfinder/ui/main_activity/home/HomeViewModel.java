package com.example.sneakerfinder.ui.main_activity.home;

import android.app.Application;

import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoeScanResults;
import com.example.sneakerfinder.repo.ShoeRepository;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class HomeViewModel extends AndroidViewModel {
    private final ShoeRepository shoeRepository;

    public HomeViewModel(Application application) {
        super(application);
        shoeRepository = new ShoeRepository(application);
    }

    public LiveData<List<ShoeScanWithShoeScanResults>> getShoeScans() {
        return shoeRepository.getShoeScansWithShoeScanResults();
    }

    public LiveData<List<ShoeScanResultWithShoe>> getRecommendedShoes() {
        return shoeRepository.getRecommendedShoes();
    }
}