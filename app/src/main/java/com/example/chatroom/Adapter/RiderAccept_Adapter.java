package com.example.chatroom.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chatroom.R;
import com.example.chatroom.Routing;
import com.example.chatroom.Trips;
import com.example.chatroom.Update_Info;
import com.example.chatroom.Users;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RiderAccept_Adapter extends RecyclerView.Adapter<RiderAccept_Adapter.ViewHolder> {

    DatabaseReference databaseReference;

    private final String[] drivers;
    private final Trips t;
    private Context context;

    public RiderAccept_Adapter(Context context,String[] drivers,Trips t){
        this.context = context;
        this.drivers = drivers;
        this.t = t;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rider_accept,viewGroup,false);
        return new RiderAccept_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(drivers[i]);
        //String name = String.valueOf(dataSnapshot.child("username").getValue());
        //viewHolder.drivername.setText(drivers[i]);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = String.valueOf(dataSnapshot.child("username").getValue());
                if(name == null) {

                    viewHolder.accept.setVisibility(View.INVISIBLE);
                    viewHolder.drivername1.setVisibility(View.INVISIBLE);
                    viewHolder.drivername.setVisibility(View.INVISIBLE);

                }
                else{
                    viewHolder.drivername.setVisibility(View.VISIBLE);
                    viewHolder.drivername.setText(name);
                    viewHolder.accept.setVisibility(View.VISIBLE);
                    viewHolder.drivername1.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                t.setStatus("Accept");
                t.setDriverid(drivers[i]);
                DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Trips").child(t.getTrip_id());
                dr.setValue(t);
                Intent i = new Intent(context, Routing.class);
                i.putExtra("trip",t);
                context.startActivity(i);


            }
        });

    }

    @Override
    public int getItemCount() {
        return drivers.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView drivername,drivername1;
        public Button accept;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);

            drivername = itemView.findViewById(R.id.drivername);
            accept = itemView.findViewById(R.id.accept1);
            drivername1 = itemView.findViewById(R.id.drivername1);

        }
    }

}
