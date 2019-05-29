package com.example.chaitanya.lostb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

//Feedback page in the app
public class FeedbackActivity extends AppCompatActivity {

    EditText name, emailid, comments;
    TextView rateTextView;
    Button sumbit;
    RatingBar ratingBar;
    FirebaseUser mUser;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle("Feedback");
        name = (EditText) findViewById(R.id.edt_name);
        emailid = (EditText) findViewById(R.id.edt_useremail);
        comments = (EditText) findViewById(R.id.edt_comments);
        rateTextView = (TextView) findViewById(R.id.txt_rate);
        sumbit = (Button) findViewById(R.id.btn_submit);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        emailid.setText(mUser.getEmail());

        //Submits user entered data to the database
        sumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameTv = name.getText().toString();
                String emailTv = emailid.getText().toString();
                String commentsTv = comments.getText().toString();
                String ratingTv = String.valueOf(ratingBar.getRating());
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                //Checking for a valid email
                if(emailTv.matches(emailPattern) && emailTv.length() > 0){

                } else {
                    Toast.makeText(getApplicationContext(),"Invalid Email",Toast.LENGTH_SHORT).show();
                    emailid.setError("Please enter valid email address");
                    return;
                }

                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("name", nameTv);
                hm.put("emailid", emailTv);
                hm.put("comments", commentsTv);
                hm.put("rating", ratingTv);

                ref = FirebaseDatabase.getInstance().getReference().child("Feedback");
                ref.push().setValue(hm);
                Toast.makeText(getApplicationContext(),"Thanks for your feedback",Toast.LENGTH_SHORT).show();
                clearText();
            }
        });
    }

    //To clear all the edit text fields and rating bar once user clicks submit
    private void clearText(){
        name.setText("");
        comments.setText("");
        emailid.setText(mUser.getEmail());
        ratingBar.setRating(0);
    }
}
