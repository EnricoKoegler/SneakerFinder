package com.example.sneakerfinder.ui.similar_shoes;

import android.content.Intent;
import android.os.Bundle;

import com.example.sneakerfinder.R;
import com.example.sneakerfinder.databinding.FragmentSavedResultsBinding;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.example.sneakerfinder.ui.main_activity.saved_results.SavedResultsAdapter;
import com.example.sneakerfinder.ui.scan_result.ProductActivity;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SimilarShoesActivity extends AppCompatActivity implements SimilarShoesAdapter.ItemClickListener {
    public static final String EXTRA_SHOE_SCAN_ID = "EXTRA_SHOE_SCAN_ID";
    private FragmentSavedResultsBinding binding;

    private SimilarShoesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentSavedResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // If shoeScanId == null -> activity type = recommended shoes
        // else -> activity type = similar shoes
        Long shoeScanId = getIntent().getLongExtra(EXTRA_SHOE_SCAN_ID, -1);
        if (shoeScanId == -1) shoeScanId = null;

        if (shoeScanId == null) binding.savedResultsTitle.setText(R.string.recommended_shoes);
        else binding.savedResultsTitle.setText(R.string.similar_shoes);

        SimilarShoesAdapter adapter = new SimilarShoesAdapter(this);
        binding.shoeListView.setAdapter(adapter);
        binding.shoeListView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setItemClickListener(this);

        viewModel = new ViewModelProvider(
                this,
                new SimilarShoesViewModel.Factory(getApplication(), shoeScanId)
        ).get(SimilarShoesViewModel.class);

        viewModel.getSimilarShoes().observe(this, adapter::setItems);
    }

    @Override
    public void onItemClicked(ShoeScanResultWithShoe shoe) {
        Intent i = new Intent(this, ProductActivity.class);
        i.putExtra(ProductActivity.EXTRA_SHOE_SCAN_ID, shoe.shoeScanResult.shoeScanId);
        i.putExtra(ProductActivity.EXTRA_SHOE_ID, shoe.shoe.shoeId);
        startActivity(i);
    }
}
