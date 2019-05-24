package com.example.chaitanya.lostb;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.service.carrier.CarrierMessagingService;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

public class Geofence implements ResultCallback {

    public static final float GEOFENCING_RADIUS = 500;
    public static final long GEOFENCING_EXPIRY_TIME = 999999;

    private List<com.google.android.gms.location.Geofence> mGeofenceLocationLst;
    private PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;

    public Geofence(Context context, GoogleApiClient client) {
        mContext = context;
        mGoogleApiClient = client;
        mGeofencePendingIntent = null;
        mGeofenceLocationLst = new ArrayList<>();
    }

    public void registerAllGeofences() {

        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected() ||
                mGeofenceLocationLst == null || mGeofenceLocationLst.size() == 0) {
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {

            Log.e("Geofence", securityException.getMessage());
        }
    }

    public void unRegisterAllGeofences() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            return;
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,

                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {

            Log.e("Geofence", securityException.getMessage());
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceLocationLst);
        return builder.build();
    }

    public void updateGeofenceLst(PlaceBuffer places) {
        mGeofenceLocationLst = new ArrayList<>();
        if (places == null || places.getCount() == 0) return;
        for (Place place : places) {

            String placeUID = place.getId();
            double lat = place.getLatLng().latitude;
            double lng = place.getLatLng().longitude;

            com.google.android.gms.location.Geofence geofence = new com.google.android.gms.location.Geofence.Builder()
                    .setRequestId(placeUID)
                    .setExpirationDuration(GEOFENCING_EXPIRY_TIME)
                    .setCircularRegion(lat, lng, GEOFENCING_RADIUS)
                    .setTransitionTypes(com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER | com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            mGeofenceLocationLst.add(geofence);
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.e("Geofence", String.format("Error in geofence class",
                result.getStatus().toString()));
    }
}
