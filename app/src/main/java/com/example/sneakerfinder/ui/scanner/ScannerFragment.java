package com.example.sneakerfinder.ui.scanner;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sneakerfinder.ProductActivity;
import com.example.sneakerfinder.R;
import com.example.sneakerfinder.databinding.FragmentScannerBinding;
import com.example.sneakerfinder.db.entity.Shoe;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoeScanResults;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import static androidx.navigation.fragment.FragmentKt.findNavController;

public class ScannerFragment extends Fragment {
    private FragmentScannerBinding binding;
    private ScannerViewModel scannerViewModel;

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
            Uri photoUri = FileProvider.getUriForFile(
                    requireActivity(),
                    "com.example.sneakerfinder.fileprovider",
                    photoFile
            );
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            scannerViewModel.setPhotoCaptureInProgress(true);
            scannerViewModel.startShoeScan(photoFile.getAbsolutePath());
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

                    Intent data = result.getData();
                    if (data != null) {
                        findNavController(ScannerFragment.this).navigate(R.id.action_scanner_to_scanner_processing);
                        scannerViewModel.shoeScanFinished(new ScannerViewModel.ShoeRecognitionCallback() {
                            @Override
                            public void onRecognitionComplete(ShoeScanWithShoeScanResults scanResults) {
                                findNavController(ScannerFragment.this).navigate(R.id.action_scanner_processing_to_home);
                                Intent i = new Intent(getActivity(), ProductActivity.class);
                                ShoeScanResultWithShoe resultWithShoe = scanResults.getTopResult();
                                Shoe shoe = resultWithShoe.shoe;
                                i.putExtra("shoe_name", shoe.name);
                                i.putExtra("shoe_desc", "");
                                i.putExtra("shoe_image", shoe.thumbnailUrl);
                                i.putExtra("shoe_price", shoe.price);
                                i.putExtra("shoe_acc", resultWithShoe.shoeScanResult.confidence);
                                startActivity(i);
                            }

                            @Override
                            public void onError() {

                            }
                        });
                    } else {
                        findNavController(ScannerFragment.this).navigate(R.id.action_scanner_to_home);
                    }
                }
            });

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}