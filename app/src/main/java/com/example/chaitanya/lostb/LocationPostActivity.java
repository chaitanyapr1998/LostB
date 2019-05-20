package com.example.chaitanya.lostb;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.chaitanya.lostb.FirebaseApplication.CHANNEL_2_ID;
import static com.example.chaitanya.lostb.FirebaseApplication.CHANNEL_3_ID;
import static com.example.chaitanya.lostb.SettingsActivity.NOTIFICATION_SWITCH;
import static com.example.chaitanya.lostb.SettingsActivity.SHARED_PREFS;

public class LocationPostActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    Button btnloc;
    public static final int PLACE_PICKER = 3333;
    public static final String TAG = LocationPostActivity.class.getSimpleName();

    ArrayList<GeofencePostModel> geofencePostData;
    ArrayList<Post> lostData;

    DatabaseReference ref;
    FirebaseUser mUser;
    Context c;
    private NotificationManagerCompat notificationManagerCompat;

    private PostPlaceAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private GoogleApiClient mClient;


    APIService apiService;
    boolean notify;

    //
    Button st, sp;
    private FirebaseJobDispatcher jb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_post);
        setTitle("Geofence for Post");
        notificationManagerCompat = NotificationManagerCompat.from(this);
        apiService = NClient.getClient("https://fcm.googleapis.com/").create(APIService.class);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        geofencePostData = new ArrayList<>();
        lostData = new ArrayList<>();

        btnloc = (Button) findViewById(R.id.btn_locpost);
        btnloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLocBtnClicked();
            }
        });

        st = (Button) findViewById(R.id.btn_startt);
        sp = (Button) findViewById(R.id.btn_stopp);

        jb = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startJob(v);
            }
        });

        sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopJob(v);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.listofplaces);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PostPlaceAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
    }

    private void startJob(View v){

        Job job = jb.newJobBuilder()
                .setService(JobServiceExample.class)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTag("My job")
                .setTrigger(Trigger.executionWindow(0, 60))
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setReplaceCurrent(false)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        jb.mustSchedule(job);

        Log.i("Job service", "Schedulled");


    }

    private void stopJob(View v){
        jb.cancel("My job");
        Log.i("Job service", "Cancel");
    }

    private void onLocBtnClicked(){
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent i = builder.build(this);
            startActivityForResult(i, PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, String.format("PlacePicker Exception: %s", e.getMessage()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PLACE_PICKER && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            if (place == null) {
                Log.i(TAG, "No place selected");
                return;
            }

            String locationID = place.getId();
            LatLng latlon = place.getLatLng();
            String lat = String.valueOf(latlon.latitude);
            String lon = String.valueOf(latlon.longitude);


            ref = FirebaseDatabase.getInstance().getReference().child("GeofencePost").child(mUser.getUid()).child(locationID);
            GeofencePostModel gm = new GeofencePostModel(locationID, lat, lon);
            ref.setValue(gm);
            getGeofencePostData();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getGeofencePostData();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
                refreshData();
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
                //check();
                //makePrevPostMarked();
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

    private void makePrevPostMarked(){
//        double latL = 0;
//        double latR = 0;
//        double lonL = 0;
//        double lonR = 0;
//        double val = 0.001;
//        if(geofencePostData.size() != 0){
//            for(int i = 0; i < lostData.size(); i++){
//                double latLost = Double.parseDouble(lostData.get(i).getLatitude()); //25
//                double lonLost = Double.parseDouble(lostData.get(i).getLongitude()); //25
//                double latMat = Double.parseDouble(geofencePostData.get(i).getLat()); //28
//                double lonMat = Double.parseDouble(geofencePostData.get(i).getLon()); //25
//                latL = latMat - val; //18
//                latR = latMat + val; //38
//                lonL = lonMat - val; //15
//                lonR = lonMat + val; //35
//                Log.i("latLost", String.valueOf(latLost));
//                Log.i("lonLost", String.valueOf(lonLost));
//                Log.i("latMat", String.valueOf(latMat));
//                Log.i("lonMat", String.valueOf(lonMat));
//                Log.i("latL", String.valueOf(latL));
//                Log.i("latR", String.valueOf(latR));
//                Log.i("lonL", String.valueOf(lonL));
//                Log.i("lonR", String.valueOf(lonR));
//                if(latL < latLost && latR > latLost){
//                    if(lonL < lonLost && lonR > lonLost){
//                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("GeoPostMark").child(mUser.getUid());
//                        String key = ref.push().getKey();
//                        GeoPostMarkModel g = new GeoPostMarkModel(key, lostData.get(i).getId(), mUser.getUid());
//                        ref = FirebaseDatabase.getInstance().getReference().child("GeoPostMark").child(mUser.getUid()).child(lostData.get(i).getId());
//                        ref.setValue(g);
//                    }
//                }
//            }
//        }
        if(lostData.size() != 0){
            for(int i = 0; i < lostData.size(); i++){
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("GeoPostMark").child(mUser.getUid());
                String key = ref.push().getKey();
                GeoPostMarkModel g = new GeoPostMarkModel(key, lostData.get(i).getId(), mUser.getUid());
                ref = FirebaseDatabase.getInstance().getReference().child("GeoPostMark").child(mUser.getUid()).child(lostData.get(i).getId());
                ref.setValue(g);
            }
        }
    }

    private void refreshData(){
        List<String> plc = new ArrayList<>();
        if(geofencePostData.size() > 0){
            for(int i = 0; i < geofencePostData.size(); i++){
                if(!geofencePostData.get(i).getPlaceId().isEmpty()){
                    plc.add(geofencePostData.get(i).getPlaceId());
                }
            }
        }

        if(!plc.isEmpty()){
            PendingResult<PlaceBuffer> plcRslt = Places.GeoDataApi.getPlaceById(mClient,
                    plc.toArray(new String[plc.size()]));
            plcRslt.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(@NonNull PlaceBuffer places) {
                    mAdapter.swapP(places);
                }
            });
        }

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
                        Log.i(TAG, "Matchhhhhh");
                        SharedPreferences ss = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        boolean switchNot = ss.getBoolean(NOTIFICATION_SWITCH, false);
                        Log.i(TAG, String.valueOf(switchNot));

                        if(switchNot){
//                            String id = CHANNEL_3_ID;
//                            Notification n = new NotificationCompat.Builder(this, id)
//                                    .setSmallIcon(R.mipmap.ic_launcher_round)
//                                    .setContentTitle("Post Match")
//                                    .setContentText("Post Reminder")
//                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                                    .build();
//                            notificationManagerCompat.notify(1, n);
                        }
                        Toast.makeText(LocationPostActivity.this, "Matchhhhhhh..........",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }
}
