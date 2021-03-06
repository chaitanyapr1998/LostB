package com.example.chaitanya.lostb;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

//Adapter to display my lost items
public class CustomAdapter extends BaseAdapter {

    private Context context;
    private List<Post> items;
    private List<Integer> prg;
    DatabaseReference ref;
    int pos;
    int progress;

    public CustomAdapter(Context context, List<Post> items) {
        this.context = context;
        this.items = items;
//        this.prg = prg;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.lostfragment_listitem, null);
        TextView tit = (TextView) v.findViewById(R.id.txt_item);
        ImageButton edit = (ImageButton) v.findViewById(R.id.btn_edit);
        ImageButton del = (ImageButton) v.findViewById(R.id.btn_del);
        //ProgressBar pg = (ProgressBar) v.findViewById(R.id.progressBar);
        tit.setText(items.get(position).getTitle());
        //pg.setProgress(prg.get(position));
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PostLostItems.class);
                String tit = items.get(position).getTitle();
                String des = items.get(position).getDescription();
                String date = items.get(position).getDate();
                String cat = items.get(position).getCategory();
                String loc = items.get(position).getLocation();
                String id = items.get(position).getId();
                String lat = items.get(position).getLatitude();
                String lon = items.get(position).getLongitude();
                String country = items.get(position).getCountry();
                String street = items.get(position).getStreet();
                i.putExtra("title", tit);
                i.putExtra("date", date);
                i.putExtra("loc", loc);
                i.putExtra("id", id);
                i.putExtra("des", des);
                i.putExtra("cat", cat);
                i.putExtra("lat", lat);
                i.putExtra("lon", lon);
                i.putExtra("cou", country);
                i.putExtra("strt", street);
                context.startActivity(i);

                Toast.makeText(context, "Edit",
                        Toast.LENGTH_LONG).show();
            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = FirebaseDatabase.getInstance().getReference().child("Lost").child(items.get(position).getId());
                ref.removeValue(); //removes item from the database
                pos = position; //used to remove item from the recycler view
                refreshItems();
                Toast.makeText(context, "Deleted",
                        Toast.LENGTH_LONG).show();
            }
        });
        v.setTag(items.get(position).getId());
        return v;
    }

    public void refreshItems() {
        this.items.remove(pos);
        notifyDataSetChanged();
    }
}
