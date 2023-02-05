package com.example.sneakerfinder.ui.scan_result;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.sneakerfinder.databinding.ActivityProductBinding;
import com.example.sneakerfinder.db.entity.Shoe;
import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanResult;
import com.example.sneakerfinder.ui.similar_shoes.SimilarShoesActivity;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class ProductActivity extends AppCompatActivity {
    public static final String EXTRA_SHOE_SCAN_ID = "EXTRA_SHOE_SCAN_ID";
    public static final String EXTRA_SHOE_ID = "EXTRA_SHOE_ID";

    private ScanResultViewModel scanResultViewModel;
    private String onlineStoreUrl;
    private ActivityProductBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.productdetailImageBack.setVisibility(View.GONE);

        // Get data from intent
        Intent i = getIntent();
        long shoeScanId = i.getLongExtra(EXTRA_SHOE_SCAN_ID, -1);
        long shoeId = i.getLongExtra(EXTRA_SHOE_ID, -1);

        if (shoeScanId == -1 || shoeId == -1) {
            Log.e("IntentError", "Missing intent extras");
        }

        scanResultViewModel = new ViewModelProvider(
                this,
                new ScanResultViewModel.Factory(getApplication(), shoeScanId, shoeId)
        ).get(ScanResultViewModel.class);

        scanResultViewModel.getShoeScanResult().observe(this, shoeScanResultWithShoeAndScan -> {
            Shoe shoe = shoeScanResultWithShoeAndScan.shoe;
            if (shoe != null) {
                if (shoe.name != null)
                    binding.productdetailName.setText(shoe.name);
                if (shoe.onlineStoreUrl != null)
                    this.onlineStoreUrl = shoe.onlineStoreUrl;
                if (shoe.releaseDate != null)
                    binding.productdetailReleaseDate.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(shoe.releaseDate));
                if (shoe.price != null)
                    binding.productdetailPrice.setText(shoe.price);
                if (shoe.description != null)
                    binding.productdetailDescription.setText(shoe.description);
                if (shoe.thumbnailUrl != null)
                    Picasso.get().load(shoe.thumbnailUrl).into(binding.productdetailImage);

                if (shoe.onlineStoreUrl == null && shoe.name != null)
                    onlineStoreUrl = "https://www.amazon.de/s?k=" + shoe.name;
                else if (shoe.onlineStoreUrl != null) {
                    onlineStoreUrl = shoe.onlineStoreUrl;
                }
            }

            ShoeScan scan = shoeScanResultWithShoeAndScan.shoeScan;
            if (scan != null) {
                if (scan.scanImageFilePath != null)
                    Picasso.get().load("file://" + scan.scanImageFilePath).into(binding.productdetailImageBack);
            }

            ShoeScanResult result = shoeScanResultWithShoeAndScan.shoeScanResult;
            if (result != null) {
                binding.productdetailAccuracy.setText(String.format("%.0f%%", result.confidence * 100));
            }
        });

        binding.productdetailImage.setOnClickListener(view -> {
                binding.productdetailImage.setVisibility(View.GONE);
                binding.productdetailImageBack.setVisibility(View.VISIBLE);
        });

        binding.productdetailImageBack.setOnClickListener(view -> {
            binding.productdetailImage.setVisibility(View.VISIBLE);
            binding.productdetailImageBack.setVisibility(View.GONE);
        });

        binding.btnSimilarShoes.setOnClickListener(view -> {
            Intent intent = new Intent(this, SimilarShoesActivity.class);
            intent.putExtra(ProductActivity.EXTRA_SHOE_SCAN_ID, shoeScanId);
            startActivity(intent);
        });

        binding.btnBuyOnline.setOnClickListener(view -> {
            if (onlineStoreUrl != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(onlineStoreUrl));
                startActivity(intent);
            }
        });

        binding.productdetailBack.setOnClickListener(view -> finish());
    }
}