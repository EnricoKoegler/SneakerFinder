package com.example.sneakerfinder.ui.saved_results;

import android.app.Application;

import com.example.sneakerfinder.db.entity.ShoeScanWithShoeScanResults;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoes;
import com.example.sneakerfinder.repo.ShoeRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class SavedResultsViewModel extends AndroidViewModel {

    private final ShoeRepository shoeRepository;

    public SavedResultsViewModel(@NonNull Application application) {
        super(application);
        shoeRepository = new ShoeRepository(application);
    }


    public LiveData<List<ShoeScanWithShoeScanResults>> getShoeScans() {
        return shoeRepository.getShoeScansWithShoeScanResults();
    }
}