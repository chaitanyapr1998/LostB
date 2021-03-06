package com.example.chaitanya.lostb;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

//Mapview page in the app
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseReference ref;
    ArrayList<String> lat, lon, tit;
    ArrayList<String> flat, flon, ftit;
    ImageButton filter, backBtn;
    Dialog mDialog;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private DatePickerDialog.OnDateSetListener toDateSetListener;

    ArrayList<Post> data;
    ArrayList<Post> dataFound;

    ArrayList<Post> filteredData;
    ArrayList<Post> filteredDataFound;

    Date datadate, datefrom, dateto;
    private int check = 0;

    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mDialog = new Dialog(this);

        lat = new ArrayList<>();
        lon = new ArrayList<>();
        tit = new ArrayList<>();
        flat = new ArrayList<>();
        flon = new ArrayList<>();
        ftit = new ArrayList<>();
        filteredData = new ArrayList<>();
        filteredDataFound = new ArrayList<>();
        data = new ArrayList<>();
        dataFound = new ArrayList<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        ref = FirebaseDatabase.getInstance().getReference().child("Lost");
        getLatLon();
        ref = FirebaseDatabase.getInstance().getReference().child("Found");
        getFoundLatLon();

        sleepThread();
        mapFragment.getMapAsync(this);

        filter = (ImageButton)findViewById(R.id.filterBtn);
        backBtn = (ImageButton)findViewById(R.id.backBtn) ;

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, LostActivity.class);
                startActivity(intent);
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpView(v);
            }
        });

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
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                final int finalI = i;
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            Intent intent = new Intent(MapsActivity.this, DetailedViewActivity.class);
                            String title = data.get(finalI).getTitle();
                            String date = data.get(finalI).getDate();
                            String place = data.get(finalI).getLocation();
                            String email = data.get(finalI).getEmail();
                            String uid = data.get(finalI).getId();
                            String userid = data.get(finalI).getUserId();
                            String des = data.get(finalI).getDescription();
                            String cat = data.get(finalI).getCategory();
                            String country = data.get(finalI).getCountry();
                            String address = data.get(finalI).getAddress();
                            intent.putExtra("title", title);
                            intent.putExtra("date", date);
                            intent.putExtra("place", place);
                            intent.putExtra("email", email);
                            intent.putExtra("uid", uid);
                            intent.putExtra("userid", userid);
                            intent.putExtra("des", des);
                            intent.putExtra("cat", cat);
                            intent.putExtra("country", country);
                            intent.putExtra("address", address);
                            startActivity(intent);
                            Toast.makeText(MapsActivity.this, "Viewing item in detail", Toast.LENGTH_LONG).show();
                        }
                    });
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
                 mMap.getUiSettings().setZoomControlsEnabled(true);
                 mMap.getUiSettings().setMyLocationButtonEnabled(true);
                 final int finalI = i;
                 mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            Intent intent = new Intent(MapsActivity.this, DetailedViewActivity.class);
                            String title = dataFound.get(finalI).getTitle();
                            String date = dataFound.get(finalI).getDate();
                            String place = dataFound.get(finalI).getLocation();
                            String email = dataFound.get(finalI).getEmail();
                            String uid = dataFound.get(finalI).getId();
                            String userid = dataFound.get(finalI).getUserId();
                            String des = dataFound.get(finalI).getDescription();
                            String cat = dataFound.get(finalI).getCategory();
                            String country = dataFound.get(finalI).getCountry();
                            String address = dataFound.get(finalI).getAddress();
                            intent.putExtra("title", title);
                            intent.putExtra("date", date);
                            intent.putExtra("place", place);
                            intent.putExtra("email", email);
                            intent.putExtra("uid", uid);
                            intent.putExtra("userid", userid);
                            intent.putExtra("des", des);
                            intent.putExtra("cat", cat);
                            intent.putExtra("country", country);
                            intent.putExtra("address", address);
                            startActivity(intent);
                            Toast.makeText(MapsActivity.this, "Viewing item in detail",
                                    Toast.LENGTH_LONG).show();
                        }
                 });
             }
        }

    }

    //Getting lost data latitude and longitude
    private void getLatLon(){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lat.clear();
                lon.clear();
                data.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        Post p = d.getValue(Post.class);
                        lat.add(p.getLatitude());
                        lon.add(p.getLongitude());
                        tit.add(p.getTitle());
                        data.add(p);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Getting found data latitude and longitude
    private void getFoundLatLon(){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                flat.clear();
                flon.clear();
                dataFound.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        Post p = d.getValue(Post.class);
                        flat.add(p.getLatitude());
                        flon.add(p.getLongitude());
                        ftit.add(p.getTitle());
                        dataFound.add(p);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void popUpView(View v){
        final EditText ti, lo, from, to;
        final Spinner ca, countriesspinner, choosespinner;
        Button apply, cancel;
        ImageButton imgFrom, imgTo;
        mDialog.setContentView(R.layout.layout_mappopupview);

        ti = (EditText) mDialog.findViewById(R.id.edt_ti);
        //lo = (EditText) mDialog.findViewById(R.id.edt_lo);
        from = (EditText) mDialog.findViewById(R.id.edt_datefrom);
        to = (EditText) mDialog.findViewById(R.id.edt_dateto);
        ca = (Spinner) mDialog.findViewById(R.id.spin_ca);
        apply = (Button) mDialog.findViewById(R.id.btn_show);
        cancel = (Button) mDialog.findViewById(R.id.btn_cancel);
        imgFrom = (ImageButton) mDialog.findViewById(R.id.from_date);
        imgTo = (ImageButton) mDialog.findViewById(R.id.to_date);
        countriesspinner = (Spinner) mDialog.findViewById(R.id.spin_countries);
        choosespinner = (Spinner) mDialog.findViewById(R.id.spin_items);

        from.setEnabled(false);
        to.setEnabled(false);

        Locale[] loc = Locale.getAvailableLocales();
        ArrayList<String> listOfCountries = new ArrayList<String>();
        String country;
        for( Locale l : loc ){
            country = l.getDisplayCountry();
            if( country.length() > 0 && !listOfCountries.contains(country) ){
                listOfCountries.add( country );
            }
        }
        Collections.sort(listOfCountries, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> countriesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listOfCountries);
        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countriesspinner.setAdapter(countriesAdapter);

        imgFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int y = cal.get(Calendar.YEAR);
                int m = cal.get(Calendar.MONTH);
                int da = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog d = new DatePickerDialog(
                        MapsActivity.this,
                        android.R.style.Theme_Holo_Light,
                        dateSetListener,
                        y,m,da);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                d.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int m = month + 1;
                String dateSet = dayOfMonth + "-" + m + "-" + year;
                from.setText(dateSet);
                from.setError(null);
                to.setError(null);
            }
        };

        imgTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int y = cal.get(Calendar.YEAR);
                int m = cal.get(Calendar.MONTH);
                int da = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog d = new DatePickerDialog(
                        MapsActivity.this,
                        android.R.style.Theme_Holo_Light,
                        toDateSetListener,
                        y,m,da);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                d.show();
            }
        });

        toDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int m = month + 1;
                String dateSet = dayOfMonth + "-" + m + "-" + year;
                to.setText(dateSet);
                from.setError(null);
                to.setError(null);

            }
        };



        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ca.setAdapter(adapter);

        final ArrayAdapter<CharSequence> itemschooseadapter = ArrayAdapter.createFromResource(this, R.array.chooseitems_array, android.R.layout.simple_spinner_item);
        itemschooseadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choosespinner.setAdapter(itemschooseadapter);


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t = ti.getText().toString();
                //String l = lo.getText().toString();
                String l = "";
                String df = from.getText().toString();
                String dt = to.getText().toString();
                String c = ca.getSelectedItem().toString();
                String country = countriesspinner.getSelectedItem().toString();
                String choose = choosespinner.getSelectedItem().toString();

                if(dt.length() != 0 && df.length() == 0){
                    from.setError("Please enter From date");
                    Toast.makeText(getApplicationContext(), "Enter from date", Toast.LENGTH_LONG).show();
                    return;
                }

                if(df.length() != 0){
                    if(dt.compareTo(df) < 0 || dt.compareTo(df) == 0){
                        to.setError("Please check To date");
                        Toast.makeText(getApplicationContext(), "To date should be after from date", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if(choose.equals("Lost")){
                    ref = FirebaseDatabase.getInstance().getReference().child("Lost");
                    filterData(t, l, df, dt, c, country);
                    mDialog.dismiss();
                    refreshFilterData();
                }

                if(choose.equals("Found")){
                    ref = FirebaseDatabase.getInstance().getReference().child("Found");
                    filterDataFound(t, l, df, dt, c, country);
                    mDialog.dismiss();
                    refreshFilterData();
                }

                if(choose.equals("Both")){
                    ref = FirebaseDatabase.getInstance().getReference().child("Lost");
                    filterData(t, l, df, dt, c, country);
                    ref = FirebaseDatabase.getInstance().getReference().child("Found");
                    filterDataFound(t, l, df, dt, c, country);
                    mDialog.dismiss();
                    refreshFilterData();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void refreshFilterData() {
        filteredMarkers();
    }

    private void filteredMarkers(){
        mMap.clear();

        BitmapDescriptor bitmapDescriptor1
                = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_YELLOW);

        if(filteredData.size() != 0){
            for(int i = 0; i < filteredData.size(); i++){
                LatLng mark = new LatLng(Double.valueOf(filteredData.get(i).getLatitude()), Double.valueOf(filteredData.get(i).getLongitude()));
                mMap.addMarker(new MarkerOptions().position(mark).title(filteredData.get(i).getTitle())).setIcon(bitmapDescriptor1);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mark));
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                final int finalI = i;
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(MapsActivity.this, DetailedViewActivity.class);
                        String title = filteredData.get(finalI).getTitle();
                        String date = filteredData.get(finalI).getDate();
                        String place = filteredData.get(finalI).getLocation();
                        String email = filteredData.get(finalI).getEmail();
                        String uid = filteredData.get(finalI).getId();
                        String userid = filteredData.get(finalI).getUserId();
                        String des = filteredData.get(finalI).getDescription();
                        String cat = filteredData.get(finalI).getCategory();
                        String country = filteredData.get(finalI).getCountry();
                        String address = filteredData.get(finalI).getAddress();
                        intent.putExtra("title", title);
                        intent.putExtra("date", date);
                        intent.putExtra("place", place);
                        intent.putExtra("email", email);
                        intent.putExtra("uid", uid);
                        intent.putExtra("userid", userid);
                        intent.putExtra("des", des);
                        intent.putExtra("cat", cat);
                        intent.putExtra("country", country);
                        intent.putExtra("address", address);
                        startActivity(intent);
                        Toast.makeText(MapsActivity.this, "Viewing item in detail",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        BitmapDescriptor bitmapDescriptor
                = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_BLUE);

        if(filteredDataFound.size() != 0){
            for(int i = 0; i < filteredDataFound.size(); i++){
                LatLng mark = new LatLng(Double.valueOf(filteredDataFound.get(i).getLatitude()), Double.valueOf(filteredDataFound.get(i).getLongitude()));
                mMap.addMarker(new MarkerOptions().position(mark).title(filteredDataFound.get(i).getTitle())).setIcon(bitmapDescriptor);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mark));
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                final int finalI = i;
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(MapsActivity.this, DetailedViewActivity.class);
                        String title = filteredDataFound.get(finalI).getTitle();
                        String date = filteredDataFound.get(finalI).getDate();
                        String place = filteredDataFound.get(finalI).getLocation();
                        String email = filteredDataFound.get(finalI).getEmail();
                        String uid = filteredDataFound.get(finalI).getId();
                        String userid = filteredDataFound.get(finalI).getUserId();
                        String des = filteredDataFound.get(finalI).getDescription();
                        String cat = filteredDataFound.get(finalI).getCategory();
                        String country = filteredDataFound.get(finalI).getCountry();
                        String address = filteredDataFound.get(finalI).getAddress();
                        intent.putExtra("title", title);
                        intent.putExtra("date", date);
                        intent.putExtra("place", place);
                        intent.putExtra("email", email);
                        intent.putExtra("uid", uid);
                        intent.putExtra("userid", userid);
                        intent.putExtra("des", des);
                        intent.putExtra("cat", cat);
                        intent.putExtra("country", country);
                        intent.putExtra("address", address);
                        startActivity(intent);
                        Toast.makeText(MapsActivity.this, "Viewing item in detail",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private void filterData(String tit, String loc, String datef, String datet, String cat, String cou){
        String t = tit;
        String l = loc;
        String df = datef;
        String dt = datet;
        String c = cat;
        String country = cou;
        filteredData.clear();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        //All empty
        if(t.length() == 0 && country.length() == 0 && c.length() == 0){
            for(int i = 0; i < data.size(); i++){
                filteredData.add(data.get(i));
            }
        }

        //only title
        if(t.length() != 0 && country.length() == 0 && c.length() == 0 && df.length() == 0){
            for(int i = 0; i < data.size(); i++){
                if(data.get(i).getTitle().equals(t)){
                    filteredData.add(data.get(i));
                }
            }
        }

        //only country
        if(t.length() == 0 && country.length() != 0 && c.length() == 0 && df.length() == 0){
            for(int i = 0; i < data.size(); i++){
                if(data.get(i).getCountry().equals(country)){
                    filteredData.add(data.get(i));
                }
            }
        }

        //only category
        if(t.length() == 0 && country.length() == 0 && c.length() != 0 && df.length() == 0){
            for(int i = 0; i < data.size(); i++){
                if(data.get(i).getCategory().equals(c)){
                    filteredData.add(data.get(i));
                }
            }
        }

        //only date
        if(t.length() == 0 && country.length() == 0 && c.length() == 0 && df.length() != 0 && dt.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }

        //title and country
        if(t.length() != 0 && country.length() != 0 && c.length() == 0 && df.length() == 0){
            for(int i = 0; i < data.size(); i++){
                if(data.get(i).getTitle().equals(t) && data.get(i).getCountry().equals(country)){
                    filteredData.add(data.get(i));
                }
            }
        }

        //title and category
        if(t.length() != 0 && country.length() == 0 && c.length() != 0 && df.length() == 0){
            for(int i = 0; i < data.size(); i++){
                if(data.get(i).getCategory().equals(c) && data.get(i).getTitle().equals(t)){
                    filteredData.add(data.get(i));
                }
            }
        }

        //title, date
        if(t.length() != 0 && country.length() == 0 && c.length() == 0 && df.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(data.get(i).getTitle().equals(t) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }

        //country, category
        if(t.length() == 0 && country.length() != 0 && c.length() != 0 && df.length() == 0){
            for(int i = 0; i < data.size(); i++){
                if(data.get(i).getCategory().equals(c) && data.get(i).getCountry().equals(country)){
                    filteredData.add(data.get(i));
                }
            }
        }

        //country, date
        if(t.length() == 0 && country.length() != 0 && c.length() == 0 && df.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(data.get(i).getCountry().equals(country) && data.get(i).getTitle().equals(t) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }

        //category, date
        if(t.length() == 0 && country.length() == 0 && c.length() != 0 && df.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(data.get(i).getCategory().equals(c) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }

        //title, country, category
        if(t.length() != 0 && country.length() != 0 && c.length() != 0 && df.length() == 0){
            for(int i = 0; i < data.size(); i++){
                if(data.get(i).getTitle().equals(t) && data.get(i).getCountry().equals(country) && data.get(i).getCategory().equals(c)){
                    filteredData.add(data.get(i));
                }
            }
        }

        //title, country, date
        if(t.length() != 0 && country.length() != 0 && c.length() == 0 && df.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(data.get(i).getTitle().equals(t) && data.get(i).getCountry().equals(country) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }

        //title, category, date
        if(t.length() != 0 && country.length() == 0 && c.length() != 0 && df.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(data.get(i).getTitle().equals(t) && data.get(i).getCategory().equals(c) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }


        //country, category, date
        if(t.length() == 0 && country.length() != 0 && c.length() != 0 && df.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(data.get(i).getCategory().equals(c) && data.get(i).getCountry().equals(country) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }

        //title, country, category and date
        if(t.length() != 0 && country.length() != 0 && c.length() != 0 && df.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(data.get(i).getTitle().equals(t) && data.get(i).getCategory().equals(c) && data.get(i).getCountry().equals(country) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }
    }

    private void filterDataFound(String tit, String loc, String datef, String datet, String cat, String cou){
        String t = tit;
        String l = loc;
        String df = datef;
        String dt = datet;
        String c = cat;
        String country = cou;
        filteredDataFound.clear();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        //All empty
        if(t.length() == 0 && country.length() == 0 && c.length() == 0){
            for(int i = 0; i < dataFound.size(); i++){
                filteredDataFound.add(dataFound.get(i));
            }
        }

        //only title
        if(t.length() != 0 && country.length() == 0 && c.length() == 0 && df.length() == 0){
            for(int i = 0; i < dataFound.size(); i++){
                if(dataFound.get(i).getTitle().equals(t)){
                    filteredDataFound.add(dataFound.get(i));
                }
            }
        }

        //only country
        if(t.length() == 0 && country.length() != 0 && c.length() == 0 && df.length() == 0){
            for(int i = 0; i < dataFound.size(); i++){
                if(dataFound.get(i).getCountry().equals(country)){
                    filteredDataFound.add(dataFound.get(i));
                }
            }
        }

        //only category
        if(t.length() == 0 && country.length() == 0 && c.length() != 0 && df.length() == 0){
            for(int i = 0; i < dataFound.size(); i++){
                if(dataFound.get(i).getCategory().equals(c)){
                    filteredDataFound.add(dataFound.get(i));
                }
            }
        }

        //only date
        if(t.length() == 0 && country.length() == 0 && c.length() == 0 && df.length() != 0 && dt.length() != 0){
            for(int i = 0; i < dataFound.size(); i++){
                String dateeee = dataFound.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredDataFound.add(dataFound.get(i));
                }
            }
        }

        //title and country
        if(t.length() != 0 && country.length() != 0 && c.length() == 0 && df.length() == 0){
            for(int i = 0; i < dataFound.size(); i++){
                if(dataFound.get(i).getTitle().equals(t) && dataFound.get(i).getCountry().equals(country)){
                    filteredDataFound.add(dataFound.get(i));
                }
            }
        }

        //title and category
        if(t.length() != 0 && country.length() == 0 && c.length() != 0 && df.length() == 0){
            for(int i = 0; i < data.size(); i++){
                if(dataFound.get(i).getCategory().equals(c) && dataFound.get(i).getTitle().equals(t)){
                    filteredDataFound.add(dataFound.get(i));
                }
            }
        }

        //title, date
        if(t.length() != 0 && country.length() == 0 && c.length() == 0 && df.length() != 0){
            for(int i = 0; i < dataFound.size(); i++){
                String dateeee = dataFound.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(dataFound.get(i).getTitle().equals(t) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredDataFound.add(dataFound.get(i));
                }
            }
        }

        //country, category
        if(t.length() == 0 && country.length() != 0 && c.length() != 0 && df.length() == 0){
            for(int i = 0; i < dataFound.size(); i++){
                if(dataFound.get(i).getCategory().equals(c) && dataFound.get(i).getCountry().equals(country)){
                    filteredDataFound.add(dataFound.get(i));
                }
            }
        }

        //country, date
        if(t.length() == 0 && country.length() != 0 && c.length() == 0 && df.length() != 0){
            for(int i = 0; i < dataFound.size(); i++){
                String dateeee = dataFound.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(dataFound.get(i).getCountry().equals(country) && dataFound.get(i).getTitle().equals(t) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredDataFound.add(dataFound.get(i));
                }
            }
        }

        //category, date
        if(t.length() == 0 && country.length() == 0 && c.length() != 0 && df.length() != 0){
            for(int i = 0; i < dataFound.size(); i++){
                String dateeee = dataFound.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(dataFound.get(i).getCategory().equals(c) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredDataFound.add(dataFound.get(i));
                }
            }
        }

        //title, country, category
        if(t.length() != 0 && country.length() != 0 && c.length() != 0 && df.length() == 0){
            for(int i = 0; i < dataFound.size(); i++){
                if(dataFound.get(i).getTitle().equals(t) && dataFound.get(i).getCountry().equals(country) && dataFound.get(i).getCategory().equals(c)){
                    filteredDataFound.add(dataFound.get(i));
                }
            }
        }

        //title, country, date
        if(t.length() != 0 && country.length() != 0 && c.length() == 0 && df.length() != 0){
            for(int i = 0; i < dataFound.size(); i++){
                String dateeee = dataFound.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(dataFound.get(i).getTitle().equals(t) && dataFound.get(i).getCountry().equals(country) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredDataFound.add(dataFound.get(i));
                }
            }
        }

        //title, category, date
        if(t.length() != 0 && country.length() == 0 && c.length() != 0 && df.length() != 0){
            for(int i = 0; i < dataFound.size(); i++){
                String dateeee = dataFound.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(dataFound.get(i).getTitle().equals(t) && dataFound.get(i).getCategory().equals(c) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredDataFound.add(dataFound.get(i));
                }
            }
        }

        //country, category, date
        if(t.length() == 0 && country.length() != 0 && c.length() != 0 && df.length() != 0){
            for(int i = 0; i < dataFound.size(); i++){
                String dateeee = dataFound.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(dataFound.get(i).getCategory().equals(c) && dataFound.get(i).getCountry().equals(country) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredDataFound.add(dataFound.get(i));
                }
            }
        }

        //title, country, category and date
        if(t.length() != 0 && country.length() != 0 && c.length() != 0 && df.length() != 0){
            for(int i = 0; i < dataFound.size(); i++){
                String dateeee = dataFound.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(dataFound.get(i).getTitle().equals(t) && dataFound.get(i).getCategory().equals(c) && dataFound.get(i).getCountry().equals(country) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredDataFound.add(dataFound.get(i));
                }
            }
        }
    }

    private void sleepThread(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

}
