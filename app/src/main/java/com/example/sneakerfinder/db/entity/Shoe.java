package com.example.sneakerfinder.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Shoe {
    @PrimaryKey(autoGenerate = true) public long shoeId;
    public String name;
    public String description;
    public String price;
    public String onlineStoreUrl;
    public String thumbnailUrl;

    public Shoe(String name, String description, String price, String onlineStoreUrl, String thumbnailUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.onlineStoreUrl = onlineStoreUrl;
        this.thumbnailUrl = thumbnailUrl;
    }
}
