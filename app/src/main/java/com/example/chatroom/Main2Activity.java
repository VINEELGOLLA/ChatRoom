package com.example.chatroom;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatroom.Fragments.Chat_Fragment;
import com.example.chatroom.Fragments.User_Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class Main2Activity extends AppCompatActivity {

    TextView username;
    ImageView profileimage;
    Button rides;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference,memRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //getSupportActionBar().hide();


        Main2Activity.this.setTitle("GroupChats");

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Login");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        username = findViewById(R.id.username);
        profileimage = findViewById(R.id.profile_image);
        rides = findViewById(R.id.rides);

        rides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this,UserTrips.class);
                startActivity(intent);
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                username.setText(users.getUsername());
                //profileimage.setImageResource(R.mipmap.ic_launcher);
                Picasso.get().load(users.getImageurl()).placeholder(R.mipmap.loading).into(profileimage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, Update_Info.class);
                intent.putExtra("userid", firebaseUser.getUid());
                startActivity(intent);
            }
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        viewpagerAdapter viewpagerAdapter = new viewpagerAdapter(getSupportFragmentManager());
        viewpagerAdapter.addFragment(new Chat_Fragment(),"Chats");
        viewpagerAdapter.addFragment(new User_Fragment(),"Users");

        viewPager.setAdapter(viewpagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Main2Activity.this,MainActivity.class));
                finish();
                return true;

            case R.id.addgroup:
                RequestNewGroup();
                Log.d("hi","came");
                return true;
        }

        return false;
    }

    private void RequestNewGroup() {
        Log.d("bye","came");
        AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name");


        final EditText groupNameField = new EditText(Main2Activity.this);
        groupNameField.setHint("E.g Group Name");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String GroupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(GroupName)){
                    Toast.makeText(Main2Activity.this,"Enter Group name",Toast.LENGTH_LONG).show();
                }
                else{
                    CreateNewGroup(GroupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void CreateNewGroup(final String GroupName){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups");
         reference.child(GroupName).setValue("")
                 .addOnCompleteListener(new OnCompleteListener <Void>() {
                     @Override
                     public void onComplete(@NonNull Task <Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Main2Activity.this,GroupName + " created",Toast.LENGTH_LONG).show();
                        }
                     }
                 });
    }

    class viewpagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;


        public viewpagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList <>();
            this.titles = new ArrayList <>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);

        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position){
            return titles.get(position);
        }
    }

    private void status(String status){
        memRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap = new HashMap <>();
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

