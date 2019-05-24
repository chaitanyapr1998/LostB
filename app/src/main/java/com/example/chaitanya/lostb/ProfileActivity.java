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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    FirebaseUser user;
    Uri proPicUri;
    StorageReference mSRef;
    String imageURL;
    private ProgressBar progressBar;
    Button myLost, myFound;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");

        mSRef = FirebaseStorage.getInstance().getReference();

        profilePic = (CircleImageView)findViewById(R.id.cir_pic);
        id = (TextView)findViewById(R.id.txt_email);
        his = (TextView)findViewById(R.id.txt_his);
        progressBar = (ProgressBar)findViewById(R.id.prog_profile);
        myLost = (Button)findViewById(R.id.btnmylostitems);
        myFound = (Button)findViewById(R.id.btnmyfounditems);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String email = user.getEmail();
            id.setText(email);
        }

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePicChooser();
            }
        });

        myLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MyLostActivity.class);
                startActivity(intent);
            }
        });

        myFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MyFoundActivity.class);
                startActivity(intent);
            }
        });

        displayProfilePic();
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
                    getUploadedProPicLink();
                    Toast.makeText(ProfileActivity.this, "Profile Pic Uploaded",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getUploadedProPicLink(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("pp").child(user.getUid());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageURL = uri.toString();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Link");
                ref.setValue(imageURL);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

    private void displayProfilePic(){
        progressBar.setVisibility(View.VISIBLE);
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
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_remove) {
            removeProfilePic();
        } else if(id == R.id.action_lochis) {
            Intent intent = new Intent(ProfileActivity.this, LocationHistory.class);
            startActivity(intent);
        } else if(id == R.id.action_geofence){
            Intent intent = new Intent(ProfileActivity.this, GeofenceActivity.class);
            startActivity(intent);
        } else if(id == R.id.action_geofencepost){
            Intent intent = new Intent(ProfileActivity.this, LocationPostActivity.class);
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
