package com.example.chaitanya.lostb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationResult;

public class LocationBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATE = "com.example.chaitanya.lostb.UPDATE_LOCATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            final String action = intent.getAction();
            if(ACTION_PROCESS_UPDATE.equals(action)){
                LocationResult r = LocationResult.extractResult(intent);
                if(r != null){
                    Location location = r.getLastLocation();
                    String lat = String.valueOf(location.getLatitude());
                    String lon = String.valueOf(location.getLongitude());
                    String b = new StringBuilder(""+location.getLatitude()).append("/").append(location.getLongitude()).toString();
                    try {
                        LocationHistory.getInstance().updateTextview(location.getLatitude(), location.getLongitude());
                    } catch (Exception e){

                    }
                }
            }
        }
    }
}
