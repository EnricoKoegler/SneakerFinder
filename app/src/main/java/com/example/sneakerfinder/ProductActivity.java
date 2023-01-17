package com.example.sneakerfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        //get data from intent
        try {
            ImageView iv = findViewById(R.id.productdetail_image);
            iv.setImageResource(getIntent().getIntExtra("shoe_image", R.drawable.example_shoe3));
            TextView tv0 = findViewById(R.id.productdetail_name);
            tv0.setText(getIntent().getStringExtra("shoe_name"));
            TextView tv1 = findViewById(R.id.productdetail_description);
            tv1.setText(getIntent().getStringExtra("shoe_desc"));
            findViewById(R.id.productdetail_accuracy);
            TextView tv2 = findViewById(R.id.productdetail_price);
            tv2.setText("$ " + String.valueOf(getIntent().getIntExtra("shoe_price", 0)));
            TextView tv3 = findViewById(R.id.productdetail_accuracy);
            tv3.setText("80%");
        } catch (Exception e) {}


        ImageView back_button = findViewById(R.id.productdetail_back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProductActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}