package com.example.sneakerfinder.ui.saved_results;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SavedResultsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SavedResultsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is saved results fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}