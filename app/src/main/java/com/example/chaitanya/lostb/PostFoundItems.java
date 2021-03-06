package com.example.chaitanya.lostb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

//Post found items page in the app
public class PostFoundItems extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText title, description, date, location;
    Spinner category;
    Button submit, cancel, upload;
    ImageButton dateBtn, locBtn;
    private static String userId;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private int PLACE_PICKER_REQUEST = 1;
    private int LOAD_IMAGE_CODE = 2;

    private StorageReference mStorage;
    ArrayList<String> imageUrl;
    ArrayList<Uri> imgUri;
    ArrayList<String> uqFileName;

    DatabaseReference mRootReference;
    FirebaseUser mUser;

    String address;
    LatLng latlon;

    Intent i;
    Bundle b;
    String intentId, u, latitude, longitude, cou, strt;
    String lat, lon, country, street;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_found_items);
        setTitle("Post Found Item");

        title = (EditText)findViewById(R.id.edt_title);
        description = (EditText)findViewById(R.id.edt_description);
        date = (EditText)findViewById(R.id.edt_date);
        location = (EditText)findViewById(R.id.edt_loc);
        submit = (Button)findViewById(R.id.btn_submit);
        cancel = (Button)findViewById(R.id.btn_cancel);
        dateBtn = (ImageButton)findViewById(R.id.dateBtn);
        locBtn = (ImageButton)findViewById(R.id.locBtn);
        upload = (Button)findViewById(R.id.btn_upload);

        category = (Spinner)findViewById(R.id.spin_cat);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = mUser.getUid();

        imageUrl = new ArrayList<>();
        imgUri = new ArrayList<>();
        uqFileName = new ArrayList<>();

        date.setEnabled(false);
        location.setEnabled(false);

        i= getIntent();
        b = i.getExtras();

        if(b!=null)
        {
            String t = (String) b.get("title");
            String d = (String) b.get("date");
            String des = (String) b.get("des");
            String cat = (String) b.get("cat");
            String loc = (String) b.get("loc");
            intentId = (String) b.get("id");
            latitude = (String) b.get("lat");
            longitude = (String) b.get("lon");
            cou = (String) b.get("cou");
            strt = (String) b.get("strt");
            title.setText(t);
            description.setText(des);
            location.setText(loc);
            date.setText(d);
        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t = title.getText().toString().toLowerCase();
                if(t.isEmpty()){
                    title.setError("Please enter name of an item");
                    return;
                } else {
                    title.setError(null);//removes error
                    title.clearFocus();
                }
                String d = description.getText().toString();
                String da = date.getText().toString();
                if(da.isEmpty()){
                    date.setError("Please pick a date");
                    return;
                } else {
                    date.setError(null);//removes error
                    date.clearFocus();
                }
                String loc = location.getText().toString();
                if(loc.isEmpty()){
                    location.setError("Please pick a location");
                    return;
                } else {
                    location.setError(null);//removes error
                    location.clearFocus();
                }

                String e = mUser.getEmail();
                if(b == null){
                    u = String.valueOf(System.currentTimeMillis()) + userId;
                } else {
                    u = intentId;
                }

                String ca = category.getSelectedItem().toString();
                String uid = userId;
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                String addrs = address;
                if(b == null){
                    lat = String.valueOf(latlon.latitude);
                    lon = String.valueOf(latlon.longitude);
                    country = getCountryName(getApplicationContext(), latlon.latitude, latlon.longitude);
                    street = getStreetName(getApplicationContext(), latlon.latitude, latlon.longitude);
                } else {
                    lat = latitude;
                    lon = longitude;
                    country = cou;
                    street = strt;
                }
                String tit_cou_cat = t +"_"+ country +"_"+ ca;

                uploadingToFirebase(u);

                sleepThread();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Found").child(u);
                Post p = new Post(t, da, loc, u, e, ca, uid, d, addrs, lat, lon, date, country, street, tit_cou_cat);
                ref.setValue(p);

                imgMeta(u);

                sleepThread();

                b = null;

                clearEditText();

                Toast.makeText(PostFoundItems.this, "Submitted",
                        Toast.LENGTH_LONG).show();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEditText();
            }
        });

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int y = cal.get(Calendar.YEAR);
                int m = cal.get(Calendar.MONTH);
                int da = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog d = new DatePickerDialog(
                        PostFoundItems.this,
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
                date.setText(dateSet);
            }
        };



        locBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent =builder.build(PostFoundItems.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select images"), LOAD_IMAGE_CODE);
            }
        });

        mStorage = FirebaseStorage.getInstance().getReference();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = place.getName().toString();
                address = place.getAddress().toString();
                latlon = place.getLatLng();
                location.setText(toastMsg);
            }
        }

        if(requestCode == LOAD_IMAGE_CODE && resultCode == RESULT_OK){
            if(data.getClipData() != null){
                //many images
                int itemCount = data.getClipData().getItemCount();
                for (int i = 0; i < itemCount; i++){
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imgUri.add(imageUri);
                }

            } else if (data.getData() != null){
                //one image
                Uri imageUri = data.getData();
                imgUri.add(imageUri);

            } else {
                //no image
                Toast.makeText(this, "No images selected", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String uniqueFileName(){
        String time = String.valueOf(System.currentTimeMillis());
        String u = time + userId;
        return u;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void sleepThread(){
        try {
            Thread.sleep(700);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    private void uploadingToFirebase(String u){
        for(int i = 0; i < imgUri.size(); i++){
            final String uniqueFileName = uniqueFileName();
            uqFileName.add(uniqueFileName);
            StorageReference file = mStorage.child("UploadImages").child(u).child(uniqueFileName);
            file.putFile(imgUri.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(PostFoundItems.this, "Images Uploaded",
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        imgUri.clear();

    }

    private void imgMeta(String u){
        mRootReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://lostb-48c7c.firebaseio.com/FoundImgMeta/" + u);
        for(int i = 0; i < uqFileName.size(); i++){
            int q = i;
            String k = String.valueOf(q);
            DatabaseReference childReference9 = mRootReference.child(k);
            childReference9.setValue(uqFileName.get(i));
        }

        uqFileName.clear();
    }

    //cc
    public static String getCountryName(Context context, double latitude, double longitude) {
        Geocoder geo = new Geocoder(context, Locale.getDefault());
        List<Address> countryName;
        try {
            countryName = geo.getFromLocation(latitude, longitude, 1);

            if (countryName != null && !countryName.isEmpty()) {
                return countryName.get(0).getCountryName();
            }

        } catch (IOException ignored) {
            //do something
        }
        return null;
    }

    public static String getStreetName(Context context, double latitude, double longitude) {
        Geocoder geo = new Geocoder(context, Locale.getDefault());
        List<Address> streetName;
        try {
            streetName = geo.getFromLocation(latitude, longitude, 1);

            if (streetName != null && !streetName.isEmpty()) {
                return streetName.get(0).getAdminArea();
            }

        } catch (IOException ignored) {
            //do something
        }
        return null;
    }

    private void clearEditText(){
        description.setText("");
        title.setText("");
        title.setError(null);//removes error
        title.clearFocus();
        date.setText("");
        date.setError(null);//removes error
        date.clearFocus();
        location.setText("");
        location.setError(null);//removes error
        location.clearFocus();
    }
}
