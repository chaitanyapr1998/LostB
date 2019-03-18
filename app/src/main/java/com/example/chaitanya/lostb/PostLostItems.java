package com.example.chaitanya.lostb;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Calendar;

public class PostLostItems extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText title, description, date, location;
    Spinner category;
    Button submit, cancel;
    String unique;
    ImageButton dateBtn, locBtn;
    private static int num = 0;
    private static String userId = MainActivity.uid;
    //private DatePickerDialog.OnDateSetListener mDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private int PLACE_PICKER_REQUEST = 1;

    //FirebaseAuth mAuth;

    DatabaseReference mRootReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_lost_items);

        title = (EditText)findViewById(R.id.edt_title);
        description = (EditText)findViewById(R.id.edt_description);
        date = (EditText)findViewById(R.id.edt_date);
        location = (EditText)findViewById(R.id.edt_loc);
        submit = (Button)findViewById(R.id.btn_submit);
        cancel = (Button)findViewById(R.id.btn_cancel);
        dateBtn = (ImageButton)findViewById(R.id.dateBtn);
        locBtn = (ImageButton)findViewById(R.id.locBtn);

        category = (Spinner)findViewById(R.id.spin_cat);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(this);


// Create a new Places client instance.
        //PlacesClient placesClient = Places.createClient(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t = title.getText().toString();
                String d = description.getText().toString();
                String da = date.getText().toString();
                String l = location.getText().toString();

                incrementingNumber();

                DatabaseReference childReference = mRootReference.child("Title");
                childReference.setValue(t);

                DatabaseReference childReference2 = mRootReference.child("Description");
                childReference2.setValue(d);

                DatabaseReference childReference3 = mRootReference.child("Date");
                childReference3.setValue(da);

                DatabaseReference childReference4 = mRootReference.child("Location");
                childReference4.setValue(l);

                DatabaseReference childReference5 = mRootReference.child("Id");
                childReference5.setValue(unique);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("");
                description.setText("");
                date.setText("");
                location.setText("");
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
                        PostLostItems.this,
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
                    intent =builder.build(PostLostItems.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
//                String toastMsg = String.format("Place: %s", place.getName());
                String toastMsg = place.getName().toString();
                location.setText(toastMsg);
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void incrementingNumber(){
//        num = num + 1;
        String time = String.valueOf(System.currentTimeMillis());
        unique = time + userId;
        mRootReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://lostb-48c7c.firebaseio.com/Lost/" + unique);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String cat = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
