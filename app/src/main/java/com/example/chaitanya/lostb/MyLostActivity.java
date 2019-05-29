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

//My lost items page in the app
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
    public static final int DATE_DIFF = 50;
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

        getPostedByMe();
    }

    //Getting lost items data posted by the user
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

    //To refresh my lost items data
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

    //Check if the user posted date exceeds 30 days
    private void checkPostedDate(){
        if(p.size() != 0){
            for(int aaa = 0; aaa < p.size(); aaa++){
                String datePosted = p.get(aaa).getPostedDate();
                String ctm = String.valueOf(System.currentTimeMillis());
                String dateToday = convertTime(ctm, "yyyy-MM-dd");
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

                String dateDiffStr = Long.toString(differenceDates);

                int dateDiff = Integer.parseInt(dateDiffStr);

                if(dateDiff < 50){

                } else {
                    progess = 100;
                    exipedPost.add(p.get(aaa));
                    prg.add(progess);
                }

            }

        }
        dia();
    }

    //If there are expired post, posted by the user then show the multichooser dialog to keep or remove the item
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
                    for(int i = 0; i < tit.length; i++){
                        if(chk[i]){
                            idNum[i] = i;
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Lost").child(exipedPost.get(i).getId());
                            ref.removeValue();
                            getPostedByMeRefresh();
                        } else {
                            idNum[i] = -1;
                        }
                    }
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

    //To update the user posted lost item list view
    private void refresh(){
        adapter = new CustomAdapter(this, p);
        listView.setAdapter(adapter);
    }
}
