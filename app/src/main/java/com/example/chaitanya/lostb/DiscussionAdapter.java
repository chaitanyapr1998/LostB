package com.example.chaitanya.lostb;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DiscussionAdapter extends BaseAdapter {

    private Context context;
    private List<DiscussionModel> data;

    public DiscussionAdapter(Context context, List<DiscussionModel> data) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.discussion_listitem, null);
        TextView email = (TextView) v.findViewById(R.id.txt_emailid);
        TextView msg = (TextView) v.findViewById(R.id.txt_post);
        TextView date = (TextView) v.findViewById(R.id.txt_date);
        TextView cmt = (TextView) v.findViewById(R.id.txt_cmt);
        email.setText(data.get(position).getEmailId());
        msg.setText(data.get(position).getMsg());
        String datee = convertTime(data.get(position).getPostedDate(),"dd-MM-yyyy");
        date.setText(datee);
        cmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DiscussionCommentsActivity.class);
                intent.putExtra("id", data.get(position).getId());
                intent.putExtra("email", data.get(position).getEmailId());
                intent.putExtra("msg", data.get(position).getMsg());
                intent.putExtra("date", data.get(position).getPostedDate());
                v.getContext().startActivity(intent);
            }
        });
        v.setTag(data.get(position).getId());
        return v;
    }

    public static String convertTime(String timeInMilli,String timeFormat) {
        return DateFormat.format(timeFormat, Long.parseLong(timeInMilli)).toString();
    }
}
