package com.example.chaitanya.lostb;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LostActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

//    ArrayList<String> mTitle = new ArrayList<>();
//    ArrayList<String> mDate = new ArrayList<>();
//    ArrayList<String> mLoc = new ArrayList<>();
//    ArrayList<String> mImages = new ArrayList<>();

    static ArrayList<Post> data;
    ArrayList<Post> test;
    ArrayList<Post> search = new ArrayList<Post>();
    ArrayList<String> uq = new ArrayList<String>();
    RecyclerView v;
    DatabaseReference ref;
    RecyclerviewAdapter adapter;
    private static int ij = 0;
    private static int check = 1;

    PermissionManager permission;
    FirebaseUser mUser;
    String uid;

    EditText edtSearch;
    ImageButton btnSearch, btnFilter;

    Dialog mDialog;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private DatePickerDialog.OnDateSetListener toDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDialog = new Dialog(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(mUser != null){
//                    uid = mUser.getUid();
//                }
                Intent intent = new Intent(LostActivity.this, PostLostItems.class);
                //intent.putExtra("uid", uid);
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
        btnFilter = (ImageButton)findViewById(R.id.btn_filter);

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

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpView(v);
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

    private void popUpView(View v){
        final EditText ti, lo, from, to;
        final Spinner ca;
        Button show, cancel;
        mDialog.setContentView(R.layout.layout_popupview);

        ti = (EditText) mDialog.findViewById(R.id.edt_ti);
        lo = (EditText) mDialog.findViewById(R.id.edt_lo);
        from = (EditText) mDialog.findViewById(R.id.edt_datefrom);
        to = (EditText) mDialog.findViewById(R.id.edt_dateto);
        ca = (Spinner) mDialog.findViewById(R.id.spin_ca);
        show = (Button) mDialog.findViewById(R.id.btn_show);
        cancel = (Button) mDialog.findViewById(R.id.btn_cancel);

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int y = cal.get(Calendar.YEAR);
                int m = cal.get(Calendar.MONTH);
                int da = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog d = new DatePickerDialog(
                        LostActivity.this,
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
                from.setText(dateSet);
            }
        };

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int y = cal.get(Calendar.YEAR);
                int m = cal.get(Calendar.MONTH);
                int da = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog d = new DatePickerDialog(
                        LostActivity.this,
                        android.R.style.Theme_Holo_Light,
                        toDateSetListener,
                        y,m,da);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                d.show();
            }
        });

        toDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int m = month + 1;
                String dateSet = dayOfMonth + "-" + m + "-" + year;
                to.setText(dateSet);
            }
        };



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ca.setAdapter(adapter);
//        ca.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t = ti.getText().toString();
                String l = lo.getText().toString();
                String df = from.getText().toString();
                String dt = to.getText().toString();
                String c = ca.getSelectedItem().toString();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
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
            Intent intent = new Intent(LostActivity.this, InboxActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(LostActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent intent = new Intent(LostActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(LostActivity.this, MapsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
