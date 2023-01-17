package com.example.sneakerfinder.network;

import android.content.Context;

import com.example.sneakerfinder.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
    private static RestClient instance;

    private Retrofit retrofit;
    private OkHttpClient okHttpClient;
    private Gson gson;

    public static RestClient getInstance(Context context) {
        if (instance == null) instance = new RestClient(context);
        return instance;
    }

    public RestConnection getConnection() {
        return retrofit.create(RestConnection.class);
    }

    private RestClient(Context context) {
        okHttpClient = new OkHttpClient.Builder()
                .build();

        gson = new GsonBuilder()
                .serializeNulls()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
