package com.example.chaitanya.lostb;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseReference ref;
    ArrayList<String> lat, lon, tit;
    ArrayList<String> flat, flon, ftit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        lat = new ArrayList<>();
        lon = new ArrayList<>();
        tit = new ArrayList<>();
        flat = new ArrayList<>();
        flon = new ArrayList<>();
        ftit = new ArrayList<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        ref = FirebaseDatabase.getInstance().getReference().child("Lost");
        getLatLon();
        ref = FirebaseDatabase.getInstance().getReference().child("Found");
        getFoundLatLon();

        sleepThread();
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        BitmapDescriptor bitmapDescriptor1
                = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_YELLOW);

        if(lat.size() != 0){
            for(int i = 0; i < lat.size(); i++){
                LatLng mark = new LatLng(Double.valueOf(lat.get(i)), Double.valueOf(lon.get(i)));
                mMap.addMarker(new MarkerOptions().position(mark).title(tit.get(i))).setIcon(bitmapDescriptor1);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mark));
            }
        }



        BitmapDescriptor bitmapDescriptor
                = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_BLUE);

        if(flat.size() != 0){
            for(int i = 0; i < flat.size(); i++){
                LatLng mark = new LatLng(Double.valueOf(flat.get(i)), Double.valueOf(flon.get(i)));
                mMap.addMarker(new MarkerOptions().position(mark).title(ftit.get(i))).setIcon(bitmapDescriptor);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mark));

            }
        }
    }

    private void getLatLon(){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lat.clear();
                lon.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        Post p = d.getValue(Post.class);
                        lat.add(p.getLatitude());
                        lon.add(p.getLongitude());
                        tit.add(p.getTitle());
                        Toast.makeText(MapsActivity.this, p.getLatitude(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFoundLatLon(){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                flat.clear();
                flon.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        Post p = d.getValue(Post.class);
                        flat.add(p.getLatitude());
                        flon.add(p.getLongitude());
                        ftit.add(p.getTitle());
                        Toast.makeText(MapsActivity.this, p.getLatitude(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void sleepThread(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
