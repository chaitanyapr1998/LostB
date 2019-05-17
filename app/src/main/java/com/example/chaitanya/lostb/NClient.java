package com.example.chaitanya.lostb;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String url){
        if(retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
