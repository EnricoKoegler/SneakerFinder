package com.example.sneakerfinder.network.sneaks;

import com.example.sneakerfinder.db.entity.Shoe;
import com.example.sneakerfinder.network.sneaks.dto.SneaksProduct;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Converts domain models from the SneaksAPI to internal domain models which can be stored in the
 * {@link com.example.sneakerfinder.db.AppDb}.
 */
public class SneaksAdapter {
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);

    public static Shoe getShoe(SneaksProduct p) {
        Shoe s = new Shoe();
        s.name = p.shoeName;
        s.description = p.description;
        s.brand = p.brand;
        s.model = p.make;
        s.colorway = p.colorway;

        try {
            s.price = CURRENCY_FORMAT.format(p.lowestResellPrice.stockX);
        } catch (Exception ignored) {}
        s.onlineStoreUrl = p.resellLinks.stockX;
        s.releaseDate = p.releaseDate;
        s.thumbnailUrl = p.thumbnail;
        s.styleId = p.styleID;

        return s;
    }
}
