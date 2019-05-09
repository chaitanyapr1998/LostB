package com.example.chaitanya.lostb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImagesRecyclerviewAdapter extends RecyclerView.Adapter<ImagesRecyclerviewAdapter.ViewHolder>{

    private Context mContext;

    private ArrayList<String> mImageUrl;

    public ImagesRecyclerviewAdapter(Context mContext, ArrayList<String> mImageUrl) {
        this.mContext = mContext;
        this.mImageUrl = mImageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_list, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Glide.with(mContext)
                .asBitmap()
                .load(mImageUrl.get(i))
                .into(viewHolder.img);
    }

    @Override
    public int getItemCount() {
        return mImageUrl.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //TextView email, msg;
        ImageView img;
        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imagesss);
            layout = itemView.findViewById(R.id.layout_chatlistitem);
        }
    }
}
