package com.example.chaitanya.lostb;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATE = "com.example.chaitanya.lostb.UPDATE_LOCATION";
    DatabaseReference ref;
    FirebaseUser mUser;
    private static String country, address, street;
    ArrayList<LocationModel> locMatch;
    ArrayList<Post> lostData;
    Context c;
    private NotificationManagerCompat notificationManagerCompat;
    ArrayList<LocationModel> locHisData;
    ArrayList<LocationModel> filLocHisData;
    public static final long DAY_IN_MILLI = 86400000; //86400000

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("MyLocation", "Doingg job");
        locMatch = new ArrayList<>();
        lostData = new ArrayList<>();
        locHisData = new ArrayList<>();
        filLocHisData = new ArrayList<>();
        notificationManagerCompat = NotificationManagerCompat.from(context);
        c = context;
        LocationResult res = LocationResult.extractResult(intent);
        if(res != null){
            Location loc = res.getLastLocation();
            double lat = loc.getLatitude();
            double lon = loc.getLongitude();
            getCountryName(context, lat, lon);
            uploadLocation();
            getLocData();
            checkForLocationMatch();
        }
    }

    private void getLocData(){
        ref = FirebaseDatabase.getInstance().getReference().child("LocationHistory");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locHisData.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        LocationModel p = d.getValue(LocationModel.class);
                        locHisData.add(p);
                    }
                    filtering();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void filtering(){
        filLocHisData.clear();
        if(locHisData.size() != 0){
            for(int i = 0; i < locHisData.size(); i++){
                if(locHisData.get(i).getUid().equals(mUser.getUid())){
                    filLocHisData.add(locHisData.get(i));
                }
            }
        }
        deleteExpiredLocation();
    }

    private void deleteExpiredLocation(){
        long time = System.currentTimeMillis();
        if(filLocHisData.size() != 0){
            for(int i = 0; i < filLocHisData.size(); i++){
                long dataTime = Long.parseLong(filLocHisData.get(i).getTime());
                if(time - dataTime > DAY_IN_MILLI){
                    ref = FirebaseDatabase.getInstance().getReference().child("LocationHistory").child(filLocHisData.get(i).getKey());
                    ref.removeValue();
                }
            }
        }
    }

//    private void query(){
//
//    }

    public void uploadLocation(){
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("LocationHistory");
        String key = ref.push().getKey();
        String time = String.valueOf(System.currentTimeMillis());
        String uid = mUser.getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("LocationHistory").child(key);
        LocationModel lm = new LocationModel(time, address, uid, country, street, key);
        ref.setValue(lm);
        //Log.i("LocationReceiver", ref.getKey());
    }

    public static void getCountryName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            Address result;

            if (addresses != null && !addresses.isEmpty()) {
                country = addresses.get(0).getCountryName();
                address = addresses.get(0).getAddressLine(0);
                street = addresses.get(0).getAdminArea();
            }

        } catch (IOException ignored) {
            //do something
        }
        //return null;
    }

    private void checkForLocationMatch(){
        ref = FirebaseDatabase.getInstance().getReference().child("LocationHistory");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                locMatch.clear();

                LocationModel model = dataSnapshot.getValue(LocationModel.class);
                locMatch.add(model);
                Log.i("MyLocation", "locMatch Added");
                getLostItemData();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLostItemData(){

        ref = FirebaseDatabase.getInstance().getReference().child("Lost");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lostData.clear();
                int count = 0;
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        Post p = d.getValue(Post.class);
                        lostData.add(p);
                        count = count + 1;
                        Log.i("MyLocation", "Lostdata Added");
                    }
                }
                checking();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checking(){
        if(lostData.size() != 0 && locMatch.size() != 0){
            for(int i = 0; i < lostData.size(); i++){
                String lostCountry = lostData.get(i).getCountry();
                String matchCountry = locMatch.get(0).getCountry();
                String lostStreet = lostData.get(i).getStreet();
                String matchStreet = locMatch.get(0).getStreet();
                if(!lostCountry.isEmpty() && !matchCountry.isEmpty() && !lostStreet.isEmpty() && !matchStreet.isEmpty()){
                    if(lostCountry.equals(matchCountry) && lostStreet.equals(matchStreet)){
                        if(!lostData.get(i).getUserId().equals(mUser.getUid())){
                            Notification n = new NotificationCompat.Builder(c, FirebaseApplication.CHANNEL_1_ID)
                                    .setSmallIcon(R.mipmap.logoicon)
                                    .setContentTitle("Location Match")
                                    .setContentText("Can you help someone to find their lost item?")
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                    .build();
                            notificationManagerCompat.notify(1, n);
                        }
                    }
                }

            }
        }

    }
}
