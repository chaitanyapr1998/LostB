package com.example.chaitanya.lostb;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LostFragment extends Fragment {

    SwipeMenuListView listView;
    ArrayList<String> title = new ArrayList<>();
    ArrayList<Post> p ;
    FirebaseUser mUser;
    Context c;
    DatabaseReference ref;
    private CustomAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lost_fragment, container, false);
        listView = (SwipeMenuListView) view.findViewById(R.id.listView);
        c = container.getContext();
        p = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();

        getPostedByMe();

        adapter = new CustomAdapter(getContext(), p);
        listView.setAdapter(adapter);
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
        ref = FirebaseDatabase.getInstance().getReference().child("Lost");
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
