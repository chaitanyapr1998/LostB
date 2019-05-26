package com.example.chaitanya.lostb;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.example.chaitanya.lostb.LocCustomAdapter.convertTime;

public class MyLostActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> title = new ArrayList<>();
    ArrayList<Post> p ;
    ArrayList<Post> exipedPost;
    FirebaseUser mUser;
    Context c;
    DatabaseReference ref;
    private CustomAdapter adapter;
    private ProgressBar progressBar;
    public static final int DATE_DIFF = 30;
    ArrayList<Integer> prg;
    Dialog mDialog;
    int progess = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lost);
        setTitle("My Lost Items");

        listView = (ListView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.prog_lost);
        progressBar.setVisibility(View.VISIBLE);
        p = new ArrayList<>();
        prg = new ArrayList<>();
        exipedPost = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        mDialog = new Dialog(this);

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        //adapter = new CustomAdapter(this, p, progess);

        getPostedByMe();

        //checkPostedDate();


    }

    private void getPostedByMe(){
        ref = FirebaseDatabase.getInstance().getReference().child("Lost");
        Query q = ref.orderByChild("userId").equalTo(mUser.getUid());
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                p.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        Post post = d.getValue(Post.class);
                        p.add(post);
                        Log.i("My Lost Activity", "Here");
                    }
                    checkPostedDate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        progressBar.setVisibility(View.GONE);
    }



    private void getPostedByMeRefresh(){
        ref = FirebaseDatabase.getInstance().getReference().child("Lost");
        Query q = ref.orderByChild("userId").equalTo(mUser.getUid());
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                p.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        Post post = d.getValue(Post.class);
                        p.add(post);
                        Log.i("My Lost Activity", "Here");
                    }
                }
                refresh();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        progressBar.setVisibility(View.GONE);
    }


    private void checkPostedDate(){
        if(p.size() != 0){
            for(int aaa = 0; aaa < p.size(); aaa++){
                String datePosted = p.get(aaa).getPostedDate();
                String ctm = String.valueOf(System.currentTimeMillis());
                String dateToday = convertTime(ctm, "yyyy-MM-dd");
                //int dateDiff = dateToday.compareTo(datePosted);
                Date date1 = null;
                Date date2 = null;

                SimpleDateFormat dates = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    date1 = dates.parse(dateToday);
                    date2 = dates.parse(datePosted);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long difference = Math.abs(date1.getTime() - date2.getTime());
                long differenceDates = difference / (24 * 60 * 60 * 1000);

                //Convert long to String
                String dateDiffStr = Long.toString(differenceDates);

                int dateDiff = Integer.parseInt(dateDiffStr);

                if(dateDiff < 50){
//                    progess = dateDiff * 2;
//                    exipedPost.add(p.get(aaa));
//                    prg.add(progess);
                } else {
                    progess = 100;
                    exipedPost.add(p.get(aaa));
                    prg.add(progess);
                }

            }

        }
        dia();
        //refresh();
    }

    private void dia(){
        final String[] tit = new String[exipedPost.size()];
        final String[] id = new String[exipedPost.size()];
        final boolean[] chk = new boolean[exipedPost.size()];
        final int[] idNum = new int[exipedPost.size()];
        if(exipedPost.size() != 0){
            for(int i = 0; i < exipedPost.size(); i++){
                tit[i] = exipedPost.get(i).getTitle();
                id[i] = exipedPost.get(i).getId();
                chk[i] = false;
            }
        }

        if(exipedPost.size() != 0){
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("These posts are posted 30 days ago. Do you want to delete it?");
            b.setMultiChoiceItems(tit, chk, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    chk[which] = isChecked;
                    Toast.makeText(MyLostActivity.this, tit[which], Toast.LENGTH_LONG).show();
                }
            });
            b.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //int abc = 0;
                    for(int i = 0; i < tit.length; i++){
                        if(chk[i]){
                            idNum[i] = i;
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Lost").child(exipedPost.get(i).getId());
                            ref.removeValue();
                            getPostedByMeRefresh();
                            //abc = abc + 1;
                        } else {
                            idNum[i] = -1;
                        }
                    }
                    //showtext();
                }
            });
            b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = b.create();
            dialog.show();
        }

        refresh();

    }

    private void refresh(){
        adapter = new CustomAdapter(this, p);
        listView.setAdapter(adapter);
    }

    private void showCustomDialog(){
        TextView txtTitle;
        Button rm, cancel;
        mDialog.setContentView(R.layout.popup_mylist);

        txtTitle = (TextView) mDialog.findViewById(R.id.txt_titlemylist);
        rm = (Button) mDialog.findViewById(R.id.btn_remove);
        cancel = (Button) mDialog.findViewById(R.id.btn_cancel);
        if(exipedPost.size() != 0){
            for(int i = 0; i < exipedPost.size(); i++){
                txtTitle.setText(exipedPost.get(i).getTitle());
                final int finalI = i;
                rm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Lost").child(exipedPost.get(finalI).getId());
                        ref.removeValue();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
            mDialog.show();
        }


    }

    private void sleepThread(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }



}
