package com.example.chaitanya.lostb;

import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GeofenceActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int PLACE_PICKER = 222;

    private RecyclerView mRecyclerView;

    private GoogleApiClient mClient;
    private Geofence mGeofence;

    Button btnAddGeofence;

    public static final String TAG = GeofenceActivity.class.getSimpleName();

    DatabaseReference ref;
    FirebaseUser mUser;

    ArrayList<GeofenceModel> geofenceData;

    private PlaceAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);
        setTitle("Geofence");

        btnAddGeofence = (Button) findViewById(R.id.btn_addgeo);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        geofenceData = new ArrayList<>();

        btnAddGeofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGeoBtnClicked();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.listofplaces);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaceAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();

        mGeofence = new Geofence(this, mClient);
        mGeofence.registerAllGeofences();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getGeofenceData();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void addGeoBtnClicked(){
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

            String id = String.valueOf(System.currentTimeMillis());
            String locationID = place.getId();

            ref = FirebaseDatabase.getInstance().getReference().child("Geofence").child(mUser.getUid()).child(locationID);
            GeofenceModel gm = new GeofenceModel(locationID, id);
            ref.setValue(gm);
            getGeofenceData();

        }
    }

    private void getGeofenceData() {
        ref = FirebaseDatabase.getInstance().getReference().child("Geofence").child(mUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                geofenceData.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        GeofenceModel p = d.getValue(GeofenceModel.class);
                        geofenceData.add(p);
                    }
                }
                refreshData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void refreshData(){
        List<String> plc = new ArrayList<>();
        for(int i = 0; i < geofenceData.size(); i++){
            if(!geofenceData.get(i).getPlaceId().isEmpty()){
                plc.add(geofenceData.get(i).getPlaceId());
            }
        }

        if(!plc.isEmpty()){
            PendingResult<PlaceBuffer> plcRslt = Places.GeoDataApi.getPlaceById(mClient,
                    plc.toArray(new String[plc.size()]));
            plcRslt.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(@NonNull PlaceBuffer places) {
                    mAdapter.swapP(places);
                    mGeofence.updateGeofenceLst(places);
                    mGeofence.registerAllGeofences();
                }
            });
        }

    }
}
