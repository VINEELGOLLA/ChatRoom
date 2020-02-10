package com.example.chatroom.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.chatroom.Adapter.Chat_Adapter;
import com.example.chatroom.Chat_room;
import com.example.chatroom.Group_Chat;
import com.example.chatroom.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class Chat_Fragment extends Fragment {

    private RecyclerView recycler;
    private RecyclerView.LayoutManager layout;
    private ArrayList<Chat_room> chat_group;
    FirebaseDatabase database;
    DatabaseReference myref;
    FirebaseUser firebaseUser;
    RecyclerView.Adapter mAdapter;



    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups = new ArrayList <>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        database=FirebaseDatabase.getInstance();
        myref=database.getReference("chat_groups");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println(("user_name "+firebaseUser.getEmail()+""));

        View view = inflater.inflate(R.layout.fragment_chat_, container, false);

        /*listView = view.findViewById(R.id.listview_chat);
        arrayAdapter = new ArrayAdapter <String>(getContext(),android.R.layout.simple_list_item_1,list_of_groups);
        listView.setAdapter(arrayAdapter);*/

        read_chatrooms();


        recycler = view.findViewById(R.id.listview_chat);
        recycler.setHasFixedSize(true);
        layout = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layout);
        //chat_group = new ArrayList<Chat_room>();
        //read_chatrooms();
        mAdapter = new Chat_Adapter(list_of_groups, getContext());


        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, int position , long id) {
                String currentGroupName = parent.getItemAtPosition(position).toString();
                Intent intent = new Intent(getContext(), Group_Chat.class);
                intent.putExtra("groupname",currentGroupName);
                startActivity(intent);


            }
        });*/

        return view;
    }
    private void read_chatrooms(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set <String> set = new HashSet <>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                list_of_groups.clear();
                list_of_groups.addAll(set);
                recycler.setAdapter(mAdapter);

                //arrayAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


