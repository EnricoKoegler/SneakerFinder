package com.example.sneakerfinder.ui.main_activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sneakerfinder.R;
import com.example.sneakerfinder.databinding.FragmentHomeBinding;
import com.example.sneakerfinder.databinding.ViewShoeBinding;
import com.example.sneakerfinder.db.entity.Shoe;
import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoeScanResults;
import com.example.sneakerfinder.helper.UIHelper;
import com.example.sneakerfinder.ui.scan_processing.ScanProcessingActivity;
import com.example.sneakerfinder.ui.scan_result.ProductActivity;
import com.example.sneakerfinder.ui.similar_shoes.SimilarShoesActivity;
import com.squareup.picasso.Picasso;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import static android.app.Activity.RESULT_OK;
import static androidx.navigation.fragment.FragmentKt.findNavController;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnCaptureNow.setOnClickListener(this::onCaptureBtnClick);
        binding.btnUploadFromGallery.setOnClickListener(this::onUploadBtnClick);
        binding.furtherRecommendationsDivider.setOnClickListener(this::onRecommendationsClick);
        binding.latestScanResultsDivider.setOnClickListener(this::onLatestScanResultsClick);
        binding.furtherRecommendationsNoItemsBtn.setOnClickListener(this::onCaptureBtnClick);
        binding.latestScanResultsNoItemsBtn.setOnClickListener(this::onCaptureBtnClick);

        viewModel.getShoeScans().observe(requireActivity(), list -> {
            ShoeScanResultWithShoe[] selection = new ShoeScanResultWithShoe[3];
            int count = 0;

            for (ShoeScanWithShoeScanResults scan: list) {
                if (scan.shoeScan.resultQuality == ShoeScan.RESULT_QUALITY_HIGH) {
                    selection[count] = scan.shoeScanResults.get(0);
                    count++;
                    if (count == 3) break;
                }
            }

            if (count == 3) {
                binding.latestScanResultsShoes.getRoot().setVisibility(View.VISIBLE);
                binding.latestScanResultsNoItems.setVisibility(View.GONE);

                setShoeViewData(binding.latestScanResultsShoes.shoeCenter, selection[0]);
                setShoeViewData(binding.latestScanResultsShoes.shoeLeft, selection[1]);
                setShoeViewData(binding.latestScanResultsShoes.shoeRight, selection[2]);
            } else {
                binding.latestScanResultsShoes.getRoot().setVisibility(View.GONE);
                binding.latestScanResultsNoItems.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getRecommendedShoes().observe(requireActivity(), list -> {
            if (list.size() < 2) {
                binding.furtherRecommendationsShoes.getRoot().setVisibility(View.GONE);
                binding.furtherRecommendationsNoItems.setVisibility(View.VISIBLE);
            } else {
                binding.furtherRecommendationsShoes.getRoot().setVisibility(View.VISIBLE);
                binding.furtherRecommendationsNoItems.setVisibility(View.GONE);

                setShoeViewData(binding.furtherRecommendationsShoes.shoeLeft, list.get(0));
                setShoeViewData(binding.furtherRecommendationsShoes.shoeRight, list.get(1));
            }
        });

        return root;
    }

    private void onCaptureBtnClick(View view) {
        findNavController(this).navigate(R.id.action_home_to_scanner);
    }

    private void onUploadBtnClick(View view) {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("image/*");
        photoActivityResultLauncher.launch(fileIntent);
    }

    private final ActivityResultLauncher<Intent> photoActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent resultIntent = result.getData();
                if (result.getResultCode() == RESULT_OK && resultIntent != null && resultIntent.getData() != null) {
                    // data contains content uri
                    Intent i = new Intent(requireActivity(), ScanProcessingActivity.class);
                    i.setData(resultIntent.getData());
                    startActivity(i);
                } else {
                    Context context = requireActivity();
                    UIHelper.showAlertDialog(context, context.getString(R.string.were_sorry), context.getString(R.string.image_could_not_be_read_from_device), null);
                }
            }
    );

    private void onRecommendationsClick(View view) {
        Intent intent = new Intent(requireActivity(), SimilarShoesActivity.class);
        startActivity(intent);
    }

    private void onLatestScanResultsClick(View view) {
        NavController navController = findNavController(this);
        navController.navigate(R.id.action_home_to_saved_results);
    }

    private void setShoeViewData(ViewShoeBinding binding, ShoeScanResultWithShoe result) {
        if (result.shoe != null) {
            Shoe shoe = result.shoe;
            if (shoe.thumbnailUrl != null) Picasso.get().load(shoe.thumbnailUrl).into(binding.shoeImage);

            binding.getRoot().setOnClickListener(view -> launchProductActivity(shoe.shoeId, result.shoeScanResult.shoeScanId));
        }
    }

    private void launchProductActivity(long shoeId, long shoeScanId) {
        Intent i = new Intent(requireActivity(), ProductActivity.class);
        i.putExtra(ProductActivity.EXTRA_SHOE_SCAN_ID, shoeScanId);
        i.putExtra(ProductActivity.EXTRA_SHOE_ID, shoeId);
        startActivity(i);
    }
}