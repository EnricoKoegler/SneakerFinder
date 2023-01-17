package com.example.sneakerfinder.ui.saved_results;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.sneakerfinder.ProductActivity;
import com.example.sneakerfinder.R;
import com.example.sneakerfinder.databinding.FragmentSavedResultsBinding;
import com.example.sneakerfinder.db.entity.Shoe;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoeScanResults;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SavedResultsFragment extends Fragment implements SimpleAdapter.ItemClickListener{

    private FragmentSavedResultsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SavedResultsViewModel dashboardViewModel =
                new ViewModelProvider(this).get(SavedResultsViewModel.class);

        binding = FragmentSavedResultsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();





        //View view = inflater.inflate(R.layout.fragment_saved_results, container, false);
        // Create an adapter for our list:
        SimpleAdapter adapter = new SimpleAdapter(root.getContext());

        // Set the adapter to be used by the ListView:
        RecyclerView listview = root.findViewById(R.id.shoe_list_view);
        listview.setAdapter(adapter);
        listview.setLayoutManager(new LinearLayoutManager(root.getContext()));

        adapter.setItemClickListener(this);
        //TODO set Arraylist for adapter
        //adapter.setItems(shoes);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClicked(ShoeScanWithShoeScanResults scan) {
        Intent i = new Intent(getActivity(), ProductActivity.class);
        Shoe shoe = scan.getTopResult().shoe;
        i.putExtra("shoe_name", shoe.name);
        i.putExtra("shoe_desc", shoe.description);
        i.putExtra("shoe_image", shoe.thumbnailUrl);
        i.putExtra("shoe_price", shoe.price);
        startActivity(i);
    }
}