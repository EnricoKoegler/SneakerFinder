package com.example.sneakerfinder.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShoeRecognitionResult {
    @SerializedName("objects")
    public List<ShoeRecognitionResultObject> objects;
}
