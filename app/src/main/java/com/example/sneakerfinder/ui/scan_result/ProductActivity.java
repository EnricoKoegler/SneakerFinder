package com.example.sneakerfinder.ui.scan_result;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.sneakerfinder.R;
import com.example.sneakerfinder.databinding.ActivityProductBinding;
import com.example.sneakerfinder.db.entity.Shoe;
import com.example.sneakerfinder.db.entity.ShoeScanResult;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class ProductActivity extends AppCompatActivity {
    public static final String EXTRA_SHOE_SCAN_ID = "EXTRA_SHOE_SCAN_ID";

    private ScanResultViewModel scanResultViewModel;
    private String onlineStoreUrl;
    private ActivityProductBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.productdetailImageBack.setVisibility(View.GONE);

        scanResultViewModel = new ViewModelProvider(this).get(ScanResultViewModel.class);

        // Get data from intent
        Intent i = getIntent();
        long shoeId = i.getLongExtra(EXTRA_SHOE_SCAN_ID, -1);

        if (shoeId != -1) scanResultViewModel.setScanId(shoeId);

        scanResultViewModel.getShoeScan().observe(this, scan -> {
            if (scan.scanImageFilePath != null)
                Picasso.get().load("file://" + scan.scanImageFilePath).into(binding.productdetailImageBack);
        });

        scanResultViewModel.getShoeScanResults().observe(this, shoeScanResultWithShoes -> {
            ShoeScanResultWithShoe shoeScanResultWithShoe = shoeScanResultWithShoes.get(0); // Get top result

            if (shoeScanResultWithShoe != null) {
                ShoeScanResult result = shoeScanResultWithShoe.shoeScanResult;
                if (result != null) {
                    binding.productdetailAccuracy.setText(String.format("%.0f%%", result.confidence * 100));
                }

                Shoe shoe = shoeScanResultWithShoe.shoe;
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
                }
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

        View buyOnlineBtn = findViewById(R.id.btn_buy_online);
        buyOnlineBtn.setOnClickListener(view -> {
            List<ShoeScanResultWithShoe> resultList = scanResultViewModel.getShoeScanResults().getValue();
            if (resultList != null && resultList.get(0) != null) {
                Shoe shoe = resultList.get(0).shoe;
                if (shoe != null) {
                    String url = null;
                    if (shoe.onlineStoreUrl == null && shoe.name != null)
                        url = "https://www.amazon.de/s?k=" + shoe.name;
                    else if (shoe.onlineStoreUrl != null) {
                        url = shoe.onlineStoreUrl;
                    }
                    if (url != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                }
            }
        });

        ImageView back_button = findViewById(R.id.productdetail_back);
        back_button.setOnClickListener(view -> finish());
    }
}