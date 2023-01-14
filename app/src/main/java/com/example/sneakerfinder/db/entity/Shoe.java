package com.example.sneakerfinder.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Shoe {
    @PrimaryKey public long shoeId;
    public String name;
    public String description;
    public String price;
    public String onlineStoreUrl;
    public String thumbnailUrl;
}
