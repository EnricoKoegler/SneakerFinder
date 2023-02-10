package com.example.sneakerfinder.ui.similar_shoes;

import android.content.Intent;
import android.os.Bundle;

import com.example.sneakerfinder.R;
import com.example.sneakerfinder.databinding.FragmentSavedResultsBinding;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.example.sneakerfinder.ui.scan_result.ProductActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * This activity is used to display similar shoes AND shoe recommendations.
 * If EXTRA_SHOE_SCAN_ID is given, similar shoes to the given shoe scan are displayed.
 * Otherwise general recommendations are displayed.
 */
public class SimilarShoesActivity extends AppCompatActivity implements SimilarShoesAdapter.ItemClickListener {
    public static final String EXTRA_SHOE_SCAN_ID = "EXTRA_SHOE_SCAN_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.sneakerfinder.databinding.FragmentSavedResultsBinding binding = FragmentSavedResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Long shoeScanId = getIntent().getLongExtra(EXTRA_SHOE_SCAN_ID, -1);
        if (shoeScanId == -1) shoeScanId = null;

        // EXTRA_SHOE_SCAN_ID determines the style of the activity
        ActivityStyle activityStyle;
        if (shoeScanId == null) activityStyle = ActivityStyle.RECOMMENDED_SHOES;
        else activityStyle = ActivityStyle.SIMILAR_SHOES;

        if (activityStyle == ActivityStyle.RECOMMENDED_SHOES) binding.savedResultsTitle.setText(R.string.recommended_shoes);
        else binding.savedResultsTitle.setText(R.string.similar_shoes);

        SimilarShoesAdapter adapter = new SimilarShoesAdapter(this, activityStyle);
        binding.shoeListView.setAdapter(adapter);
        binding.shoeListView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setItemClickListener(this);

        SimilarShoesViewModel viewModel = new ViewModelProvider(
                this,
                new SimilarShoesViewModel.Factory(getApplication(), shoeScanId)
        ).get(SimilarShoesViewModel.class);

        viewModel.getSimilarShoes().observe(this, adapter::setItems);
    }

    /**
     * @param shoe for which the ProductActivity should be launched
     */
    @Override
    public void onItemClicked(ShoeScanResultWithShoe shoe) {
        Intent i = new Intent(this, ProductActivity.class);
        i.putExtra(ProductActivity.EXTRA_SHOE_SCAN_ID, shoe.shoeScanResult.shoeScanId);
        i.putExtra(ProductActivity.EXTRA_SHOE_ID, shoe.shoe.shoeId);
        i.putExtra(ProductActivity.EXTRA_SHOW_SIMILAR_SHOES, false);
        startActivity(i);
    }

    enum ActivityStyle {RECOMMENDED_SHOES, SIMILAR_SHOES}
}
