package com.example.sneakerfinder.ui.main_activity.home;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sneakerfinder.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BRAND = "brand";
    private static final String ARG_MODEL = "model";
    private static final String ARG_IMGID = "imageID";
    private static final String ARG_PRICE = "price";

    // TODO: Rename and change types of parameters
    private String brand;
    private String model;
    private int imageId;
    private int price;

    TextView tv_brand, tv_model, tv_price;
    ImageView iv_shoe;

    public ShoeFragment() {
    }

    public static ShoeFragment newInstance(String brand, String model, int imageId, int price) {
        ShoeFragment fragment = new ShoeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BRAND, brand);
        args.putString(ARG_MODEL, model);
        args.putInt(ARG_IMGID, imageId);
        args.putInt(ARG_PRICE, price);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        brand = ""; model = ""; imageId = R.drawable.example_shoe; price = 0;
        if (getArguments() != null) {
            brand = getArguments().getString(ARG_BRAND);
            model = getArguments().getString(ARG_MODEL);
            imageId = getArguments().getInt(ARG_IMGID);
            price = getArguments().getInt(ARG_PRICE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LayoutInflater lf = getActivity().getLayoutInflater();
        View view =  lf.inflate(R.layout.fragment_shoe, container, false); //pass the correct layout name for the fragment

        tv_brand = view.findViewById(R.id.fragment_brand);
        tv_model = view.findViewById(R.id.fragment_model);
        iv_shoe = view.findViewById(R.id.fragment_imageID);
        tv_price = view.findViewById(R.id.fragment_price);

        tv_brand.setText(brand);
        tv_model.setText(model);
        iv_shoe.setImageResource(imageId);
        tv_price.setText("$ " + String.valueOf(price));

        return view;
    }
}