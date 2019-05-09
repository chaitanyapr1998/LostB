package com.example.chaitanya.lostb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    TextView txtLoc, txtNot;
    Switch switchLoc, switchNot;
    LocationManager manager;
    boolean check;

    public static final String SHARED_PREFS = "shared_prefs";
    public static final String NOTIFICATION_SWITCH = "notification_switch";

    public static boolean checkNotSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        txtLoc = (TextView) findViewById(R.id.settings_loc);
        txtNot = (TextView) findViewById(R.id.settings_locmatch);
        switchLoc = (Switch) findViewById(R.id.switch_loc);
        switchNot = (Switch) findViewById(R.id.switch_locmatch);

        checkingLocationOnOrOff();
        switchLoc.setChecked(check);

        switchLoc.setOnCheckedChangeListener(this);

        switchNot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notificationSwitch();
            }
        });



        loadPrefs();
        updateNotSwitch();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
            Toast.makeText(this, "Location on", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Location off", Toast.LENGTH_SHORT).show();
        }
    }

    private void notificationSwitch(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(NOTIFICATION_SWITCH, switchNot.isChecked());
        editor.apply();
    }

    private void loadPrefs(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        checkNotSwitch = sharedPreferences.getBoolean(NOTIFICATION_SWITCH, false);
    }

    public void updateNotSwitch(){
        switchNot.setChecked(checkNotSwitch);
    }


    public static SharedPreferences getSharedPreferences (Context context) {
        return context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
    }


}
