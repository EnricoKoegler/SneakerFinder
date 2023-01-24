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
import com.example.sneakerfinder.db.entity.ShoeScanWithShoes;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SavedResultsFragment extends Fragment implements SimpleAdapter.ItemClickListener{

    private FragmentSavedResultsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SavedResultsViewModel savedResultsViewModel =
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

        savedResultsViewModel.getShoeScans().observe(getViewLifecycleOwner(), adapter::setItems);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClicked(ShoeScanWithShoes scan) {
        Intent i = new Intent(getActivity(), ProductActivity.class);
        //TODO send the correct shoeID
        i.putExtra("shoeID", 0);
        startActivity(i);
    }
}