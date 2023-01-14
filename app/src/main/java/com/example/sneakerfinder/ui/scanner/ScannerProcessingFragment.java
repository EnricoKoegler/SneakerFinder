package com.example.sneakerfinder.ui.scanner;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sneakerfinder.databinding.FragmentScannerProcessingBinding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ScannerProcessingFragment extends Fragment {
    private FragmentScannerProcessingBinding binding;
    private ScannerViewModel scannerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() == null) {
            scannerViewModel = new ViewModelProvider(this).get(ScannerViewModel.class);
        } else {
            scannerViewModel = new ViewModelProvider(getActivity()).get(ScannerViewModel.class);
        }

        binding = FragmentScannerProcessingBinding.inflate(inflater, container, false);

        Bitmap capturePreviewBitmap = scannerViewModel.getCapturePreviewBitmap();

        if (capturePreviewBitmap != null) {
            binding.imageView.setImageBitmap(capturePreviewBitmap);
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}