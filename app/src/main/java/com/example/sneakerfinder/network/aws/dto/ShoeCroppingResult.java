package com.example.sneakerfinder.network.aws.dto;

import com.google.gson.annotations.SerializedName;

public class ShoeCroppingResult {
    @SerializedName("shoe_found")
    public boolean shoeFound;

    @SerializedName("image_base64")
    public String imageBase64;
}
