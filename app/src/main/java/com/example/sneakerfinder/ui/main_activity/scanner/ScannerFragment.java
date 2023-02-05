package com.example.sneakerfinder.ui.main_activity.scanner;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sneakerfinder.databinding.FragmentScannerBinding;
import com.example.sneakerfinder.ui.scan_processing.ScanProcessingActivity;

import java.io.File;
import java.io.IOException;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import static android.app.Activity.RESULT_OK;
import static androidx.navigation.fragment.FragmentKt.findNavController;

public class ScannerFragment extends Fragment {
    private FragmentScannerBinding binding;
    private ScannerViewModel scannerViewModel;

    private Uri photoUri;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        scannerViewModel = new ViewModelProvider(requireActivity()).get(ScannerViewModel.class);

        binding = FragmentScannerBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!scannerViewModel.isPhotoCaptureInProgress()) capturePhoto();
    }

    private void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            // Create the File for the photo
            File photoFile = createImageFile();

            // Put photoUri as extra for the Intent
            photoUri = FileProvider.getUriForFile(
                    requireActivity(),
                    "com.example.sneakerfinder.fileprovider",
                    photoFile
            );
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            scannerViewModel.setPhotoCaptureInProgress(true);
            capturePhotoResult.launch(takePictureIntent);
        } catch (ActivityNotFoundException e) {
            // TODO: display error state to the user
        } catch (IOException e) {
            // TODO: display error state to the user
        }
    }

    ActivityResultLauncher<Intent> capturePhotoResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    scannerViewModel.setPhotoCaptureInProgress(false);

                    if (result.getResultCode() == RESULT_OK) {
                        Intent i = new Intent(getActivity(), ScanProcessingActivity.class);
                        i.setData(photoUri);
                        startActivity(i);
                    } else {
                        findNavController(ScannerFragment.this).navigateUp();
                    }
                }
            });

    private File createImageFile() throws IOException {
        File storageDir = requireActivity().getExternalCacheDir();

        return File.createTempFile(
                "SneakerFinderCameraScan",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}