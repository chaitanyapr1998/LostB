package com.example.chaitanya.lostb;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


//Adapter to display comments posted by the users
public class CommentsAdapter extends BaseAdapter {

    Context context;
    private List<DiscussionModel> data;

    public CommentsAdapter(Context context, List<DiscussionModel> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.comments_listitem, null);
        TextView email = (TextView) v.findViewById(R.id.txt_oemailid);
        TextView msg = (TextView) v.findViewById(R.id.txt_omsg);
        email.setText(data.get(position).getEmailId());
        msg.setText(data.get(position).getMsg());
        v.setTag(data.get(position).getId());
        return v;
    }


}
