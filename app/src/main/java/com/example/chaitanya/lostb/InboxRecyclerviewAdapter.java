package com.example.chaitanya.lostb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class InboxRecyclerviewAdapter extends RecyclerView.Adapter<InboxRecyclerviewAdapter.ViewHolder>{

    private Context mContext;

    private ArrayList<ChatModel> mChat;

    public InboxRecyclerviewAdapter(Context mContext, ArrayList<ChatModel> mChat) {
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
//        Glide.with(mContext)
//                .asBitmap()
//                .load(mImages.get(i))
//                .into(viewHolder.image);
        viewHolder.email.setText(mChat.get(i).getFrom());
        viewHolder.msg.setText(mChat.get(i).getMsg());
//        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), DetailedViewActivity.class);
//                String email = mChat.get(i).getE();
//                String date = mChat.get(i).getDate();
//                String place = mChat.get(i).getLocation();
//                intent.putExtra("title", title);
//                intent.putExtra("date", date);
//                intent.putExtra("place", place);
//                v.getContext().startActivity(intent);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView email, msg;
        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.chat_email);
            msg = itemView.findViewById(R.id.chat_msg);
            layout = itemView.findViewById(R.id.layout_chatlistitem);
        }
    }
}
