package com.example.chatroom.Adapter;


import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatroom.R;
import com.example.chatroom.Routing;
import com.example.chatroom.Trips;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;

import java.io.Serializable;
import java.util.ArrayList;

public class details_Adapter extends RecyclerView.Adapter<details_Adapter.ViewHolder> {
    private Context context;
    private DatabaseReference mRef,mRef2;

    String ridername;
    private int flag = -1;
    ArrayList<Trips> trip_details;
    private String template="Ride by ";
    String username;
    private Trips t1;
    public details_Adapter(ArrayList<Trips> trip_details,String username,Context context){
        System.out.println("Recyclerview got created");
        System.out.println("Size of trips in adapter "+trip_details.size());
        this.trip_details=trip_details;
        this.username=username;
        this.context = context;
    }
    @NonNull
    @Override
    public details_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        System.out.println("Inside Oncreate method in adapter");
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.details,viewGroup,false);
        ViewHolder v_h=new ViewHolder(view);
        return v_h;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            System.out.println("Inside bindview at position " + i);
            Trips trip = trip_details.get(i);
            mRef =  FirebaseDatabase.getInstance().getReference("Users").child(trip.getRiderid());

            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ridername = String.valueOf(dataSnapshot.child("username").getValue());
                    viewHolder.username.setText(ridername);
                }



                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Trips");
        db.child(trip.getTrip_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trip.setDriverid(dataSnapshot.getValue(Trips.class).getDriverid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

            System.out.println("dest_name " + trip.dest_name);
            //viewHolder.username.setText(ridername);
            viewHolder.from.setText(trip.source_name);
            viewHolder.to.setText(trip.dest_name);


            viewHolder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Trips");
                    Trips t = trip;
                    String tripid = t.getTrip_id();



                    if(t.getDriverid().equals("NA") || t.getDriverid().equals(null)){
                        System.out.println("Driver id:"+ t.getDriverid());
                        String user = username;
                        t.setDriverid(user);
                    }
                    else {
                        System.out.println("came in else; "+ t.getDriverid());
                        String user = t.getDriverid() + "," + username;
                        t.setDriverid(user);
                    }

                    System.out.println("Trip id" + t);

                    db.child(t.getTrip_id()).setValue(t);
                    viewHolder.accept.setVisibility(View.GONE);
                    Sprite wave = new Wave();
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                    viewHolder.progressBar.setIndeterminateDrawable(wave);
                    //viewHolder.accept.setEnabled(false);


                }
            });
            t1 = trip;
        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("Trips");
        db1.child(t1.getTrip_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Trips t = dataSnapshot.getValue(Trips.class);
                    System.out.println("new terip:" + t);
                    if (t.getStatus().equals( "Accept") && t.getDriverid().equals(username)) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        Intent i = new Intent(context, Routing.class);
                        i.putExtra("trip", t);
                        context.startActivity(i);
                    }else if(t.getStatus().equals("cancel")){
                        Toast.makeText(context,"The ride is Canceled by the User",Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return trip_details.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView from;
        TextView to;
        Button accept;
        ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=(TextView)itemView.findViewById(R.id.details_username);
            from=(TextView)itemView.findViewById(R.id.details_from);
            to=(TextView)itemView.findViewById(R.id.details_to);
            accept = itemView.findViewById(R.id.details_accept);
            progressBar = itemView.findViewById(R.id.spin_kit2);
        }
    }
}