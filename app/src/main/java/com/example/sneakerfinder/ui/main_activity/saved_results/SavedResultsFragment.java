package com.example.sneakerfinder.ui.main_activity.saved_results;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sneakerfinder.R;
import com.example.sneakerfinder.databinding.FragmentSavedResultsBinding;
import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoeScanResults;
import com.example.sneakerfinder.helper.UIHelper;
import com.example.sneakerfinder.ui.scan_processing.ScanProcessingActivity;
import com.example.sneakerfinder.ui.scan_result.ProductActivity;
import com.example.sneakerfinder.ui.similar_shoes.SimilarShoesActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.navigation.fragment.FragmentKt.findNavController;

public class SavedResultsFragment extends Fragment implements SavedResultsAdapter.ItemClickListener{

    private FragmentSavedResultsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SavedResultsViewModel savedResultsViewModel =
                new ViewModelProvider(this).get(SavedResultsViewModel.class);

        binding = FragmentSavedResultsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Create an adapter for our list:
        SavedResultsAdapter adapter = new SavedResultsAdapter(root.getContext());

        // Set the adapter to be used by the ListView:
        RecyclerView listview = root.findViewById(R.id.shoe_list_view);
        listview.setAdapter(adapter);
        listview.setLayoutManager(new LinearLayoutManager(root.getContext()));

        adapter.setItemClickListener(this);

        savedResultsViewModel.getShoeScans().observe(getViewLifecycleOwner(), shoeScanWithShoeScanResults -> {
            if (shoeScanWithShoeScanResults.size() == 0) {
                binding.latestScanResultsNoItems.setVisibility(View.VISIBLE);
            } else {
                binding.latestScanResultsNoItems.setVisibility(View.GONE);
            }
            adapter.setItems(shoeScanWithShoeScanResults);
        });

        binding.latestScanResultsNoItemsBtn.setOnClickListener(this::onCaptureBtnClick);

        return root;
    }

    private void onCaptureBtnClick(View view) {
        findNavController(this).navigate(R.id.action_saved_results_to_scanner);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClicked(ShoeScanWithShoeScanResults scan) {
        switch (scan.shoeScan.resultQuality) {
            case ShoeScan.RESULT_QUALITY_ERROR:
                Context context = requireActivity();
                UIHelper.showAlertDialog(context,
                        "Error during recognition",
                        "This shoe was not recognized before because a problem occurred. We will try to recognize the shoe now.",
                        (dialogInterface, i) -> {
                            Intent intent = new Intent(requireActivity(), ScanProcessingActivity.class);
                            intent.putExtra(ScanProcessingActivity.EXTRA_RETRY_SHOE_SCAN_ID, scan.shoeScan.shoeScanId);
                            startActivity(intent);
                        });
                break;
            case ShoeScan.RESULT_QUALITY_NO_RESULT:
                break;
            case ShoeScan.RESULT_QUALITY_LOW:
                Intent intent = new Intent(requireActivity(), SimilarShoesActivity.class);
                intent.putExtra(ProductActivity.EXTRA_SHOE_SCAN_ID, scan.shoeScan.shoeScanId);
                startActivity(intent);
                break;
            case ShoeScan.RESULT_QUALITY_HIGH:
                Intent i = new Intent(getActivity(), ProductActivity.class);
                i.putExtra(ProductActivity.EXTRA_SHOE_SCAN_ID, scan.shoeScan.shoeScanId);
                i.putExtra(ProductActivity.EXTRA_SHOE_ID, scan.shoeScanResults.get(0).shoe.shoeId);
                startActivity(i);
                break;
        }
    }
}