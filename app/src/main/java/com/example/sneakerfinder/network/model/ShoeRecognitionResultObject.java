package com.example.sneakerfinder.network.model;

import com.google.gson.annotations.SerializedName;

public class ShoeRecognitionResultObject {
    @SerializedName("title")
    public String title;

    @SerializedName("price")
    public String price;

    @SerializedName("online_store_url")
    public String onlineStoreUrl;

    @SerializedName("confidence")
    public float confidence;

    @SerializedName("thumbnail_url")
    public String thumbnailUrl;
}
