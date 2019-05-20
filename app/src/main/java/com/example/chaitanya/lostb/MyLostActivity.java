package com.example.chaitanya.lostb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyLostActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> title = new ArrayList<>();
    ArrayList<Post> p ;
    FirebaseUser mUser;
    Context c;
    DatabaseReference ref;
    private CustomAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lost);
        setTitle("My Lost Items");

        listView = (ListView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.prog_lost);
        progressBar.setVisibility(View.VISIBLE);
        p = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        getPostedByMe();
        //sleepThread();
        adapter = new CustomAdapter(this, p);


    }

//    private void getTitleData(){
//        if(p != null){
//            for(int i = 0; i < p.size(); i++){
//                title.add(p.get(i).getTitle());
//            }
//        }
//    }

    private void getPostedByMe(){
        ref = FirebaseDatabase.getInstance().getReference().child("Lost");
        Query q = ref.orderByChild("userId").equalTo(mUser.getUid());
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                p.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        Post post = d.getValue(Post.class);
                        p.add(post);
                        Log.i("My Lost Activity", "Here");
                    }
//                    getTitleData();
                    listView.setAdapter(adapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        progressBar.setVisibility(View.GONE);
    }

    private void sleepThread(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
