package com.example.sneakerfinder.ui.scanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sneakerfinder.databinding.FragmentScannerProcessingBinding;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ScannerProcessingFragment extends Fragment {
    private FragmentScannerProcessingBinding binding;
    private ScannerViewModel scannerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        scannerViewModel = new ViewModelProvider(requireActivity()).get(ScannerViewModel.class);

        binding = FragmentScannerProcessingBinding.inflate(inflater, container, false);

        String currentImagePath = scannerViewModel.getCurrentShoeScanImagePath();

        if (currentImagePath != null) {
            Picasso.get().load("file://" + currentImagePath).into(binding.imageView);
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}