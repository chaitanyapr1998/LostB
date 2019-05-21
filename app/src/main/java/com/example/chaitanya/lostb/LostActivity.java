package com.example.chaitanya.lostb;

import android.annotation.SuppressLint;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.MotionEvent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class LostActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    static ArrayList<Post> data;
    ArrayList<Post> search = new ArrayList<Post>();
    RecyclerView v;
    DatabaseReference ref;
    RecyclerviewAdapter adapter;

    PermissionManager permission;
    FirebaseUser mUser;

    EditText edtSearch;
    ImageButton btnSearch, btnFilter;

    Dialog mDialog;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private DatePickerDialog.OnDateSetListener toDateSetListener;

    CircleImageView ppImgView;
    TextView emailTextView;
    String imageURL;

    ArrayList<Post> filteredData;

    Date datadate, datefrom, dateto;

    TextView empty, filempty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDialog = new Dialog(this);

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

        View nav = navigationView.getHeaderView(0);
        setTitle("Lost Items");


        edtSearch = (EditText)findViewById(R.id.edt_search);
        btnSearch = (ImageButton)findViewById(R.id.btn_search);
//        btnFilter = (ImageButton)findViewById(R.id.btn_filter);
        ppImgView = (CircleImageView) nav.findViewById(R.id.propicImgView);
        emailTextView = (TextView) nav.findViewById(R.id.emailTextView);
        empty = (TextView)findViewById(R.id.txt_empty);
        filempty = (TextView)findViewById(R.id.txt_filterempty);
        empty.setVisibility(View.GONE);
        filempty.setVisibility(View.GONE);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = mUser.getEmail();
        emailTextView.setText(email);
        displayProPicNav();

        data = new ArrayList<Post>();
        filteredData = new ArrayList<>();
        v = (RecyclerView)findViewById(R.id.lost_recyclerview);
        v.setLayoutManager(new LinearLayoutManager(this));


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = edtSearch.getText().toString().toLowerCase();
                Query searchQuery = ref.orderByChild("title").startAt(text).endAt(text + "\uf8ff");
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
                        } else {
                            Toast toast = Toast.makeText(LostActivity.this,"No search items found", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

//        btnFilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


        ref = FirebaseDatabase.getInstance().getReference().child("Lost");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        Post p = d.getValue(Post.class);
                        data.add(p);
                    }
                    refreshData();
                } else {
                    refreshData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        adapter = new RecyclerviewAdapter(LostActivity.this, data);
//        v.setAdapter(adapter);
//        emptyView();
        permission = new PermissionManager() {};
        permission.checkAndRequestPermissions(this);


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(LostActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Toast.makeText(LostActivity.this, "Share ", Toast.LENGTH_SHORT).show();
                int position = viewHolder.getAdapterPosition();
                String tit = data.get(position).getTitle();
                String loc = data.get(position).getLocation();
                String dat = data.get(position).getDate();
                shareRecyclerViewItem(tit, dat, loc);
                adapter.notifyDataSetChanged();

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(v);

        //refreshData();

    }

    private void emptyView(){
        if(adapter.getItemCount() <= 0){
            v.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }

    private void emptyViewForFilter(){
        if(adapter.getItemCount() <= 0){
            v.setVisibility(View.GONE);
            filempty.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.VISIBLE);
            filempty.setVisibility(View.GONE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void popUpView(View v){
        final EditText ti, lo, from, to;
        final Spinner ca, countriesspinner;
        Button apply, cancel;
        ImageButton imgFrom, imgTo;
        mDialog.setContentView(R.layout.layout_popupview);

        ti = (EditText) mDialog.findViewById(R.id.edt_ti);
        //lo = (EditText) mDialog.findViewById(R.id.edt_lo);
        from = (EditText) mDialog.findViewById(R.id.edt_datefrom);
        to = (EditText) mDialog.findViewById(R.id.edt_dateto);
        ca = (Spinner) mDialog.findViewById(R.id.spin_ca);
        apply = (Button) mDialog.findViewById(R.id.btn_show);
        cancel = (Button) mDialog.findViewById(R.id.btn_cancel);
        imgFrom = (ImageButton) mDialog.findViewById(R.id.from_date);
        imgTo = (ImageButton) mDialog.findViewById(R.id.to_date);
        countriesspinner = (Spinner) mDialog.findViewById(R.id.spin_countries);

        from.setEnabled(false);
        to.setEnabled(false);

        Locale[] loc = Locale.getAvailableLocales();
        ArrayList<String> listOfCountries = new ArrayList<String>();
        String country;
        for( Locale l : loc ){
            country = l.getDisplayCountry();
            if( country.length() > 0 && !listOfCountries.contains(country) ){
                listOfCountries.add( country );
            }
        }
        Collections.sort(listOfCountries, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> countriesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listOfCountries);
        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countriesspinner.setAdapter(countriesAdapter);

        imgFrom.setOnClickListener(new View.OnClickListener() {
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
                from.setError(null);
                to.setError(null);
            }
        };

        imgTo.setOnClickListener(new View.OnClickListener() {
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
                from.setError(null);
                to.setError(null);

            }
        };



        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ca.setAdapter(adapter);


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t = ti.getText().toString();
                //String l = lo.getText().toString();
                String l = "";
                String df = from.getText().toString();
                String dt = to.getText().toString();
                String c = ca.getSelectedItem().toString();
                String country = countriesspinner.getSelectedItem().toString();

                if(dt.length() != 0 && df.length() == 0){
                    from.setError("Please enter From date");
                    Toast.makeText(getApplicationContext(), "Enter from date", Toast.LENGTH_LONG).show();
                    return;
                }

                if(df.length() != 0){
                    if(dt.compareTo(df) < 0 || dt.compareTo(df) == 0){
                        to.setError("Please check To date");
                        Toast.makeText(getApplicationContext(), "To date should be after from date", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                ref = FirebaseDatabase.getInstance().getReference().child("Lost");
                filterData(t, l, df, dt, c, country);
                refreshFilterData();

                mDialog.dismiss();

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
        //System.out.print("Hello");
        filempty.setVisibility(View.GONE);
        adapter = new RecyclerviewAdapter(LostActivity.this, data);
        v.setAdapter(adapter);
        emptyView();
    }

    private void searchRefresh(){
        adapter = new RecyclerviewAdapter(LostActivity.this, search);
        v.setAdapter(adapter);
        emptyView();
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
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            refreshData();
        } else if (id == R.id.action_filter){
            popUpView(v);
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
            Intent intent = new Intent(LostActivity.this, FoundActivity.class);
            startActivity(intent);
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
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(LostActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_feedback) {
            Intent intent = new Intent(LostActivity.this, FeedbackActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayProPicNav(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("pp").child(mUser.getUid());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageURL = uri.toString();
                Glide.with(getApplicationContext()).load(imageURL).into(ppImgView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

    private void shareRecyclerViewItem(String t, String d, String l){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Lost Item - " + t + "\n" + "Date - " + d + "\n" +"Location - " + l + "\n\n" + "Findingg App");
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "Date - " + d);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "Location - " + l);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void filterData(String tit, String loc, String datef, String datet, String cat, String cou){
        String t = tit;
        String l = loc;
        String df = datef;
        String dt = datet;
        String c = cat;
        String country = cou;
        filteredData.clear();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        //All empty
        if(t.length() == 0 && country.length() == 0 && c.length() == 0){
            for(int i = 0; i < data.size(); i++){
                filteredData.add(data.get(i));
            }
        }

        //only title
        if(t.length() != 0 && country.length() == 0 && c.length() == 0 && df.length() == 0){
            for(int i = 0; i < data.size(); i++){
                if(data.get(i).getTitle().equals(t)){
                    filteredData.add(data.get(i));
                }
            }
        }

        //only country
        if(t.length() == 0 && country.length() != 0 && c.length() == 0 && df.length() == 0){
            for(int i = 0; i < data.size(); i++){
                if(data.get(i).getCountry().equals(country)){
                    filteredData.add(data.get(i));
                }
            }
        }

        //only category
        if(t.length() == 0 && country.length() == 0 && c.length() != 0 && df.length() == 0){
            for(int i = 0; i < data.size(); i++){
                if(data.get(i).getCategory().equals(c)){
                    filteredData.add(data.get(i));
                }
            }
        }

        //only date
        if(t.length() == 0 && country.length() == 0 && c.length() == 0 && df.length() != 0 && dt.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }

        //title and country
        if(t.length() != 0 && country.length() != 0 && c.length() == 0 && df.length() == 0){
            for(int i = 0; i < data.size(); i++){
                if(data.get(i).getTitle().equals(t) && data.get(i).getCountry().equals(country)){
                    filteredData.add(data.get(i));
                }
            }
        }

        //title and category
        if(t.length() != 0 && country.length() == 0 && c.length() != 0 && df.length() == 0){
            for(int i = 0; i < data.size(); i++){
                if(data.get(i).getCategory().equals(c) && data.get(i).getTitle().equals(t)){
                    filteredData.add(data.get(i));
                }
            }
        }

        //title, date
        if(t.length() != 0 && country.length() == 0 && c.length() == 0 && df.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(data.get(i).getTitle().equals(t) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }

        //country, category
        if(t.length() == 0 && country.length() != 0 && c.length() != 0 && df.length() == 0){
            for(int i = 0; i < data.size(); i++){
                if(data.get(i).getCategory().equals(c) && data.get(i).getCountry().equals(country)){
                    filteredData.add(data.get(i));
                }
            }
        }

        //country, date
        if(t.length() == 0 && country.length() != 0 && c.length() == 0 && df.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(data.get(i).getCountry().equals(country) && data.get(i).getTitle().equals(t) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }

        //category, date
        if(t.length() == 0 && country.length() == 0 && c.length() != 0 && df.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(data.get(i).getCategory().equals(c) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }

        //title, country, category
        if(t.length() != 0 && country.length() != 0 && c.length() != 0 && df.length() == 0){
            for(int i = 0; i < data.size(); i++){
                if(data.get(i).getTitle().equals(t) && data.get(i).getCountry().equals(country) && data.get(i).getCategory().equals(c)){
                    filteredData.add(data.get(i));
                }
            }
        }

        //title, country, date
        if(t.length() != 0 && country.length() != 0 && c.length() == 0 && df.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(data.get(i).getTitle().equals(t) && data.get(i).getCountry().equals(country) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }

        //title, category, date
        if(t.length() != 0 && country.length() == 0 && c.length() != 0 && df.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(data.get(i).getTitle().equals(t) && data.get(i).getCategory().equals(c) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }


        //country, category, date
        if(t.length() == 0 && country.length() != 0 && c.length() != 0 && df.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(data.get(i).getCategory().equals(c) && data.get(i).getCountry().equals(country) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }

        //title, country, category and date
        if(t.length() != 0 && country.length() != 0 && c.length() != 0 && df.length() != 0){
            for(int i = 0; i < data.size(); i++){
                String dateeee = data.get(i).getDate();
                try {
                    datefrom = dateFormat.parse(df);
                    dateto = dateFormat.parse(dt);
                    datadate = dateFormat.parse(dateeee);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(data.get(i).getTitle().equals(t) && data.get(i).getCategory().equals(c) && data.get(i).getCountry().equals(country) && datadate.compareTo(datefrom) >= 0 && datadate.compareTo(dateto) <= 0){
                    filteredData.add(data.get(i));
                }
            }
        }
    }

    private void refreshFilterData(){
        empty.setVisibility(View.GONE);
        adapter = new RecyclerviewAdapter(LostActivity.this, filteredData);
        v.setAdapter(adapter);
        emptyViewForFilter();
    }
}
