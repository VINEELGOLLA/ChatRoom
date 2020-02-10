package com.example.chatroom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.chatroom.Adapter.Group_Adapter;
import com.example.chatroom.Adapter.RiderAccept_Adapter;
import com.example.chatroom.Adapter.UserAdapter;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RiderAccept extends AppCompatActivity {

    DatabaseReference databaseReference;
    private int flag = -1;

    private Trips trips;

    private RecyclerView mRecyclerView;

    private RiderAccept_Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_accept);

        RiderAccept.this.setTitle("Drivers List");

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.spin_kit1);
        mRecyclerView = findViewById(R.id.recycler2);

        Sprite doubleBounce = new DoubleBounce();
        Sprite wave = new Wave();
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminateDrawable(doubleBounce);

        String messageKey = getIntent().getExtras().get("trip").toString();

        databaseReference = FirebaseDatabase.getInstance().getReference("Trips").child(messageKey);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Trips t = dataSnapshot.getValue(Trips.class);
                    trips = t;
                    String driverids = t.getDriverid();
                    if (!driverids.equals("NA")){
                        progressBar.setVisibility(View.INVISIBLE);
                        String[] drivers = driverids.split(",");

                        for (String i: drivers){
                            System.out.println("Drivers:" + i);
                        }

                        mRecyclerView.setHasFixedSize(true);
                        mLayoutManager = new LinearLayoutManager(RiderAccept.this);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mAdapter = new RiderAccept_Adapter(RiderAccept.this,drivers,t);
                        mRecyclerView.setAdapter(mAdapter);


                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(RiderAccept.this);
        builder1.setMessage("Going back will cancel the Ride");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        trips.setStatus("cancel");
                        databaseReference.setValue(trips);
                        Intent intent = new Intent(RiderAccept.this,Main2Activity.class);
                        startActivity(intent);
                        finish();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }
}
