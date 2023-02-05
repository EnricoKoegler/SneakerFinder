package com.example.sneakerfinder.ui.scan_processing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.example.sneakerfinder.R;
import com.example.sneakerfinder.databinding.ActivityScanProcessingBinding;
import com.example.sneakerfinder.ui.scan_result.ProductActivity;
import com.example.sneakerfinder.ui.similar_shoes.SimilarShoesActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class ScanProcessingActivity extends AppCompatActivity {
    private ScanProcessingViewModel viewModel;
    private ActivityScanProcessingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanProcessingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.scanProcessingSimilar.setOnClickListener(view -> {
            Intent intent = new Intent(this, SimilarShoesActivity.class);
            intent.putExtra(ProductActivity.EXTRA_SHOE_SCAN_ID, viewModel.getShoeScanId());
            startActivity(intent);
        });

        binding.scanProcessingRetry.setOnClickListener(view -> finish());

        Intent i = getIntent();
        Uri imageUri = null;
        if (i.getAction() != null && i.getAction().equals(Intent.ACTION_SEND)) {
            imageUri = i.getParcelableExtra(Intent.EXTRA_STREAM);
        } else if (i.getData() != null) {
            imageUri = i.getData();
        } else {
            // TODO: error handling
            Log.e("ScanProcessing", "No URI found");
        }

        viewModel = new ViewModelProvider(this).get(ScanProcessingViewModel.class);

        viewModel.getScanImagePath().observe(this, imageFilePath ->
                Picasso.get().load("file://" + imageFilePath).into(binding.scanProcessingImage));

        viewModel.getRecognitionState().observe(this, recognitionStateObserver);

        if (imageUri != null) {
            try {
                String imageFilePath = createScanFile(imageUri).getAbsolutePath();
                viewModel.recognizeShoe(imageFilePath);
            } catch (IOException e) {
                // TODO: error handling
                Log.e("ScanProcessing", "File could not be copied: " + e.getMessage());
            }
        }
    }

    private final Observer<ScanProcessingViewModel.RecognitionState> recognitionStateObserver = recognitionState -> {
        switch (recognitionState) {
            case ERROR:
                binding.scanProcessingTitle.setText(R.string.were_sorry);
                binding.scanProcessingDescription.setText(R.string.error_occurred);
                binding.scanProcessingBar.setVisibility(View.GONE);
                binding.scanProcessingRetry.setVisibility(View.VISIBLE);
                binding.scanProcessingSimilar.setVisibility(View.GONE);
                break;
            case PROCESSING:
                binding.scanProcessingTitle.setText(R.string.please_wait);
                binding.scanProcessingDescription.setText(R.string.processing_image);
                binding.scanProcessingBar.setVisibility(View.VISIBLE);
                binding.scanProcessingRetry.setVisibility(View.INVISIBLE);
                binding.scanProcessingSimilar.setVisibility(View.INVISIBLE);
                break;
            case NO_RESULT:
                binding.scanProcessingTitle.setText(R.string.were_sorry);
                binding.scanProcessingDescription.setText(R.string.no_results);
                binding.scanProcessingBar.setVisibility(View.GONE);
                binding.scanProcessingRetry.setVisibility(View.VISIBLE);
                binding.scanProcessingSimilar.setVisibility(View.GONE);
                break;
            case LOW_ACCURACY_RESULT:
                binding.scanProcessingTitle.setText(R.string.were_sorry);
                binding.scanProcessingDescription.setText(R.string.no_results_alternatives);
                binding.scanProcessingBar.setVisibility(View.GONE);
                binding.scanProcessingRetry.setVisibility(View.VISIBLE);
                binding.scanProcessingSimilar.setVisibility(View.VISIBLE);
                break;
            case HIGH_ACCURACY_RESULT:
                viewModel.getTopResult().observe(this, result -> {
                    if (result != null) {
                        Intent intent = new Intent(this, ProductActivity.class);
                        intent.putExtra(ProductActivity.EXTRA_SHOE_SCAN_ID, result.shoeScanId);
                        intent.putExtra(ProductActivity.EXTRA_SHOE_ID, result.shoeId);
                        startActivity(intent);
                        finish();
                    } else {
                        viewModel.setRecognitionState(ScanProcessingViewModel.RecognitionState.ERROR);
                    }
                });
                break;
        }
    };

    private File createScanFile(Uri uri) throws IOException {
        String mimeType = getContentResolver().getType(uri);
        // Copy file
        InputStream input = getContentResolver().openInputStream(uri);

        DateFormat dateFormat = new SimpleDateFormat("ddMMyy_HHmmss", Locale.GERMANY);
        String timestamp = dateFormat.format(new Date());

        String fileExtension = "";
        if (uri.getPath() != null) {
            fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }
        String filename = "SneakerFinderScan_" + timestamp + "." + fileExtension;

        File copyFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);

        FileOutputStream output = new FileOutputStream(copyFile);

        if (input != null) {
            copy(input, output);
        } else {
            throw new IOException("Failed to open InputStream");
        }

        Log.d("MediaSelectPath", copyFile.getAbsolutePath());

        return copyFile;
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        int read;
        byte[] bytes = new byte[4096];

        while ((read = in.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
    }
}
