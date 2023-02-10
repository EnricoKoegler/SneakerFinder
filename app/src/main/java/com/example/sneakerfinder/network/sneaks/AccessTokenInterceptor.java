package com.example.sneakerfinder.network.sneaks;

import android.util.Log;

import com.example.sneakerfinder.BuildConfig;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Interceptor for HTTP requests which adds an Authorization Header to every request made
 * to the SneaksAPI.
 */
public class AccessTokenInterceptor implements Interceptor {
    private static AccessTokenInterceptor interceptor;

    private AccessTokenInterceptor() {}

    public static AccessTokenInterceptor getInstance() {
        if (interceptor == null) interceptor = new AccessTokenInterceptor();
        return interceptor;
    }

    @NotNull
    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String apiKey = BuildConfig.SNEAKS_API_KEY;

        request = setBearerHeader(request, apiKey);

        okhttp3.Response response = chain.proceed(request);

        switch (response.code()) {
            case 404:
                Log.e("REST", "Resource not found");
                break;
            case 500:
                Log.e("REST", "Internal server error on request to " + request.url());
                break;
            case 403:
                Log.e("REST", "No access");
                break;
        }

        return response;
    }

    public static Request setBearerHeader(Request request, String apiKey) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + apiKey)
                .build();
    }

}
