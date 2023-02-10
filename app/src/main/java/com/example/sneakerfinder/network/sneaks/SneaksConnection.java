package com.example.sneakerfinder.network.sneaks;

import com.example.sneakerfinder.network.sneaks.dto.SneaksProduct;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Connection to backend node.js server, which scrapes sneaker information on-the-fly
 * from different websites. The results is returned to the app to display additional
 * information to the shoes.
 */
public interface SneaksConnection {
    @GET("/id/{id}")
    Call<SneaksProduct> getSneaker(
            @Path("id") String styleId
    );

    @GET("/search/{shoe}")
    Call<List<SneaksProduct>> getSneakers(
            @Path("shoe") String searchString,
            @Query("count") Integer count
    );
}
