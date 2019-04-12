package com.example.chaitanya.lostb;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    TextView txtLoc;
    Switch switchLoc;
    LocationManager manager;
    boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        txtLoc = (TextView) findViewById(R.id.settings_loc);
        switchLoc = (Switch) findViewById(R.id.switch_loc);

        checkingLocationOnOrOff();
        switchLoc.setChecked(check);

        switchLoc.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        if(isChecked){
//            turningLocationOnOrOff();
//
//        } else {
//            turningLocationOnOrOff();
//            Toast.makeText(this, "Location off", Toast.LENGTH_LONG).show();
//        }
        turningLocationOnOrOff();
    }

    public void checkingLocationOnOrOff(){
        manager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        check = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.i("Location Check", String.valueOf(check));
    }

    private void turningLocationOnOrOff(){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        checkingLocationOnOrOff();
        switchLoc.setChecked(check);
        if(check){
            Toast.makeText(this, "Location on", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Location off", Toast.LENGTH_LONG).show();
        }
    }
}
