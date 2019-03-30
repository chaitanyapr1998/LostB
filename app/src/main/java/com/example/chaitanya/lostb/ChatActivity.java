package com.example.chaitanya.lostb;

        import android.content.Intent;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.List;

public class ChatActivity extends AppCompatActivity {

    RecyclerView v;
    ChatRecyclerviewAdapter adapter;
    ArrayList<ChatModel> mChatData = new ArrayList<>();
    DatabaseReference mRef;
    DatabaseReference mCM;
    EditText editMsg;
    Button btnSend;
    private String toEmail, toUid, toUserid;
    private Boolean check = false;
    FirebaseUser mUser;
    ChildEventListener mChildEventListener;
    List<ChatMeta> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        v = (RecyclerView)findViewById(R.id.chat_recyclerview);
        v.setLayoutManager(new LinearLayoutManager(this));

        editMsg = (EditText)findViewById(R.id.edt_message);
        btnSend = (Button)findViewById(R.id.btn_send);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        list = new ArrayList<>();

        Intent i= getIntent();
        Bundle b = i.getExtras();


        if(b!=null)
        {
            toEmail = (String) b.get("toEmail");
            toUid = (String) b.get("uid");
            toUserid = (String) b.get("userid");
        }

        setTitle(toEmail);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mUser.getEmail();
                String message = editMsg.getText().toString();
                getRef();
//                DatabaseReference childReference = mRef.child("Email");
//                childReference.push().setValue(email);
//
//                sleepThread();

                DatabaseReference childReference2 = mRef;
                childReference2.push().setValue(message);
                updateDisplay();
            }
        });

        adapter = new ChatRecyclerviewAdapter(ChatActivity.this, mChatData);
        v.setAdapter(adapter);
    }

    private void getRef(){
        String userId = toUserid;
        String myId = mUser.getUid();
        String us = userId+myId;
//        String su = senderId+userId;

        mRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://lostb-48c7c.firebaseio.com/Chat/" + us);



//        for(int i=0; i < list.size(); i++){
//            if(list.get(i) == us){
//                mRef = FirebaseDatabase.getInstance().getReference().child("Chat").child(us);
//                break;
//            } else if (list.get(i) == su){
//                mRef = FirebaseDatabase.getInstance().getReference().child("Chat").child(su);
//                break;
//            } else {
//                String r = userEmail+senderEmail;
//                list.add(r);
//                mRef = FirebaseDatabase.getInstance().getReference().child("Chat").child(r);
//                break;
//            }
//        }


//        mCM = FirebaseDatabase.getInstance()
//                .getReferenceFromUrl("https://lostb-48c7c.firebaseio.com/ChatMeta/");
//        mCM.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    for(DataSnapshot d : dataSnapshot.getChildren()){
//                        list.clear();
//                        ChatMeta m = d.getValue(ChatMeta.class);
//                        list.add(m);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        if(list.size() != 0){
//            for(int i=0; i < list.size(); i++){
//                if(su == list.get(i).getChatKey()){
//                    check = true;
//                }
//            }
//        }
//
//
//        if(check){
//            mRef = FirebaseDatabase.getInstance()
//                    .getReferenceFromUrl("https://lostb-48c7c.firebaseio.com/Chat/" + su);
//            check = false;
//        } else {
////            list.add(us);
//            mCM = FirebaseDatabase.getInstance()
//                    .getReferenceFromUrl("https://lostb-48c7c.firebaseio.com/ChatMeta/");
//            DatabaseReference childReference = mCM.child("ChatKey");
//            childReference.push().setValue(us);
//            mRef = FirebaseDatabase.getInstance()
//                    .getReferenceFromUrl("https://lostb-48c7c.firebaseio.com/Chat/" + us);
//            check = false;
//        }

    }



    private void sleepThread(){
        try {
            Thread.sleep(700);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    private void updateDisplay(){
        mRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://lostb-48c7c.firebaseio.com/Chat/");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        ChatModel cm = d.getValue(ChatModel.class);
                        mChatData.add(cm);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
