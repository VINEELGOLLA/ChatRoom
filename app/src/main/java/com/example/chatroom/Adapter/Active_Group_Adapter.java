package com.example.chatroom.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.chatroom.R;
import com.example.chatroom.Usermessages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Active_Group_Adapter extends RecyclerView.Adapter<Active_Group_Adapter.ViewHolder> {
    List <String> items2 ;
    DatabaseReference mRef,mRef2;
    //FirebaseAuth mAuth;

    public Active_Group_Adapter(List <String> items2 ) {
        this.items2 = items2;
        Log.d("here10", String.valueOf(items2));


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activemembers, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String items3 = (items2.get(position));
        Log.d("image5",items3);

        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(items3).child("imageurl");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = (String) dataSnapshot.getValue();
                Log.d("image5",id);
                Picasso.get().load(id).placeholder(R.mipmap.loading).into(holder.activemembers);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Log.d("image5",id);


        //Picasso.get().load(id).placeholder(R.mipmap.loading).into(holder.activemembers);


    }

    @Override
    public int getItemCount() {
        return items2.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView activemembers;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            activemembers = itemView.findViewById(R.id.active_profile_image);
        }
    }
}
