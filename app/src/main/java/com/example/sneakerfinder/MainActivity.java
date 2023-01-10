package com.example.sneakerfinder;

import android.os.Bundle;

import com.example.sneakerfinder.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.navView;

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_saved_results, R.id.navigation_scanner)
                .build();
        NavigationUI.setupWithNavController(navView, navController);
    }

}