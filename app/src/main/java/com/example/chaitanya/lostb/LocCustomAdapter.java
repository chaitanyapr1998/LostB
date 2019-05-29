package com.example.chaitanya.lostb;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

//Adapter to display user location history
public class LocCustomAdapter extends BaseAdapter {

    private Context context;
    private List<LocationModel> items;

    public LocCustomAdapter(Context context, List<LocationModel> items) {
        this.context = context;
        this.items = items;
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
        View v = View.inflate(context, R.layout.lochis_listitem, null);
        TextView time = (TextView) v.findViewById(R.id.txt_time);
        TextView address = (TextView) v.findViewById(R.id.txt_address);
        ImageButton direction = (ImageButton) v.findViewById(R.id.btn_direction);
        String timee = convertTime(items.get(position).getTime(),"hh:mm");
        time.setText(timee);
        address.setText(items.get(position).getAddress());
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + items.get(position));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });
        v.setTag(items.get(position));
        return v;
    }

    //Convert time from milliseconds to readable time
    public static String convertTime(String timeInMilli,String timeFormat) {
        return DateFormat.format(timeFormat, Long.parseLong(timeInMilli)).toString();
    }
}
