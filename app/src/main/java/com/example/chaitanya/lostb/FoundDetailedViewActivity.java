package com.example.chaitanya.lostb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

//Display found item in detail page in the app
public class FoundDetailedViewActivity extends AppCompatActivity {

    TextView txtT, txtD, txtP, tVal, dVal, pVal, cVal;
    TextView txtDes, txtCat, txtPostby, desVal, catVal, postbyVal, txtCou;
    Button btnEmail, btnChat, btnDir;
    ImageView img;
    private String postedByEmail, uid, userid;
    private String p;
    ArrayList<String> disImg;
    DatabaseReference ref;
    ArrayList<String> imgName;
    FirebaseUser mUser;

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
        txtCou = (TextView)findViewById(R.id.txt_country);
        cVal = (TextView)findViewById(R.id.country_val);
        txtDes = (TextView)findViewById(R.id.txt_des);
        txtCat = (TextView)findViewById(R.id.txt_cat);
        txtPostby = (TextView)findViewById(R.id.txt_postedby);
        desVal = (TextView)findViewById(R.id.des_val);
        catVal = (TextView)findViewById(R.id.cat_val);
        postbyVal = (TextView)findViewById(R.id.postedby_val);

        btnEmail = (Button)findViewById(R.id.btn_email);
        btnChat = (Button)findViewById(R.id.btn_chat);
        btnDir = (Button)findViewById(R.id.btn_dir);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        disImg = new ArrayList<>();
        imgName = new ArrayList<>();

        Intent i= getIntent();
        Bundle b = i.getExtras();

        if(b!=null)
        {
            String t = (String) b.get("title");
            String d = (String) b.get("date");
            String des = (String) b.get("des");
            String cat = (String) b.get("cat");
            p = (String) b.get("place");
            postedByEmail = (String) b.get("email");
            uid = (String) b.get("uid");
            userid = (String) b.get("userid");
            String country = (String) b.get("country");
            String address = (String) b.get("address");
            tVal.setText(t);
            dVal.setText(d);
            pVal.setText(p);
            desVal.setText(des);
            catVal.setText(cat);
            cVal.setText(country);
            if(mUser.getEmail().equals(postedByEmail)){
                postbyVal.setText("You");
            } else {
                postbyVal.setText(postedByEmail);
            }
        }

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", postedByEmail, null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Findingg App");
                startActivity(Intent.createChooser(intent, "Choose one"));
            }
        });

        //Disable button so user won't chat with himself
        if(mUser.getEmail().equals(postedByEmail)){
            btnChat.setEnabled(false);
        }

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoundDetailedViewActivity.this, ChatActivity.class);
                i.putExtra("toEmail", postedByEmail);
                i.putExtra("uid", uid);
                i.putExtra("userid", userid);
                startActivity(i);
            }
        });

        btnDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + p);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        displayItemImages();
    }

    //To display found items images to the user
    private void displayItemImages(){
        ref = FirebaseDatabase.getInstance().getReference().child("FoundImgMeta").child(uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                if(value instanceof List) {
                    List<Object> values = (List<Object>) value;
                    for(int i = 0; i < values.size(); i++){
                        imgName.add(values.get(i).toString());
                    }

                }
                else {

                }
                getUri();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //To get uri of the found item image posted by the user
    private void getUri(){
        for(int h = 0; h < imgName.size(); h++){
            String abc = imgName.get(h);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("UploadImages").child(uid).child(abc);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    disImg.add(uri.toString());
                    recView();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
    }

    //To update images of the found items posted by the user
    private void recView(){
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.rec_images);
        recyclerView.setLayoutManager(manager);
        ImagesRecyclerviewAdapter a = new ImagesRecyclerviewAdapter(this, disImg);
        recyclerView.setAdapter(a);
    }
}
