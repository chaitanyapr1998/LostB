package com.example.chaitanya.lostb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    TextView about, aboutinfo, mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("About us");

        about = (TextView) findViewById(R.id.txt_about);
        aboutinfo = (TextView) findViewById(R.id.txt_aboutinfo);
        mail = (TextView) findViewById(R.id.txt_email);

        String aboutText = "Findingg is an android application developed to help people to find their lost items or report found items";
        String aboutinfoText = "For more information please contact us";
        String mailText = "findingg@gmail.com";

        about.setText(aboutText);
        aboutinfo.setText(aboutinfoText);
        mail.setText(mailText);
    }
}
