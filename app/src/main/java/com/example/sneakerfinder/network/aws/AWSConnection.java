package com.example.sneakerfinder.network.aws;

import com.example.sneakerfinder.network.aws.dto.ShoeCropping;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AWSConnection {
    // Was earlier implemented to do the online shoe recognition using AWS.
    /*
    DEPRECATED
    @POST("/")
    Call<ShoeRecognitionResult> recognizeShoe(
            @Body RequestBody binaryShoeImage
    );
     */

    @POST("/")
    Call<ShoeCropping> cropShoe(
            @Body RequestBody binaryShoeImage
    );
}
