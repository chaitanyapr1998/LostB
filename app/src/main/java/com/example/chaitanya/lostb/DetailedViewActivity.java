package com.example.chaitanya.lostb;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DetailedViewActivity extends AppCompatActivity {

    TextView txtT, txtD, txtP, tVal, dVal, pVal;
    Button btnEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        setTitle("DetailedView");

        txtT = (TextView)findViewById(R.id.txt_title);
        txtD = (TextView)findViewById(R.id.txt_date);
        txtP = (TextView)findViewById(R.id.txt_place);
        tVal = (TextView)findViewById(R.id.tit_val);
        dVal = (TextView)findViewById(R.id.dat_val);
        pVal = (TextView)findViewById(R.id.pla_val);

        btnEmail = (Button)findViewById(R.id.btn_email);

        Intent i= getIntent();
        Bundle b = i.getExtras();

        if(b!=null)
        {
            String t = (String) b.get("title");
            String d = (String) b.get("date");
            String p = (String) b.get("place");
            tVal.setText(t);
            dVal.setText(d);
            pVal.setText(p);
        }

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "abc@gmail.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Findingg App");
                startActivity(Intent.createChooser(intent, "Choose one"));
            }
        });
    }
}
