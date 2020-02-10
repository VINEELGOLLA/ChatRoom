package com.example.chatroom.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.chatroom.R;
import com.example.chatroom.Routing;
import com.example.chatroom.Trips;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User_Trip_Adapter extends RecyclerView.Adapter<User_Trip_Adapter.ViewHolder> {

    DatabaseReference databaseReference,databaseReference1,databaseReference2;

    private final String[] tripids;
    private Context context;

    public User_Trip_Adapter(Context context,String[] tripids){
        this.context = context;
        this.tripids = tripids;
    }

    @android.support.annotation.NonNull
    @Override
    public ViewHolder onCreateViewHolder(@android.support.annotation.NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_trip,viewGroup,false);
        return new User_Trip_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@android.support.annotation.NonNull ViewHolder viewHolder, final int i) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Trips").child(tripids[i]);

        //String name = String.valueOf(dataSnapshot.child("username").getValue());
        //viewHolder.drivername.setText(drivers[i]);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
                Trips trip = dataSnapshot.getValue(Trips.class);
                System.out.println("opop"+trip);

                viewHolder.pick.setText(trip.getSource_name());
                viewHolder.drop.setText(trip.getDest_name());
                viewHolder.status.setText("Status: "+ trip.getStatus());
                databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(trip.getRiderid());
                databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(trip.getDriverid());


                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String ridern = String.valueOf(dataSnapshot.child("username").getValue());
                        viewHolder.ridername.setText(ridern);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String drivern = String.valueOf(dataSnapshot.child("username").getValue());
                        viewHolder.drivername.setText(drivern);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ridern = String.valueOf(dataSnapshot.child("username").getValue());
                viewHolder.ridername.setText(ridern);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String drivern = String.valueOf(dataSnapshot.child("username").getValue());
                viewHolder.drivername.setText(drivern);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/







    }

    @Override
    public int getItemCount() {
        return tripids.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView ridername,drivername,pick,drop,status;

        public ViewHolder(View itemView) {
            super(itemView);

            ridername = itemView.findViewById(R.id.rider_name1);
            drivername = itemView.findViewById(R.id.driver_name1);
            pick = itemView.findViewById(R.id.pick);
            drop = itemView.findViewById(R.id.drop);
            status = itemView.findViewById(R.id.status_ride);


        }
    }

}

