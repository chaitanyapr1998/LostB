package com.example.chaitanya.lostb;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAvllC3ig:APA91bGTqb5jqQ6T5WnlEvVaI8_AtGPEkPOFFpuAOzfx6KSEtxMOiRC26YWzE_-zKCOAD1b9yZElor008miUXJ_yK3Z6PRX1mawU3EQMfnI2XGoW880IzfmJstllTstUU4vpqxwDFl7V"
            }
    )

    @POST("fcm/send")
    Call<NotificationResponse> sendNotification(@Body NotificationSenderModel body);
}
