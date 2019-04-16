package com.example.chaitanya.lostb;

    import android.content.Intent;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
    import android.view.LayoutInflater;
    import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
    import android.widget.LinearLayout;

    import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.HashMap;
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
            //toUid = (String) b.get("uid");
            toUserid = (String) b.get("userid");
        }

        setTitle(toEmail);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from = mUser.getUid();
                String to = toUserid;
                String msg = editMsg.getText().toString();

                send(from, to, msg);
                displayMsg(from, to);
                editMsg.setText("");
            }
        });

        displayMsg(mUser.getUid(), toUserid);


    }

    private void send(String f, String t, String m){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hm = new HashMap<>();
        hm.put("from", f);
        hm.put("to", t);
        hm.put("msg", m);

        databaseReference.child("Chat").push().setValue(hm);
    }

    private void displayMsg(final String f, final String t){
        mChatData = new ArrayList<>();

        mRef = FirebaseDatabase.getInstance().getReference().child("Chat");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChatData.clear();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    ChatModel cm = d.getValue(ChatModel.class);
                    if(cm.getTo().equals(f) && cm.getFrom().equals(t) || cm.getTo().equals(t) && cm.getFrom().equals(f)){
                        mChatData.add(cm);
                    }
                    adapter = new ChatRecyclerviewAdapter(ChatActivity.this, mChatData);
                    v.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getData(){

    }
}
