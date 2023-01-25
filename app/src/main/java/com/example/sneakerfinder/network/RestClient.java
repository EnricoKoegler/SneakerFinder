package com.example.sneakerfinder.network;

import android.content.Context;

import com.example.sneakerfinder.BuildConfig;
import com.example.sneakerfinder.network.aws.AWSConnection;
import com.example.sneakerfinder.network.sneaks.AccessTokenInterceptor;
import com.example.sneakerfinder.network.sneaks.SneaksConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
    private static RestClient instance;

    private final Retrofit retrofitAws;

    private final Retrofit retrofitSneaks;

    public static RestClient getInstance(Context context) {
        if (instance == null) instance = new RestClient(context);
        return instance;
    }

    public AWSConnection getAWSConnection() {
        return retrofitAws.create(AWSConnection.class);
    }

    public SneaksConnection getSneaksConnection() {
        return retrofitSneaks.create(SneaksConnection.class);
    }

    private RestClient(Context context) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        OkHttpClient okHttpClientAws = new OkHttpClient.Builder()
                .build();
        retrofitAws = new Retrofit.Builder()
                .baseUrl(BuildConfig.AWS_BASE_URL)
                .client(okHttpClientAws)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        OkHttpClient okHttpClientSneaks = new OkHttpClient.Builder()
                .addInterceptor(AccessTokenInterceptor.getInstance())
                .build();
        retrofitSneaks = new Retrofit.Builder()
                .baseUrl(BuildConfig.SNEAKS_BASE_URL)
                .client(okHttpClientSneaks)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
