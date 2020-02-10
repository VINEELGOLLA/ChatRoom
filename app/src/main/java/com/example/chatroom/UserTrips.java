package com.example.chatroom;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.chatroom.Adapter.User_Trip_Adapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserTrips extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    private RecyclerView mRecyclerView;

    private User_Trip_Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    public Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_trips);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        mRecyclerView = findViewById(R.id.user_trips_recycler);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = dataSnapshot.getValue(Users.class);

                if(users.getTripid() != null){
                    String[] tripids = users.getTripid().split(",");

                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(UserTrips.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new User_Trip_Adapter(UserTrips.this,tripids);
                    mRecyclerView.setAdapter(mAdapter);


                }



            }





            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*if(users.getTripid() != null){
            String[] tripids = users.getTripid().split(",");

            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(UserTrips.this);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new User_Trip_Adapter(UserTrips.this,tripids);
            mRecyclerView.setAdapter(mAdapter);


        }*/








    }
}
