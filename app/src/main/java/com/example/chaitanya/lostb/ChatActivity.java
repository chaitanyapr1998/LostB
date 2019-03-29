package com.example.chaitanya.lostb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    RecyclerView v;
    ChatRecyclerviewAdapter adapter;
    ArrayList<ChatModel> mChatData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        v = (RecyclerView)findViewById(R.id.chat_recyclerview);
        v.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChatRecyclerviewAdapter(ChatActivity.this, mChatData);
        v.setAdapter(adapter);
    }
}
