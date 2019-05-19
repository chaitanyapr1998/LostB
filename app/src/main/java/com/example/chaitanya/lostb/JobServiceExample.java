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

import static com.example.chaitanya.lostb.FirebaseApplication.CHANNEL_3_ID;
import static com.example.chaitanya.lostb.FirebaseApplication.getAppContext;
import static com.example.chaitanya.lostb.SettingsActivity.NOTIFICATION_SWITCH;
import static com.example.chaitanya.lostb.SettingsActivity.SHARED_PREFS;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

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
        private NotificationManagerCompat notificationManagerCompat;
        Context c;
        FirebaseUser mUser;


        @Override
        protected String doInBackground(Void... voids) {
            Log.i("Service", "Doinggg job");

            geofencePostData = new ArrayList<>();
            lostData = new ArrayList<>();
            c = getAppContext();
            notificationManagerCompat = NotificationManagerCompat.from(c);
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            getGeofencePostData();
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
                    //refreshData();
                    getLostData();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void getLostData(){
            ref = FirebaseDatabase.getInstance().getReference().child("Lost");
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    lostData.clear();
                    if(dataSnapshot.exists()){
                        Log.i("LocationPostActivity", "Fired");
                        Post p = dataSnapshot.getValue(Post.class);
                        lostData.add(p);
                    }
                    check();
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

        private void check(){
            double latL = 0;
            double latR = 0;
            double lonL = 0;
            double lonR = 0;
            double val = 0.001;
            if(geofencePostData.size() != 0){
                for(int i = 0; i < lostData.size(); i++){
                    double latLost = Double.parseDouble(lostData.get(i).getLatitude()); //25
                    double lonLost = Double.parseDouble(lostData.get(i).getLongitude()); //25
                    double latMat = Double.parseDouble(geofencePostData.get(i).getLat()); //28
                    double lonMat = Double.parseDouble(geofencePostData.get(i).getLon()); //25
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
//                            SharedPreferences ss = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//                            boolean switchNot = ss.getBoolean(NOTIFICATION_SWITCH, false);
//                            Log.i("JobService", String.valueOf(switchNot));

                            //if(switchNot){
                                String id = CHANNEL_3_ID;
                                Notification n = new NotificationCompat.Builder(c, id)
                                        .setSmallIcon(R.mipmap.ic_launcher_round)
                                        .setContentTitle("Post Match")
                                        .setContentText("Post Reminder")
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                        .build();
                                notificationManagerCompat.notify(1, n);
                            //}
                            Toast.makeText(c, "Matchhhhhhh..........",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

        }


    }


}
