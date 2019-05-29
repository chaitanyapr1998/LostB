package com.example.chaitanya.lostb;

import android.app.Notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.chaitanya.lostb.FirebaseApplication.CHANNEL_3_ID;
import static com.example.chaitanya.lostb.FirebaseApplication.getAppContext;
import static com.example.chaitanya.lostb.SettingsActivity.NOTIFICATION_SWITCH;
import static com.example.chaitanya.lostb.SettingsActivity.SHARED_PREFS;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

//Service class to notify user for post geofence match
public class JobServiceExample extends JobService {

    BackgroundTask bt;

    @Override
    public boolean onStartJob(@NonNull final JobParameters job) {
        Log.i("Service", "Inside on start");
        bt = new BackgroundTask(){
            @Override
            protected void onPostExecute(String s) {
                Toast.makeText(getApplicationContext(), "Message" + s, Toast.LENGTH_SHORT).show();
                jobFinished(job, false);
            }
        };
        bt.execute();
        return true;
    }

    @Override
    public boolean onStopJob(@NonNull JobParameters job) {
        return true;
    }

    public static class BackgroundTask extends AsyncTask<Void, Void, String> {

        DatabaseReference ref;
        ArrayList<GeofencePostModel> geofencePostData;
        ArrayList<Post> lostData;
        ArrayList<Post> lostTempData;
        private NotificationManagerCompat notificationManagerCompat;
        Context c;
        FirebaseUser mUser;

        ArrayList<GeoPostMarkModel> markData;

        @Override
        protected String doInBackground(Void... voids) {
            Log.i("Service", "Doinggg job");

            geofencePostData = new ArrayList<>();
            lostData = new ArrayList<>();
            lostTempData = new ArrayList<>();
            markData = new ArrayList<>();
            c = getAppContext();
            notificationManagerCompat = NotificationManagerCompat.from(c);
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            Log.i("Service", "before mark info");
            getMarkInfo();
            Log.i("Service", "before geofence post data");
            getGeofencePostData();
            Log.i("Service", "before lost data");
            getLostData();
            Log.i("Service", "before check");

            Log.i("Service", "before markit");

            Log.i("Service", "before return");
            return "Hello";
        }

        private void getGeofencePostData() {
            ref = FirebaseDatabase.getInstance().getReference().child("GeofencePost").child(mUser.getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    geofencePostData.clear();
                    if(dataSnapshot.exists()){
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            GeofencePostModel p = d.getValue(GeofencePostModel.class);
                            geofencePostData.add(p);
                        }
                    }
                    sleepThread();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void getLostData(){
            ref = FirebaseDatabase.getInstance().getReference().child("Lost");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    lostData.clear();
                    if(dataSnapshot.exists()){
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            Log.i("LocationPostActivity", "Fired");
                            Post p = d.getValue(Post.class);
                            lostData.add(p);
                        }
                    }
                    getLostTempData();
                    sleepThread();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void getLostTempData(){
            ref = FirebaseDatabase.getInstance().getReference().child("LostTemp");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    lostTempData.clear();
                    if(dataSnapshot.exists()){
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            Log.i("LocationPostActivity", "Fired");
                            Post p = d.getValue(Post.class);
                            lostTempData.add(p);
                        }
                    }
                    check();
                    sleepThread();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void getMarkInfo(){
            ref = FirebaseDatabase.getInstance().getReference().child("GeoPostMark").child(mUser.getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    markData.clear();
                    if(dataSnapshot.exists()){
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            GeoPostMarkModel p = d.getValue(GeoPostMarkModel.class);
                            markData.add(p);
                        }
                    }
                    sleepThread();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void check(){
            double latL = 0;
            double latR = 0;
            double lonL = 0;
            double lonR = 0;
            double val = 0.001;
            if(geofencePostData.size() != 0){
                for(int i = 0; i < lostTempData.size(); i++){
                    for(int j = 0; j < geofencePostData.size(); j++){
                        double latLost = Double.parseDouble(lostTempData.get(i).getLatitude()); //25
                        double lonLost = Double.parseDouble(lostTempData.get(i).getLongitude()); //25
                        double latMat = Double.parseDouble(geofencePostData.get(j).getLat()); //28
                        double lonMat = Double.parseDouble(geofencePostData.get(j).getLon()); //25
                        latL = latMat - val; //18
                        latR = latMat + val; //38
                        lonL = lonMat - val; //15
                        lonR = lonMat + val; //35
                        Log.i("latLost", String.valueOf(latLost));
                        Log.i("lonLost", String.valueOf(lonLost));
                        Log.i("latMat", String.valueOf(latMat));
                        Log.i("lonMat", String.valueOf(lonMat));
                        Log.i("latL", String.valueOf(latL));
                        Log.i("latR", String.valueOf(latR));
                        Log.i("lonL", String.valueOf(lonL));
                        Log.i("lonR", String.valueOf(lonR));
                        if(latL < latLost && latR > latLost){
                            if(lonL < lonLost && lonR > lonLost){
                                Log.i("JobService", "Matchhhhhh");
                                int y = markData.size();
                                if(markData.size() != 0){
                                    for(int q = 0; q < markData.size(); q++){
                                        if(!markData.get(q).getLostId().equals(lostTempData.get(i).getId())){
                                            if(!lostTempData.get(i).getUserId().equals(mUser.getUid())){
                                                String id = CHANNEL_3_ID;
                                                final Notification n = new NotificationCompat.Builder(c, id)
                                                        .setSmallIcon(R.mipmap.ic_launcher_round)
                                                        .setContentTitle("Post Match")
                                                        .setContentText(lostTempData.get(i).getTitle())
                                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                                        .build();
                                                notificationManagerCompat.notify(1, n);
                                                Log.i("JobService", "Noti");
                                                ref = FirebaseDatabase.getInstance().getReference().child("LostTemp").child(lostTempData.get(i).getId());
                                                ref.removeValue();
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }

            }
            markIt();
            sleepThread();
        }

        private void markIt(){
            if(lostData.size() != 0){
                for(int i = 0; i < lostData.size(); i++){
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("GeoPostMark").child(mUser.getUid());
                    String key = ref.push().getKey();
                    GeoPostMarkModel g = new GeoPostMarkModel(key, lostData.get(i).getId(), mUser.getUid());
                    ref = FirebaseDatabase.getInstance().getReference().child("GeoPostMark").child(mUser.getUid()).child(lostData.get(i).getId());
                    ref.setValue(g);
                    Log.i("JobService", "Struck here");
                }
            }
            sleepThread();
        }

        private void sleepThread(){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }


}
