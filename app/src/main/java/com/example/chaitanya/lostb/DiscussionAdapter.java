package com.example.chaitanya.lostb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DiscussionAdapter extends BaseAdapter {

    private Context context;
    private List<DiscussionModel> data;
    DatabaseReference ref;
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    int pos;

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
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.layout_dis);
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
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String id = data.get(position).getId();
                String email = data.get(position).getEmailId();
                String msg = data.get(position).getMsg();
                int pos = position;
                showDeleteDialog(id, email, msg, pos);
                return true;
            }
        });
        v.setTag(data.get(position).getId());
        return v;
    }

    public static String convertTime(String timeInMilli,String timeFormat) {
        return DateFormat.format(timeFormat, Long.parseLong(timeInMilli)).toString();
    }

    private void showDeleteDialog(final String id, String email, String msg, final int p){
        if(email.equals(mUser.getEmail())){
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle("Do you want to delete this post? \n" + msg);
            b.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ref = FirebaseDatabase.getInstance().getReference().child("Discussions").child(id);
                    ref.removeValue();
                    pos = p;
                    refreshData();
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                }
            });
            b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog dialog = b.create();
            dialog.show();
        } else {
            Toast.makeText(context, "Others post can't be deleted", Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshData() {
        this.data.remove(pos);
        notifyDataSetChanged();
    }

}
