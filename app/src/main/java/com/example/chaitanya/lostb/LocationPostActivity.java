package com.example.chaitanya.lostb;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LocationPostActivity extends AppCompatActivity {

    Button btnloc;
    public static final int PLACE_PICKER = 3333;
    public static final String TAG = LocationPostActivity.class.getSimpleName();

    ArrayList<GeofencePostModel> geofencePostData;
    ArrayList<Post> lostData;

    DatabaseReference ref;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_post);
        setTitle("Geofence for Post");

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

            //String locationID = place.getId();
            LatLng latlon = place.getLatLng();
            String lat = String.valueOf(latlon.latitude);
            String lon = String.valueOf(latlon.longitude);


            ref = FirebaseDatabase.getInstance().getReference().child("GeofencePost").child(mUser.getUid());
            GeofencePostModel gm = new GeofencePostModel(lat, lon);
            ref.push().setValue(gm);
            getGeofencePostData();

        }
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
                    Toast.makeText(LocationPostActivity.this, "Matchhhhhhh..........",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
