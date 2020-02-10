package com.example.chatroom;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;


import com.bumptech.glide.Glide;
import com.example.chatroom.Adapter.Chat_Adapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {

    ImageView profile_image;
    TextView groupname;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference,memRef;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        profile_image = findViewById(R.id.profile_image);
        groupname = findViewById(R.id.username);

        intent =  getIntent();
        Chat_room c_group= (Chat_room) intent.getSerializableExtra(Chat_Adapter.group_key);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println(firebaseUser.getUid());
        databaseReference = FirebaseDatabase.getInstance().getReference("chat_groups").child(c_group.cid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Chat_room chat_room= dataSnapshot.getValue(Chat_room.class);
                groupname.setText(chat_room.name);
                //if(users.getImageUrl().equals("default")){
                     //profile_image.setImageResource(R.mipmap.ic_launcher);
                    //}else{
                    //Glide.with(MessageActivity.this).load(users.getImageUrl()).into(profile_image);
                    //}

                profile_image.setImageResource(R.mipmap.ic_launcher);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void status(String status){
        memRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap <String,Object> hashMap = new HashMap <>();
        hashMap.put("status",status);

        memRef.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause(){
        super.onPause();
        status("offline");
    }

}
