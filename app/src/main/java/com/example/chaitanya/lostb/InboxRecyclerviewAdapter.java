package com.example.chaitanya.lostb;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

//Adapter to display inbox page
public class InboxRecyclerviewAdapter extends RecyclerView.Adapter<InboxRecyclerviewAdapter.ViewHolder>{

    private Context mContext;

    private ArrayList<Users> mChat;

    public InboxRecyclerviewAdapter(Context mContext, ArrayList<Users> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inbox_listitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Glide.with(mContext)
                .asBitmap()
                .load(mChat.get(i).getLink())
                .into(viewHolder.img);
        viewHolder.email.setText(mChat.get(i).getEmail());
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                String userid = mChat.get(i).getUserId();
                String email = mChat.get(i).getEmail();
                intent.putExtra("toEmail", email);
                intent.putExtra("userid", userid);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView email, msg;
        RelativeLayout layout;
        CircleImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_pp);
            email = itemView.findViewById(R.id.chat_email);
            msg = itemView.findViewById(R.id.chat_msg);
            layout = itemView.findViewById(R.id.layout_inboxlistitem);
        }
    }
}
