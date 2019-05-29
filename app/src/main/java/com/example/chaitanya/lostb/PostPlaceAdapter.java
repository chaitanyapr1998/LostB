package com.example.chaitanya.lostb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.PlaceBuffer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

//Adapter to display post reminder places data
public class PostPlaceAdapter extends RecyclerView.Adapter<PostPlaceAdapter.PlaceViewHolder> {

    private Context mContext;
    private PlaceBuffer place;
    public static List<String> p = new ArrayList<String>();
    DatabaseReference ref;
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    int pos;

    public PostPlaceAdapter(Context context, PlaceBuffer places) {
        this.mContext = context;
        this.place = places;
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {

        TextView palceText;
        TextView addressText;
        RelativeLayout lyout;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            palceText = (TextView) itemView.findViewById(R.id.name_text_view);
            addressText = (TextView) itemView.findViewById(R.id.address_text_view);
            lyout = (RelativeLayout) itemView.findViewById(R.id.layout_geolist);
        }

    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.geofence_listitem, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, final int position) {
        String locationName = place.get(position).getName().toString();
        p.add(locationName);
        String locationAddress = place.get(position).getAddress().toString();
        holder.palceText.setText(locationName);
        holder.addressText.setText(locationAddress);
        holder.lyout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Place Adapter", "Inside onClickkkkkkkkkkkkkkkkkkkkk");
                Log.i("Place Adapter", mUser.getUid());
                Log.i("Place Adapter", place.get(position).getId());
                ref = FirebaseDatabase.getInstance().getReference().child("GeofencePost").child(mUser.getUid()).child(place.get(position).getId());
                ref.removeValue();
                pos = position;
                refreshData();
            }
        });

    }

    //To swap places with same names
    public void swapP(PlaceBuffer newPlaces){
        place = newPlaces;
        if (place != null) {
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if(place ==null) return 0;
        return place.getCount();
    }

    public List getList(){
        return p;
    }

    public void refreshData() {
        if(place.getCount() == 1){
            place = null;  //doing like this because view is not updating for the last item so when item count is 1 making it null
        }
        this.notifyDataSetChanged();
    }

}
