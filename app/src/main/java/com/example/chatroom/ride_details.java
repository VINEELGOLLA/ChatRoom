package com.example.chatroom;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.chatroom.Adapter.details_Adapter;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ride_details extends AppCompatActivity {

    ArrayList<Trips> trip_details;
    FirebaseAuth firebaseAuth;
    private DatabaseReference mRef,mRef2;
    RecyclerView recyclerView;
    String username;
    public static String group_key="group";
    String currentGroupName;
    List <String> items = new ArrayList<String>();




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ride_details.this.setTitle("Riders List");
        setContentView(R.layout.activity_ride_details);
        trip_details=new ArrayList <Trips>();
        firebaseAuth=FirebaseAuth.getInstance();
        currentGroupName = getIntent().getExtras().get("group").toString();
        Log.d("group",currentGroupName);
        //String group_name=getIntent().getExtras().
        username = firebaseAuth.getCurrentUser().getUid();
        System.out.println("Username:" + username);
        mRef= FirebaseDatabase.getInstance().getReference("Trips");
        mRef2 = FirebaseDatabase.getInstance().getReference("Groups").child(currentGroupName).child("Trips");
        //fetch_trips();
        recyclerView=findViewById(R.id.recyview);
        recyclerView.setHasFixedSize(true);

        mRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();

                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){
                    String d = (((DataSnapshot)iterator.next()).getKey());
                    items.add(d);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                trip_details.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Trips trips= dataSnapshot1.getValue(Trips.class);
                    //System.out.println("dest:" + trips);
                    if(trips.getStatus().equals("pending") && !username.equals(trips.riderid) && items.contains(trips.trip_id) && trips.getStatus() != "cancel" ) {
                        trip_details.add(trips);
                        System.out.println("dest:" + trips);
                    }

                }
                Log.d("tripvineel",trip_details.toString());
                details_Adapter r_adapter=new details_Adapter(trip_details,username,getApplicationContext());
                recyclerView.setAdapter(r_adapter);
                r_adapter.notifyDataSetChanged();
                System.out.println("Size "+trip_details.size());
                for (Trips t:trip_details){
                    System.out.println(t.dest_name);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Event failed");

            }
        });

        LinearLayoutManager llm=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);


    }


}
