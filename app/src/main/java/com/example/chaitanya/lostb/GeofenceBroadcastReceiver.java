package com.example.chaitanya.lostb;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.GeofencingEvent;

import static android.content.Context.MODE_PRIVATE;
import static com.example.chaitanya.lostb.FirebaseApplication.CHANNEL_2_ID;
import static com.example.chaitanya.lostb.SettingsActivity.NOTIFICATION_SWITCH;
import static com.example.chaitanya.lostb.SettingsActivity.SHARED_PREFS;
import static com.example.chaitanya.lostb.SettingsActivity.getSharedPreferences;


public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();
    private NotificationManagerCompat notificationManagerCompat;
    Context c;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Inside geofence onReceive");
        notificationManagerCompat = NotificationManagerCompat.from(context);
        c = context;

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, String.format("Error code : %d", geofencingEvent.getErrorCode()));
            return;
        }


        //int geofenceTransition = geofencingEvent.getGeofenceTransition();
//        if(){
//
//        }

//        SharedPreferences sharedPreferences = PreferenceManager.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        boolean checkNotSwitch = sharedPreferences.getBoolean(NOTIFICATION_SWITCH, false);
        SharedPreferences ss = getSharedPreferences(context);
        boolean switchNot = ss.getBoolean(NOTIFICATION_SWITCH, false);
        Log.i(TAG, String.valueOf(switchNot));

        if(switchNot){
            String id = CHANNEL_2_ID;
            Notification n = new NotificationCompat.Builder(c, id)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Geofence")
                    .setContentText("Location Reminder")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();
            notificationManagerCompat.notify(1, n);
        }


    }


}
