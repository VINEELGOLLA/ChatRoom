package com.example.chatroom;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.chatroom.Adapter.Active_Group_Adapter;
import com.example.chatroom.Adapter.Group_Adapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Group_Chat extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton imageButton,plus;
    private EditText editText;
    private ScrollView scrollView;
    private TextView textView;
    private RecyclerView mRecyclerView,mRecyclerView2;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ListView listView = null;
    Button imageButton1;
    ImageButton noti;
    ImageView notification;



    private String currentGroupName,currentUserID,currentUserName,currentdate,currenttime,currentuserimage;
    private FirebaseAuth mAuth;
    public DatabaseReference mRef,GroupnameRef,GroupmsgkeyRef,memRef,actmem,noti1;

    StringBuilder s = new StringBuilder(100);

    List<String> items = new ArrayList<String>();
    List<String> items2 = new ArrayList<String>();



    ArrayList<Usermessages> usermessages;
    Usermessages get;

    public static String group_key="group";
    int z =0;


    //ArrayList <Activegroupusers> userids;
    //Activegroupusers get2;

    //android.support.v7.app.ActionBar actionBar = getSupportActionBar();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group__chat);

        //android.support.v7.app.ActionBar actionBar = getSupportActionBar();


        //toolbar = findViewById(R.id.group_chat_barlayout);
        //setSupportActionBar(toolbar);

        currentGroupName = getIntent().getExtras().get("group").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupnameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("Messages");
        memRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("Members");
        actmem = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("ActiveMembers");
        noti1 = FirebaseDatabase.getInstance().getReference("Groups").child(currentGroupName).child("Trips");


        getUserInfo();

        usermessages = new ArrayList<>();




        //imageButton = findViewById(R.id.send_message);
        imageButton1 = findViewById(R.id.send_message);
        editText = findViewById(R.id.input);
        mRecyclerView=findViewById(R.id.recyclerview_group);
        mRecyclerView2 = findViewById(R.id.active_recyclerview_group);
        plus = findViewById(R.id.plus);
        noti = findViewById(R.id.rides);
        notification = findViewById(R.id.notification);

        int greenColorValue = Color.parseColor("#00ff00");
        int blackcolorvalue = Color.parseColor("#000000");



        currentGroupName = getIntent().getExtras().get("group").toString();
        getSupportActionBar().setTitle(currentGroupName);
        Toast.makeText(Group_Chat.this,currentGroupName,Toast.LENGTH_LONG).show();

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                savetoDatabase();

            }
        });

        noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Group_Chat.this,ride_details.class);
                intent.putExtra(group_key,currentGroupName);
                startActivity(intent);
            }
        });

        readmemGroups();
        readActivemembers();
        //actionBar.setSubtitle(s);

        



       /* noti1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                Log.d("lol123", "lol");
                noti.setBackgroundColor(greenColorValue);


                    //Log.d("lol123", "lol");
                    //noti.setBackgroundColor(greenColorValue);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("lol2","lol");
                noti.setBackgroundColor(blackcolorvalue);




            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("lo3l","lol");


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("lol4","lol");

                noti.setBackgroundColor(greenColorValue);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("lol5","lol");


            }
        });
*/


        /*noti1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(z==0) {

                    z=1;
                }else{
                    notification.setVisibility(View.VISIBLE);
                    noti.setBackgroundColor(greenColorValue);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Group_Chat.this,riderhome.class);
                intent.putExtra(group_key,currentGroupName);
                startActivity(intent);
                //finish();
            }
        });

        actmem.child(currentUserID).setValue("");






    }





    private void readActivemembers() {
        Log.d("active0","here0");

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("ActiveMembers");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items2.clear();
                //userids.clear();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){
                    String d = (((DataSnapshot)iterator.next()).getKey());
                    items2.add(d);
                    //userids.add(d);
                }


                Log.d("active4", items2.toString());

                mRecyclerView2.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(Group_Chat.this, LinearLayoutManager.HORIZONTAL,false);
                mRecyclerView2.setLayoutManager(mLayoutManager);
                mAdapter = new Active_Group_Adapter(items2);
                //mRecyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
                //dialog.dismiss();
                mRecyclerView2.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readmemGroups() {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("Members");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                s.setLength(0);

                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){
                    String d = (((DataSnapshot)iterator.next()).getKey());
                    items.add(d);
                    s.append(d +", ");
                    //items.add(((DataSnapshot)iterator.next()).getKey());
                    //s.append(((DataSnapshot)iterator.next()).getKey()+ " ");
                }
                if(s.length()>2) {
                    s.setLength(s.length() - 2);
                }

                Log.d("lol", String.valueOf(s));
                android.support.v7.app.ActionBar actionBar = getSupportActionBar();
                actionBar.setSubtitle(s);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu2) {
        super.onCreateOptionsMenu(menu2);
        getMenuInflater().inflate(R.menu.menu2, menu2);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.Groupmem:
                Groupmemlist();

        }

        return false;
    }

    private void Groupmemlist() {


        listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter <String>(this,R.layout.list_item,R.id.txtitem,items);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(Group_Chat.this,R.style.AlertDialog);
        builder.setTitle(currentGroupName + "  Members");
        builder.setCancelable(true);
        builder.setPositiveButton("ok",null);

        builder.setView(listView);
        builder.show();
    }

    @Override
    protected void onStart(){
        super.onStart();

        GroupnameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayMessages(DataSnapshot dataSnapshot){

        GroupnameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usermessages.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    get = new Usermessages();
                    get.setDate(postSnapshot.child("date").getValue().toString());
                    get.setMessage(postSnapshot.child("message").getValue().toString());
                    get.setMsgid(postSnapshot.child("msgid").getValue().toString());
                    get.setName(postSnapshot.child("name").getValue().toString());
                    get.setTime(postSnapshot.child("time").getValue().toString());
                    get.setUserid(postSnapshot.child("userid").getValue().toString());
                    get.setUserimage(postSnapshot.child("userimage").getValue().toString());
                    get.setGroupname(postSnapshot.child("groupname").getValue().toString());
                    get.setLikes(postSnapshot.child("likes").getValue().toString());
                    Log.d("msg",get.message);
                    usermessages.add(get);
                }

                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(Group_Chat.this);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new Group_Adapter(usermessages);
                //mRecyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
                //dialog.dismiss();
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void getUserInfo() {
        mRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("username").getValue().toString();
                    currentuserimage = dataSnapshot.child("imageurl").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void savetoDatabase(){
        String message = editText.getText().toString();
        String messageKey = GroupnameRef.push().getKey();
        if(TextUtils.isEmpty(message)){
            Toast.makeText(Group_Chat.this,"enter message",Toast.LENGTH_LONG).show();
        }
        else{
            Calendar ccalForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd,yyyy");
            currentdate = currentDateFormat.format(ccalForDate.getTime());

            Calendar calfortime = Calendar.getInstance();
            SimpleDateFormat currenttimeformat = new SimpleDateFormat("hh:mm a");
            currenttime = currenttimeformat.format(calfortime.getTime());

            HashMap<String,Object> Groupmessagekey = new HashMap <>();
            GroupnameRef.updateChildren(Groupmessagekey);

            GroupmsgkeyRef = GroupnameRef.child(messageKey);

            HashMap<String,Object> messageInfoMap = new HashMap <>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("userimage",currentuserimage);
            //Log.d("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentdate);
            messageInfoMap.put("time",currenttime);
            messageInfoMap.put("userid",currentUserID);
            messageInfoMap.put("msgid",messageKey);
            messageInfoMap.put("groupname",currentGroupName);
            messageInfoMap.put("likes",0);

            GroupmsgkeyRef.updateChildren(messageInfoMap);

            editText.setText("");

            memRef.child(currentUserName).setValue("");



        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        actmem.child(currentUserID).removeValue();

    }
}
