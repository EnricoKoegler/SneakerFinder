package com.example.sneakerfinder.network.sneaks;

import com.example.sneakerfinder.db.entity.Shoe;
import com.example.sneakerfinder.network.sneaks.dto.SneaksProduct;

import java.text.NumberFormat;
import java.util.Locale;

public class SneaksAdapter {
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);

    public static Shoe getShoe(SneaksProduct p) {
        Shoe s = new Shoe();
        s.name = p.shoeName;
        s.description = p.description;
        s.brand = p.brand;
        s.model = p.make;
        s.colorway = p.colorway;

        s.price = CURRENCY_FORMAT.format(p.retailPrice);
        s.onlineStoreUrl = p.resellLinks.stockX;
        s.releaseDate = p.releaseDate;
        s.thumbnailUrl = p.thumbnail;
        s.styleId = p.styleID;

        return s;
    }
}
