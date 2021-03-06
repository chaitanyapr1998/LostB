package com.example.chaitanya.lostb;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

//Found items page in the app
public class FoundActivity extends AppCompatActivity {

    static ArrayList<Post> data;
    ArrayList<Post> search = new ArrayList<Post>();
    RecyclerView v;
    DatabaseReference ref;
    FoundRecyclerviewAdapter adapter;

    PermissionManager permission;
    FirebaseUser mUser;

    EditText edtSearch;
    ImageButton btnSearch;

    Dialog mDialog;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private DatePickerDialog.OnDateSetListener toDateSetListener;

    ArrayList<Post> filteredData;

    Date datadate, datefrom, dateto;

    TextView empty, filempty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);
        setTitle("Found Items");

        mDialog = new Dialog(this);

        //To add found items to the app (Opens post found items page)
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_found);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoundActivity.this, PostFoundItems.class);
                startActivity(intent);
            }
        });

        edtSearch = (EditText)findViewById(R.id.edt_search);
        btnSearch = (ImageButton)findViewById(R.id.btn_search);
        empty = (TextView)findViewById(R.id.txt_empty);
        filempty = (TextView)findViewById(R.id.txt_filterempty);
        empty.setVisibility(View.GONE);
        filempty.setVisibility(View.GONE);

        data = new ArrayList<Post>();
        filteredData = new ArrayList<>();
        v = (RecyclerView)findViewById(R.id.lost_recyclerview);
        v.setLayoutManager(new LinearLayoutManager(this));

        ref = FirebaseDatabase.getInstance().getReference().child("Found");
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

        //To search for found items
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
                            Toast toast = Toast.makeText(FoundActivity.this,"No search items found", Toast.LENGTH_SHORT);
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

        permission = new PermissionManager() {};
        permission.checkAndRequestPermissions(this);

        //Share found item info when swiped left or right
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(FoundActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Toast.makeText(FoundActivity.this, "Share", Toast.LENGTH_SHORT).show();
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
    }

    //Set empty view when the data is null
    private void emptyView(){
        if(adapter.getItemCount() <= 0){
            v.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }

    //Set empty view when data is null after filtering it
    private void emptyViewForFilter(){
        if(adapter.getItemCount() <= 0){
            v.setVisibility(View.GONE);
            filempty.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.VISIBLE);
            filempty.setVisibility(View.GONE);
        }
    }

    //To display options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lost, menu);
        return true;
    }

    //When any of the options is clicked in the options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            refreshData();
        } else if (id == R.id.action_filter) {
            popUpView(v);
        }

        return super.onOptionsItemSelected(item);
    }

    //To update recycler view found data items
    private void refreshData(){
        filempty.setVisibility(View.GONE);
        adapter = new FoundRecyclerviewAdapter(FoundActivity.this, data);
        v.setAdapter(adapter);
        emptyView();
    }

    //To update recycler view found data items after performing search operation
    private void searchRefresh(){
        adapter = new FoundRecyclerviewAdapter(FoundActivity.this, search);
        v.setAdapter(adapter);
    }

    //To show apps that are suitable to share the found item data
    private void shareRecyclerViewItem(String t, String d, String l){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Found Item - " + t + "\n" + "Date - " + d + "\n" +"Location - " + l + "\n\n" + "Findingg App");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    //Getting user input (title, dates, category, country) and performing filter in the found items data
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

    //To update recyceler view found item data after performing filter operation
    private void refreshFilterData(){
        empty.setVisibility(View.GONE);
        adapter = new FoundRecyclerviewAdapter(FoundActivity.this, filteredData);
        v.setAdapter(adapter);
        emptyViewForFilter();
    }

    //To display custom dialog for the filter
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
                        FoundActivity.this,
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
                        FoundActivity.this,
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

                ref = FirebaseDatabase.getInstance().getReference().child("Found");
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
}
