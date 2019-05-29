package com.example.chaitanya.lostb;

    import android.content.Intent;
        import android.support.annotation.NonNull;
    import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
    import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;

    import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.Query;
    import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.HashMap;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    RecyclerView v;
    ChatRecyclerviewAdapter adapter;
    ArrayList<ChatModel> mChatData = new ArrayList<>();
    DatabaseReference mRef;
    EditText editMsg;
    Button btnSend;
    private String toEmail, toUid, toUserid;
    FirebaseUser mUser;


    APIService apiService;
    boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        apiService = NotificationClient.getClient("https://fcm.googleapis.com/").create(APIService.class);
        v = (RecyclerView)findViewById(R.id.chat_recyclerview);
        v.setLayoutManager(new LinearLayoutManager(this));


        editMsg = (EditText)findViewById(R.id.edt_message);
        btnSend = (Button)findViewById(R.id.btn_send);

        mUser = FirebaseAuth.getInstance().getCurrentUser();


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
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = true;
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

    //Send notification to the user
    private void notifyUserWithMessage(String toUser, final String useremail, final String message){
        mRef = FirebaseDatabase.getInstance().getReference().child("Tokens");
        Query query = mRef.orderByKey().equalTo(toUser);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    NotificationTokensModel token = d.getValue(NotificationTokensModel.class);
                    NotificationDataModel data = new NotificationDataModel(mUser.getUid(), useremail, message, toUserid, useremail);
                    NotificationSenderModel sentby = new NotificationSenderModel(data, token.getToken());
                    apiService.sendNotification(sentby)
                            .enqueue(new Callback<NotificationResponse>() {
                                @Override
                                public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {

                                }

                                @Override
                                public void onFailure(Call<NotificationResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Send message
    private void send(String f, final String t, String m){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hm = new HashMap<>();
        hm.put("from", f);
        hm.put("to", t);
        hm.put("msg", m);

        databaseReference.child("Chat").push().setValue(hm);

        final String msgg = m;

        mRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                if (check) {
                    notifyUserWithMessage(t, user.getEmail(), msgg);
                }
                check = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Display messages to the user
    private void displayMsg(final String f, final String t){
        mChatData = new ArrayList<>();

        mRef = FirebaseDatabase.getInstance().getReference().child("Chat");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChatData.clear();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    ChatModel chatmodel = d.getValue(ChatModel.class);
                    if(chatmodel.getTo().equals(f) && chatmodel.getFrom().equals(t) || chatmodel.getTo().equals(t) && chatmodel.getFrom().equals(f)){
                        mChatData.add(chatmodel);
                    }
                    adapter = new ChatRecyclerviewAdapter(ChatActivity.this, mChatData);
                    v.setAdapter(adapter);
                    v.scrollToPosition(mChatData.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
