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

    boolean displayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Get data from intent
        Intent i = getIntent();
        int shoeID = i.getIntExtra("shoeID", 0);
        //TODO get correct values
        String imagePath = "https://static.nike.com/a/images/t_PDP_864_v1/f_auto,b_rgb:f5f5f5/350e7f3a-979a-402b-9396-a8a998dd76ab/air-force-1-07-herrenschuh-Pbt0tj.png";
        String brandimage = "https://upload.wikimedia.org/wikipedia/commons/3/36/Logo_nike_principal.jpg";
        String name = "Nike Air Force 1";
        String description = "Desc";
        String imageTaken = "https://static.nike.com/a/images/t_PDP_864_v1/f_auto,b_rgb:f5f5f5/354e209b-f9f2-42c4-9130-d57f49bd77c0/air-force-1-07-herrenschuh-Pbt0tj.png";
        String releaseDate = "01/01/1990";
        double price = 199.99d;
        float accuracy = 0.7f;

        // Set data to views
        ImageView iv = findViewById(R.id.productdetail_image);
        if (imagePath != null) Picasso.get().load(imagePath).into(iv);

        ImageView iv2 = findViewById(R.id.productdetail_brandimage);
        if (brandimage!= null) Picasso.get().load(brandimage).into(iv2);

        TextView tv0 = findViewById(R.id.productdetail_name);
        if (name != null) tv0.setText(name);

        TextView tv1 = findViewById(R.id.productdetail_description);
        if (description != null) tv1.setText(description);

        TextView tv2 = findViewById(R.id.productdetail_price);
        tv2.setText("€" + String.valueOf(price));

        TextView tv3 = findViewById(R.id.productdetail_accuracy);
        tv3.setText(String.format("%.0f%%", accuracy * 100));

        TextView tv4 = findViewById(R.id.productdetail_release_date);
        tv4.setText(releaseDate);

        iv.setOnClickListener(view -> {
            if(displayed){
                Picasso.get().load(imagePath).into(iv);
            }
            else{
                Picasso.get().load(imageTaken).into(iv);
            }
            displayed = !displayed;
        });

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