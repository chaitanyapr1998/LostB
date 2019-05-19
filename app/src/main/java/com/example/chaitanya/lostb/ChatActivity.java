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
    import android.widget.Toast;

    import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.Query;
    import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;

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

    APIService apiService;
    boolean notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        apiService = NClient.getClient("https://fcm.googleapis.com/").create(APIService.class);
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

        String email = toEmail;
        String uid = toUserid;

        setTitle(toEmail);
//        setTitle(toUserid);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String from = mUser.getUid();
                String to = toUserid;
                String msg = editMsg.getText().toString();

                if(msg.isEmpty()){
                   //Empty message
                } else {
                    send(from, to, msg);
                    displayMsg(from, to);
                    editMsg.setText("");
                }

            }
        });

        displayMsg(mUser.getUid(), toUserid);


    }

    private void sendNotifiaction(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    NTokens token = snapshot.getValue(NTokens.class);
                    NData data = new NData(mUser.getUid(), username+": "+message, "New Message",
                            toUserid, username);

                    NSender sender = new NSender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<NMyResponse>() {
                                @Override
                                public void onResponse(Call<NMyResponse> call, Response<NMyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<NMyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void send(String f, final String t, String m){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hm = new HashMap<>();
        hm.put("from", f);
        hm.put("to", t);
        hm.put("msg", m);

        databaseReference.child("Chat").push().setValue(hm);

        final String msgg = m;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);

                if (notify) {
                    sendNotifiaction(t, user.getEmail(), msgg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
