package com.example.chaitanya.lostb;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InboxActivity extends AppCompatActivity {

    RecyclerView v;
    ChatRecyclerviewAdapter adapter;
    ArrayList<String> mChats = new ArrayList<>();
    DatabaseReference mRef;
    FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        v = (RecyclerView)findViewById(R.id.inbox_rec);
        v.setLayoutManager(new LinearLayoutManager(this));

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mRef = FirebaseDatabase.getInstance().getReference("Chat");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChats.clear();

                for(DataSnapshot d : dataSnapshot.getChildren()){
                    ChatModel cm = d.getValue(ChatModel.class);
                    if(cm.getFrom().equals(mUser.getUid())){
                        mChats.add(cm.getTo());
                    }
                    if(cm.getTo().equals(mUser.getUid())){
                        mChats.add(cm.getFrom());
                    }
                }

                chats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void chats(){
        mChats = new ArrayList<>();

        mRef = FirebaseDatabase.getInstance().getReference("Chat");
    }
}
