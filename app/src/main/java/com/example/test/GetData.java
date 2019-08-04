package com.example.test;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetData {
    //request
    @GET("/api/mobile/public/availablecars")
    Call<List<Car>> getAllCars();
}
