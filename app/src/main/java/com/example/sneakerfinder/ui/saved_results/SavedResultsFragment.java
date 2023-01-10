package com.example.sneakerfinder.ui.saved_results;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sneakerfinder.data.Shoe;
import com.example.sneakerfinder.databinding.FragmentSavedResultsBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class SavedResultsFragment extends Fragment {

    private FragmentSavedResultsBinding binding;
    private List<Shoe> shoes;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SavedResultsViewModel dashboardViewModel =
                new ViewModelProvider(this).get(SavedResultsViewModel.class);

        binding = FragmentSavedResultsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        shoes = new ArrayList<Shoe>();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}