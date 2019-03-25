package com.example.chaitanya.lostb;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karan.churi.PermissionManager.PermissionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LostActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

//    ArrayList<String> mTitle = new ArrayList<>();
//    ArrayList<String> mDate = new ArrayList<>();
//    ArrayList<String> mLoc = new ArrayList<>();
//    ArrayList<String> mImages = new ArrayList<>();

    ArrayList<Post> data;
    ArrayList<Post> test;
    ArrayList<Post> search = new ArrayList<Post>();
    ArrayList<String> uq = new ArrayList<String>();
    RecyclerView v;
    DatabaseReference ref;
    RecyclerviewAdapter adapter;
    private static int ij = 0;
    private static int check = 1;

    PermissionManager permission;

    EditText edtSearch;
    ImageButton btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LostActivity.this, PostLostItems.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        edtSearch = (EditText)findViewById(R.id.edt_search);
        btnSearch = (ImageButton)findViewById(R.id.btn_search);

        data = new ArrayList<Post>();
        v = (RecyclerView)findViewById(R.id.lost_recyclerview);
        v.setLayoutManager(new LinearLayoutManager(this));

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = edtSearch.getText().toString();
                Query searchQuery = ref.orderByChild("Title").startAt(text).endAt(text + "\uf8ff");
                searchQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            search.clear();
                            for(DataSnapshot d : dataSnapshot.getChildren()){
                                Post p = d.getValue(Post.class);
                                search.add(p);
                            }
                            searchRefresh();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        ref = FirebaseDatabase.getInstance().getReference().child("Lost");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                check = check + 1;

                for(DataSnapshot d : dataSnapshot.getChildren()){
                    //data.clear();
                    if(ij == 0){
                        Post p = d.getValue(Post.class);
                        data.add(p);
                    }
                    if(check == 6){
                        //data.clear();
                        Post p = d.getValue(Post.class);
                        test.add(p);
                        data.addAll(test);
                    }

                }
                ij = ij + 1;


//                if(ij == 0){
//                    adapter.notifyDataSetChanged();
//                    ij = ij + 1;
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adapter = new RecyclerviewAdapter(LostActivity.this, data);
        v.setAdapter(adapter);

        permission = new PermissionManager() {};
        permission.checkAndRequestPermissions(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        permission.checkResult(requestCode,permissions, grantResults);
        //To get Granted Permission and Denied Permission
        ArrayList<String> granted=permission.getStatus().get(0).granted;
        ArrayList<String> denied=permission.getStatus().get(0).denied;

        for(String i : granted){
            Toast.makeText(LostActivity.this, i,
                    Toast.LENGTH_LONG).show();
        }
    }



    private void refreshData(){
        System.out.print("Hello");
        adapter = new RecyclerviewAdapter(LostActivity.this, data);
        v.setAdapter(adapter);
    }

    private void searchRefresh(){
        adapter = new RecyclerviewAdapter(LostActivity.this, search);
        v.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            refreshData();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(LostActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_lost) {
            Intent intent = new Intent(LostActivity.this, LostActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_found) {

        } else if (id == R.id.nav_inbox) {

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(LostActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent intent = new Intent(LostActivity.this, MainActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
