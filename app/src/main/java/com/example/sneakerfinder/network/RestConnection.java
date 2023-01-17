package com.example.sneakerfinder.network;

import com.example.sneakerfinder.network.model.ShoeRecognitionResult;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RestConnection {
    @POST("/")
    Call<ShoeRecognitionResult> recognizeShoe(
            @Body RequestBody binaryShoeImage
    );
}
