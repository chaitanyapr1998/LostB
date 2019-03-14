package com.example.chaitanya.lostb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    ImageView profilePic;
    TextView id, his;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePic = (ImageView)findViewById(R.id.img_pro);
        id = (TextView)findViewById(R.id.txt_email);
        his = (TextView)findViewById(R.id.txt_his);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
