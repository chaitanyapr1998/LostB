package com.example.chaitanya.lostb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostLostItems extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText title, description, date, location;
    Spinner category;
    Button submit, cancel;
    String unique;
    private static int num = 0;
    private static String userId = MainActivity.uid;

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

        category = (Spinner)findViewById(R.id.spin_cat);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(this);

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
