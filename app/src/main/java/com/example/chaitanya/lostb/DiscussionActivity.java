package com.example.chaitanya.lostb;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiscussionActivity extends AppCompatActivity {

    Dialog mDialog;
    ListView listView;
    ArrayList<DiscussionModel> data;
    FirebaseUser mUser;
    DatabaseReference ref;
    private DiscussionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);
        setTitle("Discussion");

        mDialog = new Dialog(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        data = new ArrayList<>();

        listView = (ListView) findViewById(R.id.lv_dis);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_disadd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPost();
            }
        });

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        getDiscussionsData();
    }

    private void getDiscussionsData(){
        ref = FirebaseDatabase.getInstance().getReference().child("Discussions");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        DiscussionModel dm = d.getValue(DiscussionModel.class);
                        data.add(dm);
                        Log.i("My Lost Activity", "Here");
                    }
                }
                refresh();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void refresh(){
        adapter = new DiscussionAdapter(this, data);
        listView.setAdapter(adapter);
    }

    private void showAddPost(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Type or ask something");
        final EditText post = new EditText(DiscussionActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        post.setLayoutParams(params);
        b.setView(post);
        b.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String id = "";
                String email = mUser.getEmail();
                String msg = post.getText().toString();
                String date = String.valueOf(System.currentTimeMillis());

                ref = FirebaseDatabase.getInstance().getReference().child("Discussions");
                id = ref.push().getKey();
                ref = FirebaseDatabase.getInstance().getReference().child("Discussions").child(id);
                DiscussionModel dm = new DiscussionModel(id, email, msg, date);
                ref.setValue(dm);
                getDiscussionsData();
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = b.create();
        dialog.show();
    }
}
