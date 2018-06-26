package com.example.user.dipl1.appeal;

import com.example.user.dipl1.utils.Ad;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AppealService {

    @POST("/api/appeal")
    Call<Integer> sendAppeal(@Body Appeal appeal);

    @GET("/api/appeal")
    Call<List<Ad>> recvAds();
}
