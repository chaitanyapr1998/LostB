package com.example.chaitanya.lostb;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

//Location history page in the app
public class LocationHistory extends AppCompatActivity {

    Button btnStart, btnStop;
    ArrayList<String> mMyLocation;
    LocationRequest req;
    FusedLocationProviderClient fusedLocationProviderClient;
    ArrayList<String> locHis;

    static LocationHistory instance;

    private LocCustomAdapter adapter;
    ListView lhListView;

    public static LocationHistory getInstance() {
        return instance;
    }

    DatabaseReference ref;
    ArrayList<LocationModel> locHisData;
    FirebaseUser mUser;
    ArrayList<LocationModel> filLocHisData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);
        setTitle("Location History");

        instance = this;
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_stop);
        lhListView = (ListView) findViewById(R.id.lv_lochis);

        mMyLocation = new ArrayList<>();
        locHis = new ArrayList<>();
        locHisData = new ArrayList<>();
        filLocHisData = new ArrayList<>();

        if (!checkForPermission()) {
            btnClickable();
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBroadCastReceiver();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killBroadCastReceiver();
            }
        });

        loadLocHis();
    }

    private boolean checkForPermission() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1234) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                btnClickable();
            } else {
                Toast.makeText(LocationHistory.this, "Permission is not granted",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void btnClickable() {
        locationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(req, getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationBroadcastReceiver.class);
        intent.setAction(LocationBroadcastReceiver.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void locationRequest(){
        req = new LocationRequest();
        req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        req.setInterval(2700000);  //10000  //45 mins in millis
        req.setFastestInterval(2700000);  //10000
        req.setSmallestDisplacement(0);
    }

    private void startBroadCastReceiver() {
        PackageManager pm = this.getPackageManager();
        ComponentName componentName = new ComponentName(this, LocationBroadcastReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        Toast.makeText(getApplicationContext(), "Location history is turned on", Toast.LENGTH_SHORT).show();
    }

    private void killBroadCastReceiver() {
        PackageManager pm = this.getPackageManager();
        ComponentName componentName = new ComponentName(this, LocationBroadcastReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        Toast.makeText(getApplicationContext(), "Location history is turned off", Toast.LENGTH_SHORT).show();
    }

    private void loadLocHis(){
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
                    refreshLocHis();
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
    }

    private void refreshLocHis(){
        adapter = new LocCustomAdapter(this, filLocHisData);
        lhListView.setAdapter(adapter);
    }


}
