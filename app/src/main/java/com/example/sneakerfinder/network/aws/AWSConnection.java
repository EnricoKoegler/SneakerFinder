package com.example.sneakerfinder.network.aws;

import com.example.sneakerfinder.network.aws.dto.ShoeRecognitionResult;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AWSConnection {
    @POST("/")
    Call<ShoeRecognitionResult> recognizeShoe(
            @Body RequestBody binaryShoeImage
    );
}
