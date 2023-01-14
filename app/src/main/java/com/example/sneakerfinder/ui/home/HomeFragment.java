package com.example.sneakerfinder.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sneakerfinder.R;
import com.example.sneakerfinder.databinding.FragmentHomeBinding;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import static androidx.navigation.fragment.FragmentKt.findNavController;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnCaptureNow.setOnClickListener(this::onCaptureBtnClick);

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

}