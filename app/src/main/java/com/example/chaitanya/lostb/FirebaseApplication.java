package com.example.chaitanya.lostb;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;


public class FirebaseApplication extends Application {

    public static final String CHANNEL_1_ID = "LocationMatch";
    public static final String CHANNEL_2_ID = "GeofenceMatch";
    public static final String CHANNEL_3_ID = "PostMatch";

    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();

        if(!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        FirebaseApplication.context = getApplicationContext();
        createNotificationChannels();

    }

    private void createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            android.app.NotificationChannel channel = new android.app.NotificationChannel(CHANNEL_1_ID, "Location Match", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Can you help someone to find their lost item?");

            android.app.NotificationChannel channel2 = new android.app.NotificationChannel(CHANNEL_2_ID, "Geofence Match", NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("Location Reminder");

            android.app.NotificationChannel channel3 = new android.app.NotificationChannel(CHANNEL_3_ID, "Post Match", NotificationManager.IMPORTANCE_HIGH);
            channel3.setDescription("Post Reminder");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channel3);

        }
    }

    public static Context getAppContext() {
        return FirebaseApplication.context;
    }
}
