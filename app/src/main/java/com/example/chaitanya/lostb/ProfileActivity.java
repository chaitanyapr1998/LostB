package com.example.chaitanya.lostb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final int PROFILE_PIC = 345;
    CircleImageView profilePic;
    TextView id, his;
    private SectionPageAdapter mSectionPageAdapter;
    private ViewPager mViewPager;
    FirebaseUser user;
    Uri proPicUri;
    StorageReference mSRef;
    String imageURL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");

        mSRef = FirebaseStorage.getInstance().getReference();

        profilePic = (CircleImageView) findViewById(R.id.cir_pic);
        id = (TextView)findViewById(R.id.txt_email);
        his = (TextView)findViewById(R.id.txt_his);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String email = user.getEmail();
            id.setText(email);
        }

        mViewPager = (ViewPager)findViewById(R.id.container);
        viewPager(mViewPager);

        TabLayout tab = (TabLayout)findViewById(R.id.tab_layout);
        tab.setupWithViewPager(mViewPager);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePicChooser();
            }
        });

        displayProfilePic();
    }

    private void viewPager(ViewPager v){
        SectionPageAdapter a = new SectionPageAdapter(getSupportFragmentManager());
        a.add(new LostFragment(), "Lost Items");
        a.add(new FoundFragment(), "Found Items");
        v.setAdapter(a);
    }

    private void profilePicChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile picture"), PROFILE_PIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PROFILE_PIC && resultCode == RESULT_OK && data != null && data.getData() != null){
            proPicUri = data.getData();
            uploadProPic(proPicUri);
            try {
                Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), proPicUri);
                profilePic.setImageBitmap(b);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadProPic(Uri proPicUri){
        String picName = user.getUid();
        StorageReference f = mSRef.child("pp").child(picName);
        f.putFile(proPicUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this, "Profile Pic Uploaded",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void displayProfilePic(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("pp").child(user.getUid());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageURL = uri.toString();
                Glide.with(getApplicationContext()).load(imageURL).into(profilePic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_remove) {
            removeProfilePic();
        } else if(id == R.id.action_lochis) {
            Intent intent = new Intent(ProfileActivity.this, LocationHistory.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void removeProfilePic(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("pp").child(user.getUid());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                profilePic.setImageResource(R.drawable.image_place_holder);
                Toast.makeText(ProfileActivity.this, "Profile Pic Removed",
                        Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }
}
