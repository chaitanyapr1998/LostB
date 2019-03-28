package com.example.chaitanya.lostb;

import android.content.Context;
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
import android.widget.ListView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LostFragment extends Fragment {

    ListView listView;
    ArrayList<String> title = new ArrayList<>();
    ArrayList<Post> p = LostActivity.data;
    ArrayAdapter<String> a;
    Context c;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lost_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        c = container.getContext();

//        Query q = FirebaseDatabase.getInstance().getReference("Lost")
//                .orderByChild("Email")
//                .equalTo(user.getEmail());
//        q.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//
//                    for(DataSnapshot d : dataSnapshot.getChildren()){
//
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        getTitleData();
        ArrayAdapter<String> a = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1, title);
        listView.setAdapter(a);
        return view;

    }

    private void getTitleData(){
        if(p != null){
            for(int i = 0; i < p.size(); i++){
                title.add(p.get(i).getTitle());
            }
        }
    }
}
