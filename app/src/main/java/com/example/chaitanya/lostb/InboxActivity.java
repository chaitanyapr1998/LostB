package com.example.chaitanya.lostb;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//Inbox page in the app
public class InboxActivity extends AppCompatActivity {

    RecyclerView v;
    InboxRecyclerviewAdapter adapter;
    ArrayList<String> mChats;
    DatabaseReference mRef;
    FirebaseUser mUser;
    List<Object> uqChatList;
    ArrayList<Users> emails;
    ArrayList<Users> uqEmails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        setTitle("Inbox");

        v = (RecyclerView)findViewById(R.id.inbox_rec);
        v.setLayoutManager(new LinearLayoutManager(this));
        mChats = new ArrayList<>();
        emails = new ArrayList<>();
        uqEmails = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mRef = FirebaseDatabase.getInstance().getReference("Chat");
        mRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChats.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        ChatModel cm = d.getValue(ChatModel.class);
                        //Getting all the chats that user sent
                        if(cm.getFrom().equals(mUser.getUid())){
                            mChats.add(cm.getTo());
                        }
                        //Getting all the chats sent to the user
                        if(cm.getTo().equals(mUser.getUid())){
                            mChats.add(cm.getFrom());
                        }
                    }
                }

                chats();
                getInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());

    }
    //mChats consists of all the user id that user has chatted with, this methods makes the id uniques (removes duplicates)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void chats(){
        uqChatList = mChats.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    //Getting email id of the person whom user have chatted with
    private void getInfo(){
        if(uqChatList != null){
            for(int i = 0; i < uqChatList.size(); i++){
                String a = uqChatList.get(i).toString();
                mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(a);
                mRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                                Users p = dataSnapshot.getValue(Users.class);
                                emails.add(p);
                                makeEmailsUnique();
                                adapter = new InboxRecyclerviewAdapter(InboxActivity.this, uqEmails);
                                v.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    //Updating user token
    private void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        NotificationTokensModel tok = new NotificationTokensModel(token);
        ref.child(mUser.getUid()).setValue(tok);
    }

    //Making email id unique in the inbox activity (To not show the same user email id twice)
    private void makeEmailsUnique(){
        uqEmails.clear();
        for (Users u : emails) {
            boolean check = false;
            for (Users e : uqEmails) {
                if (e.getUserId().equals(u.getUserId()) || (e.equals(u))) {
                    check = true;
                    break;
                }
            }
            if (!check) uqEmails.add(u);
        }
    }
}
