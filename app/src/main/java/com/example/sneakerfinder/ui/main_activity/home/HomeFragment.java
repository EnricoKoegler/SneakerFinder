package com.example.sneakerfinder.ui.main_activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sneakerfinder.R;
import com.example.sneakerfinder.databinding.FragmentHomeBinding;
import com.example.sneakerfinder.ui.scan_processing.ScanProcessingActivity;
import com.example.sneakerfinder.ui.similar_shoes.SimilarShoesActivity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import static android.app.Activity.RESULT_OK;
import static androidx.navigation.fragment.FragmentKt.findNavController;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnCaptureNow.setOnClickListener(this::onCaptureBtnClick);
        binding.btnUploadFromGallery.setOnClickListener(this::onUploadBtnClick);
        binding.furtherRecommendationsDivider.setOnClickListener(this::onRecommendationsClick);
        binding.latestScanResultsDivider.setOnClickListener(this::onLatestScanResultsClick);

        getParentFragmentManager().beginTransaction().
                add(R.id.fragement_placeholder0, ShoeFragment.newInstance("A", "Model", R.drawable.example_shoe4, 100)).
                add(R.id.fragement_placeholder1, ShoeFragment.newInstance("B", "Model", R.drawable.example_shoe3, 100)).
                add(R.id.fragement_placeholder2, ShoeFragment.newInstance("C", "Model", R.drawable.example_shoe4, 100)).
                add(R.id.fragement_placeholder3, ShoeFragment.newInstance("D", "Model", R.drawable.example_shoe4, 100)).
                add(R.id.fragement_placeholder4, ShoeFragment.newInstance("E", "Model", R.drawable.example_shoe3, 100)).
                commit();

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

    private void onRecommendationsClick(View view) {
        Intent intent = new Intent(requireActivity(), SimilarShoesActivity.class);
        startActivity(intent);
    }

    private void onLatestScanResultsClick(View view) {
        NavController navController = findNavController(this);
        navController.navigate(R.id.action_home_to_saved_results);
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
                    // TODO: error handling
                }
            }
    );
}