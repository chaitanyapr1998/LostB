package com.example.chaitanya.lostb;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String url){
        if(retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
        }
        String check = retrofit.toString();
        Log.i("Notification Client", retrofit.toString());
        return retrofit;
    }
}
