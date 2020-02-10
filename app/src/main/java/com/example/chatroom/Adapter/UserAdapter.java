package com.example.chatroom.Adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatroom.MessageActivity;
import com.example.chatroom.R;
import com.example.chatroom.Update_Info;
import com.example.chatroom.Users;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<Users> musers;
    private boolean ischat;

    public UserAdapter(Context context,List<Users> musers,boolean ischat){
        this.context = context;
        this.musers = musers;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_item,viewGroup,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        final Users users = musers.get(i);
        viewHolder.username.setText(users.getUsername());
        //viewHolder.profileimage.setImageResource(R.mipmap.ic_launcher);
        Picasso.get().load(users.getImageurl()).placeholder(R.mipmap.loading).into(viewHolder.profileimage);
        //if(users.getImageUrl().equals("default")){
           // viewHolder.profileimage.setImageResource(R.mipmap.ic_launcher);
        //}else{
            //Glide.with(context).load(users.getImageUrl()).into(viewHolder.profileimage);
        //}

        if(ischat){
            Log.d("users",users.getStatus());
            if(users.getStatus().equals("online")){
                viewHolder.image_on.setVisibility(View.VISIBLE);
                viewHolder.image_off.setVisibility(View.GONE);
                System.out.println(users.getUsername());
            }
            else if(users.getStatus().equals("offline")){
                viewHolder.image_off.setVisibility(View.VISIBLE);
                viewHolder.image_on.setVisibility(View.GONE);
            }
        }else{
            viewHolder.image_off.setVisibility(View.GONE);
            viewHolder.image_on.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Update_Info.class);
                intent.putExtra("userid", users.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profileimage;
        public ImageView image_on;
        public ImageView image_off;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username_users_item);
            profileimage = itemView.findViewById(R.id.profile_image);
            image_on = itemView.findViewById(R.id.status_on);
            image_off = itemView.findViewById(R.id.status_off);

        }
    }

}
