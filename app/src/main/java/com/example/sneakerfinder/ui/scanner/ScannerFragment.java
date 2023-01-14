package com.example.sneakerfinder.ui.scanner;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sneakerfinder.R;
import com.example.sneakerfinder.databinding.FragmentScannerBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        if (getActivity() == null) {
            scannerViewModel = new ViewModelProvider(this).get(ScannerViewModel.class);
        } else {
            scannerViewModel = new ViewModelProvider(getActivity()).get(ScannerViewModel.class);
        }

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
            scannerViewModel.setPhotoCaptureInProgress(true);
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
                    scannerViewModel.setPhotoCaptureInProgress(false);

                    Intent data = result.getData();
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        scannerViewModel.setCapturePreviewBitmap((Bitmap) extras.get("data"));
                        findNavController(ScannerFragment.this).navigate(R.id.action_scanner_to_scanner_processing);
                    } else {
                        findNavController(ScannerFragment.this).navigate(R.id.action_scanner_to_home);
                    }
                }
            });

    private File createImageFile() throws IOException, ActivityNotFoundException {
        if (getActivity() == null) throw new ActivityNotFoundException();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}