package com.example.sneakerfinder.db.entity;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Represents a single shoe model. Even though the shoe has an internal primary key, the styleId
 * can also be used as unique identifier. The styleId is populated by the styleId which comes from
 * the StockX api. Most of the additional fields are also populated during the shoe recognition
 * process using the {@link com.example.sneakerfinder.network.sneaks.SneaksConnection}
 */
@Entity(indices = {@Index(value = {"styleId"}, unique = true)})
public class Shoe {
    @PrimaryKey(autoGenerate = true) public long shoeId;

    public String styleId; // ID from StockX
    public String name; // eg. "New Balance 530 White Silver Navy"
    public String description;
    public String brand; // eg. "New Balance"
    public String model; // eg. "New Balance 530"
    public String colorway; // eg. "White/Silver/Navy"
    public String price; // Retail price
    public String onlineStoreUrl;
    public String thumbnailUrl;
    public Date releaseDate;

    @Ignore
    public Shoe(String name, String description, String price, String onlineStoreUrl, String thumbnailUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.onlineStoreUrl = onlineStoreUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public Shoe() {}
}
