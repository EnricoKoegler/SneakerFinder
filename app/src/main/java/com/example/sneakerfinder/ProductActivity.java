package com.example.sneakerfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

public class ProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Get data from intent
        Intent i = getIntent();
        String imagePath = i.getStringExtra("shoe_image");
        String name = i.getStringExtra("shoe_name");
        String description = i.getStringExtra("shoe_desc");
        String price = i.getStringExtra("shoe_price");
        float accuracy = i.getFloatExtra("shoe_acc", 1.f);

        // Set data to views
        ImageView iv = findViewById(R.id.productdetail_image);
        if (imagePath != null) Picasso.get().load(imagePath).into(iv);

        TextView tv0 = findViewById(R.id.productdetail_name);
        if (name != null) tv0.setText(name);

        TextView tv1 = findViewById(R.id.productdetail_description);
        if (description != null) tv1.setText(description);

        TextView tv2 = findViewById(R.id.productdetail_price);
        if (price != null) tv2.setText(price);

        TextView tv3 = findViewById(R.id.productdetail_accuracy);
        tv3.setText(String.format("%.0f%%", accuracy * 100));

        View buyOnlineBtn = findViewById(R.id.btn_buy_online);
        buyOnlineBtn.setOnClickListener(view -> {
            String url = "https://www.amazon.de/s?k=" + name;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        ImageView back_button = findViewById(R.id.productdetail_back);
        back_button.setOnClickListener(view -> finish());
    }
}