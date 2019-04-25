package com.example.chaitanya.lostb;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FoundRecyclerviewAdapter extends RecyclerView.Adapter<FoundRecyclerviewAdapter.ViewHolder>{

    private Context mContext;

    private ArrayList<Post> mData ;

    public FoundRecyclerviewAdapter(Context mContext, ArrayList<Post> mData) {
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
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
//        Glide.with(mContext)
//                .asBitmap()
//                .load(mImages.get(i))
//                .into(viewHolder.image);
        viewHolder.title.setText(mData.get(i).getTitle());
        viewHolder.date.setText(mData.get(i).getDate());
        viewHolder.place.setText(mData.get(i).getLocation());
        viewHolder.country.setText(mData.get(i).getCountry());
        if(mData.get(i).getCategory().equals("Automobile")){
            viewHolder.image.setImageResource(R.drawable.baseline_directions_car_black_18);
        }
        if(mData.get(i).getCategory().equals("Electronics")){
            viewHolder.image.setImageResource(R.drawable.baseline_phone_iphone_black_18);
        }
        if(mData.get(i).getCategory().equals("People")){
            viewHolder.image.setImageResource(R.drawable.baseline_people_black_18);
        }
        if(mData.get(i).getCategory().equals("Pets")){
            viewHolder.image.setImageResource(R.drawable.baseline_pets_black_18);
        }
        if(mData.get(i).getCategory().equals("Jewellery")){
            viewHolder.image.setImageResource(R.drawable.baseline_euro_symbol_black_18);
        }
        if(mData.get(i).getCategory().equals("Others")){
            viewHolder.image.setImageResource(R.drawable.baseline_panorama_fish_eye_black_18);
        }
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FoundDetailedViewActivity.class);
                String title = mData.get(i).getTitle();
                String date = mData.get(i).getDate();
                String place = mData.get(i).getLocation();
                String email = mData.get(i).getEmail();
                String uid = mData.get(i).getId();
                String userid = mData.get(i).getUserId();
                String des = mData.get(i).getDescription();
                String cat = mData.get(i).getCategory();
                String country = mData.get(i).getCountry();
                String address = mData.get(i).getAddress();
                intent.putExtra("title", title);
                intent.putExtra("date", date);
                intent.putExtra("place", place);
                intent.putExtra("email", email);
                intent.putExtra("uid", uid);
                intent.putExtra("userid", userid);
                intent.putExtra("des", des);
                intent.putExtra("cat", cat);
                intent.putExtra("country", country);
                intent.putExtra("address", address);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title, date, place, country;
        ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.txt_ltitle);
            date = itemView.findViewById(R.id.txt_ldate);
            place = itemView.findViewById(R.id.txt_lloc);
            country = itemView.findViewById(R.id.txt_country);
            layout = itemView.findViewById(R.id.layout_listitem);
        }
    }
}
