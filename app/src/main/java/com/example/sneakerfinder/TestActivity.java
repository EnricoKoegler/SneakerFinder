package com.example.sneakerfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.sneakerfinder.data.Shoe;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        getSupportFragmentManager().beginTransaction().
                add(R.id.test_placeholder1, new ShoeFragment()).
                add(R.id.test_placeholder2, new ShoeFragment()).
                commit();
    }
}