package com.example.chaitanya.lostb;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LocationHistory extends AppCompatActivity {

    Button btnStart, btnStop;
    TextView txtLoc;
    private BroadcastReceiver receiver;
    ArrayList<String> mMyLocation;
    LocationRequest req;
    FusedLocationProviderClient fusedLocationProviderClient;

    static LocationHistory instance;

    public static LocationHistory getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);

        instance = this;

        txtLoc = (TextView) findViewById(R.id.txt_loc);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_stop);
        mMyLocation = new ArrayList<>();

        if (!checkForPermission()) {
            btnClickable();
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startBroadCastReceiver();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //killBroadCastReceiver();
            }
        });

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
        req.setInterval(5000);
        req.setFastestInterval(3000);
        req.setSmallestDisplacement(0);
    }

    public void updateTextview(final String v){
        LocationHistory.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtLoc.append(v);
                Log.i("MyLocation", v);
                Toast.makeText(LocationHistory.this, v,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

//    private void startBroadCastReceiver() {
//        PackageManager pm = this.getPackageManager();
//        ComponentName componentName = new ComponentName(this, LocationBroadcastReceiver.class);
//        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
////                PackageManager.DONT_KILL_APP);
////        Toast.makeText(getApplicationContext(), "BroadCast Receiver Started", Toast.LENGTH_LONG).show();
////    }
////
////    private void killBroadCastReceiver() {
////        PackageManager pm = this.getPackageManager();
////        ComponentName componentName = new ComponentName(this, LocationBroadcastReceiver.class);
////        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
////                PackageManager.DONT_KILL_APP);
////        Toast.makeText(getApplicationContext(), "BroadCast Receiver Killed", Toast.LENGTH_LONG).show();
////    }


}
