package com.example.sneakerfinder.ui.scan_processing;

import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.example.sneakerfinder.R;
import com.example.sneakerfinder.databinding.ActivityScanProcessingBinding;
import com.example.sneakerfinder.databinding.ViewShoeBinding;
import com.example.sneakerfinder.db.entity.Shoe;
import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
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
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class ScanProcessingActivity extends AppCompatActivity {
    private ScanProcessingViewModel viewModel;
    private ActivityScanProcessingBinding binding;

    public static final String EXTRA_RETRY_SHOE_SCAN_ID = "EXTRA_RETRY_SHOE_SCAN_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanProcessingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        Uri imageUri = null;
        Long previousShoeScanId = null;
        if (i.getAction() != null && i.getAction().equals(Intent.ACTION_SEND)) {
            imageUri = i.getParcelableExtra(Intent.EXTRA_STREAM);
        } else if (i.getData() != null) {
            imageUri = i.getData();
        } else if (i.getLongExtra("EXTRA_RETRY_SHOE_SCAN_ID", -1) != -1) {
            previousShoeScanId = i.getLongExtra("EXTRA_RETRY_SHOE_SCAN_ID", -1);
        } else {
            Log.e("ScanProcessing", "No URI found");
            finish();
        }

        viewModel = new ViewModelProvider(this).get(ScanProcessingViewModel.class);

        viewModel.getCurrentShoeScan().observe(this, recognitionStateObserver);

        if (imageUri != null) {
            try {
                String imageFilePath = createScanFile(imageUri).getAbsolutePath();
                viewModel.recognizeShoe(imageFilePath);
            } catch (IOException e) {
                Log.e("ScanProcessing", "File could not be copied: " + e.getMessage());
                finish();
            }
        } else if (previousShoeScanId != null) {
            viewModel.retryShoeScan(previousShoeScanId);
        }
    }

    private boolean productActivityAlreadyLaunched = false;

    private final Observer<ShoeScan> recognitionStateObserver = shoeScan -> {
        Picasso.get().load("file://" + shoeScan.scanImageFilePath).into(binding.scanProcessingImage);

        if (shoeScan.resultQuality != ShoeScan.RESULT_QUALITY_LOW) {
            binding.scanProcessingSimilarPreview.getRoot().setVisibility(View.GONE);
        }

        if (shoeScan.resultQuality != ShoeScan.RESULT_QUALITY_PROCESSING) {
            binding.searchAnim.setVisibility(View.GONE);
        }

        switch (shoeScan.resultQuality) {
            case ShoeScan.RESULT_QUALITY_ERROR:
                binding.scanProcessingTitle.setText(R.string.were_sorry);
                binding.scanProcessingDescription.setText(R.string.error_occurred);
                binding.scanProcessingBar.setVisibility(View.GONE);
                binding.scanProcessingRetry.setVisibility(View.VISIBLE);
                binding.scanProcessingSimilar.setVisibility(View.GONE);

                binding.scanProcessingRetry.setOnClickListener(view -> viewModel.retryShoeScan(shoeScan.shoeScanId));
                break;
            case ShoeScan.RESULT_QUALITY_PROCESSING:
                binding.scanProcessingTitle.setText(R.string.please_wait);
                binding.scanProcessingDescription.setText(R.string.processing_image);
                binding.scanProcessingBar.setVisibility(View.VISIBLE);
                binding.scanProcessingRetry.setVisibility(View.INVISIBLE);
                binding.scanProcessingSimilar.setVisibility(View.INVISIBLE);

                binding.searchAnim.setVisibility(View.VISIBLE);
                binding.searchAnim.setImageResource(R.drawable.search_anim);
                AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) binding.searchAnim.getDrawable();

                drawable.start();

                break;
            case ShoeScan.RESULT_QUALITY_NO_RESULT:
                binding.scanProcessingTitle.setText(R.string.were_sorry);
                binding.scanProcessingDescription.setText(R.string.no_results);
                binding.scanProcessingBar.setVisibility(View.GONE);
                binding.scanProcessingRetry.setVisibility(View.VISIBLE);
                binding.scanProcessingSimilar.setVisibility(View.GONE);

                binding.scanProcessingRetry.setOnClickListener(view -> finish());
                break;
            case ShoeScan.RESULT_QUALITY_LOW:
                binding.scanProcessingTitle.setText(R.string.were_sorry);
                binding.scanProcessingDescription.setText(R.string.no_results_alternatives);
                binding.scanProcessingBar.setVisibility(View.GONE);
                binding.scanProcessingRetry.setVisibility(View.VISIBLE);
                binding.scanProcessingSimilar.setVisibility(View.VISIBLE);

                View.OnClickListener listener = view -> {
                    Intent intent = new Intent(this, SimilarShoesActivity.class);
                    intent.putExtra(ProductActivity.EXTRA_SHOE_SCAN_ID, shoeScan.shoeScanId);
                    startActivity(intent);
                };

                viewModel.getSimilarShoes(shoeScan.shoeScanId).observe(this, list -> {
                    if (list.size() > 2 && shoeScan.resultQuality == ShoeScan.RESULT_QUALITY_LOW) {
                        binding.scanProcessingSimilarPreview.getRoot().setVisibility(View.VISIBLE);
                        binding.scanProcessingSimilarPreview.getRoot().setOnClickListener(listener);

                        setShoeViewData(binding.scanProcessingSimilarPreview.shoeCenter, list.get(0));
                        setShoeViewData(binding.scanProcessingSimilarPreview.shoeLeft, list.get(1));
                        setShoeViewData(binding.scanProcessingSimilarPreview.shoeRight,list.get(2));
                    } else {
                        binding.scanProcessingSimilarPreview.getRoot().setVisibility(View.GONE);
                    }
                });

                binding.scanProcessingRetry.setOnClickListener(view -> finish());
                binding.scanProcessingSimilar.setOnClickListener(listener);
                break;
            case ShoeScan.RESULT_QUALITY_HIGH:
                if (productActivityAlreadyLaunched) return;

                viewModel.getTopResult(shoeScan.shoeScanId).observe(this, result -> {
                    if (productActivityAlreadyLaunched) return;
                    if (result != null) {
                        Intent intent = new Intent(this, ProductActivity.class);
                        intent.putExtra(ProductActivity.EXTRA_SHOE_SCAN_ID, result.shoeScanId);
                        intent.putExtra(ProductActivity.EXTRA_SHOE_ID, result.shoeId);
                        productActivityAlreadyLaunched = true;
                        startActivity(intent);
                        finish();
                    }
                });
                break;
        }
    };

    private void setShoeViewData(ViewShoeBinding binding, ShoeScanResultWithShoe result) {
        if (result.shoe != null) {
            Shoe shoe = result.shoe;
            if (shoe.thumbnailUrl != null) Picasso.get().load(shoe.thumbnailUrl).into(binding.shoeImage);
        }
    }

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
