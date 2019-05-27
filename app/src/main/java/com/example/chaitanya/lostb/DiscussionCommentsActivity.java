package com.example.chaitanya.lostb;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiscussionCommentsActivity extends AppCompatActivity {

    TextView txtEmail, txtMsg, txtDate, txtCmt;
    ListView lv;
    FirebaseUser mUser;
    DatabaseReference ref;
    private String id, email, msg, date;
    ArrayList<DiscussionModel> cmtsData;
    private CommentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_comments);
        setTitle("Comments");

        txtEmail = (TextView) findViewById(R.id.txt_emailid);
        txtMsg = (TextView) findViewById(R.id.txt_post);
        txtDate = (TextView) findViewById(R.id.txt_date);
        txtCmt = (TextView) findViewById(R.id.txt_cmt);
        lv = (ListView) findViewById(R.id.cmts_lv);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        cmtsData = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_disadd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComments();
            }
        });

        Intent i= getIntent();
        Bundle b = i.getExtras();

        if(b!=null)
        {
            id = (String) b.get("id");
            email = (String) b.get("email");
            msg = (String) b.get("msg");
            date = (String) b.get("date");
            txtEmail.setText(email);
            txtMsg.setText(msg);
            txtDate.setText(date);
        }

        getCommentsData();
    }

    private void addComments(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Add Comment");
        final EditText post = new EditText(DiscussionCommentsActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        post.setLayoutParams(params);
        b.setView(post);
        b.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String idComment = "";
                String email = mUser.getEmail();
                String msg = post.getText().toString();
                String date = String.valueOf(System.currentTimeMillis());

                //ref = FirebaseDatabase.getInstance().getReference().child("Comments");
                //id = ref.push().getKey();
                ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(id);
                idComment = ref.push().getKey();
                ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(id).child(idComment);
                DiscussionModel dm = new DiscussionModel(idComment, email, msg, date);
                ref.setValue(dm);
                getCommentsData();
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

    private void getCommentsData(){
        ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cmtsData.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        DiscussionModel dm = d.getValue(DiscussionModel.class);
                        cmtsData.add(dm);
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
        adapter = new CommentsAdapter(this, cmtsData);
        lv.setAdapter(adapter);
    }
}
