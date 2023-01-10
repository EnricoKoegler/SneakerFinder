package com.example.sneakerfinder.ui.scanner;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sneakerfinder.R;
import com.example.sneakerfinder.databinding.FragmentScannerBinding;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import static androidx.navigation.fragment.FragmentKt.findNavController;

public class ScannerFragment extends Fragment {
    private FragmentScannerBinding binding;
    private ScannerViewModel scannerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        scannerViewModel = new ViewModelProvider(this).get(ScannerViewModel.class);

        binding = FragmentScannerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        scannerViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        findNavController(this).addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if (navDestination.getId() == R.id.navigation_scanner) capturePhoto();
        });

        return root;
    }

    private void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            capturePhotoResult.launch(takePictureIntent);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    ActivityResultLauncher<Intent> capturePhotoResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                }
            });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}