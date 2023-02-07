package com.example.sneakerfinder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sneakerfinder.R;
import com.example.sneakerfinder.ui.main_activity.MainActivity;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        findViewById(R.id.landingpage_button).setOnClickListener(this);
        findViewById(R.id.landingpage_iv).setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}