package com.example.chaitanya.lostb;

import android.content.Context;
import android.content.Intent;
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

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder>{

//    private ArrayList<String> mTitle = new ArrayList<>();
//    private ArrayList<String> mDate = new ArrayList<>();
//    private ArrayList<String> mLoc = new ArrayList<>();
//    private ArrayList<String> mImages = new ArrayList<>();
    private Context mContext;

    private ArrayList<Post> mData ;

    public RecyclerviewAdapter(Context mContext, ArrayList<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lost_listitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
//        Glide.with(mContext)
//                .asBitmap()
//                .load(mImages.get(i))
//                .into(viewHolder.image);
        viewHolder.title.setText(mData.get(i).getTitle());
        viewHolder.date.setText(mData.get(i).getDate());
        viewHolder.place.setText(mData.get(i).getLocation());
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailedViewActivity.class);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView title, date, place;
        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.txt_ltitle);
            date = itemView.findViewById(R.id.txt_ldate);
            place = itemView.findViewById(R.id.txt_lloc);
            layout = itemView.findViewById(R.id.layout_listitem);
        }
    }
}
