package com.example.user.dipl1.appeal;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class AppealRepository {
    private Retrofit retrofit;
    private AppealService appealService;

    public static final String BASE_URL = "http://vladsenyuk-001-site1.htempurl.com/";

    public void InitializeRetrofit(){
        try{
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();

            appealService = retrofit.create(AppealService.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AppealService getApi(){
        return appealService;
    }
}
