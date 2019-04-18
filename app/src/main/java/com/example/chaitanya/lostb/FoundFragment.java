package com.example.chaitanya.lostb;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FoundFragment extends Fragment {

    ListView listView;
    ArrayList<String> title = new ArrayList<>();
    ArrayList<Post> p ;
    FirebaseUser mUser;
    Context c;
    DatabaseReference ref;
    private FoundCustomAdapter adapter;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.found_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        progressBar = (ProgressBar) view.findViewById(R.id.prog_lost);
        progressBar.setVisibility(View.VISIBLE);
        c = container.getContext();
        p = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();

        getPostedByMe();

        adapter = new FoundCustomAdapter(getContext(), p);
        listView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        return view;

    }

    private void getTitleData(){
        if(p != null){
            for(int i = 0; i < p.size(); i++){
                title.add(p.get(i).getTitle());
            }
        }
    }

    private void getPostedByMe(){
        ref = FirebaseDatabase.getInstance().getReference().child("Found");
        Query q = ref.orderByChild("userId").equalTo(mUser.getUid());
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                p.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        Post post = d.getValue(Post.class);
                        p.add(post);
                    }
                    getTitleData();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

