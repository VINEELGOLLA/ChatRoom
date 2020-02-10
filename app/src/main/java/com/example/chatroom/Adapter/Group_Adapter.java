package com.example.chatroom.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatroom.Group_Chat;
import com.example.chatroom.R;
import com.example.chatroom.Update_Info;
import com.example.chatroom.Usermessages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;



public class Group_Adapter extends RecyclerView.Adapter<Group_Adapter.ViewHolder> {
    ArrayList <Usermessages> mdata;
    DatabaseReference mRef,mRef2;
    String currentUserID;
    FirebaseAuth mAuth;



    public Group_Adapter(ArrayList <Usermessages> mdata) {
        this.mdata = mdata;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_messages, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Usermessages expense_data = mdata.get(position);

        holder.name.setText(expense_data.getName());
        holder.time.setText(expense_data.getTime());
        holder.date.setText(expense_data.getDate());
        holder.message.setText(expense_data.getMessage());
        holder.like.setText(expense_data.getLikes()+ " likes");
        Picasso.get().load(expense_data.getUserimage()).placeholder(R.mipmap.loading).into(holder.click);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("msg1",expense_data.getMessage());
            }
        });

        mRef2 = FirebaseDatabase.getInstance().getReference().child("Groups").child(expense_data.getGroupname()).child("Messages");




        mRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(expense_data.getGroupname()).child("Messages");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        Log.d("id",currentUserID);

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(expense_data.getUserid().equals(currentUserID)){
                    Log.d("check","here");
                    onClick2(v);
                }
                else {
                    int d = Integer.valueOf(expense_data.getLikes());
                    d = d + 1;
                    Log.d("likes", String.valueOf(d));
                    mRef2.child(expense_data.getMsgid()).child("likes").setValue(d);
                }
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("kl",expense_data.getUserid());
                Log.d("kl1",currentUserID);
                if(expense_data.getUserid().equals(currentUserID)){
                    Log.d("gggg",expense_data.getUserid());
                    Log.d("kl1",currentUserID);

                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext(),R.style.AlertDialog1);
                    builder.setTitle("Deletion Alert")
                            .setMessage("Are you sure, you want to delete this message ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mRef.child(expense_data.getMsgid()).removeValue();
                                    Toast.makeText(holder.itemView.getContext(),"Message Deleted",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(holder.itemView.getContext(),"Not Deleted",Toast.LENGTH_SHORT).show();
                                }
                            });
                    //Creating dialog box
                    AlertDialog dialog  = builder.create();
                    dialog.show();


                    //mRef.child(expense_data.getMsgid()).removeValue();

                } else{
                    onClick(v);
                }

                return false;
            }
        });


    }


    public void onClick(View v) {


        Toast.makeText(v.getContext(), "you cannot delete this message", Toast.LENGTH_SHORT).show();
    }

    public void onClick2(View v) {


        Toast.makeText(v.getContext(), "you cannot like this message", Toast.LENGTH_SHORT).show();
    }



    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView time, date, name, message,like;
        ImageView click;
        ImageButton likes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            date = itemView.findViewById(R.id.date_chat);
            message = itemView.findViewById(R.id.msg_chat);
            name = itemView.findViewById(R.id.chat_name);
            time = itemView.findViewById(R.id.time_chat);
            click = itemView.findViewById(R.id.imageView_chat);
            likes = itemView.findViewById(R.id.imageButton);
            like = itemView.findViewById(R.id.likes);



        }

    }
}



