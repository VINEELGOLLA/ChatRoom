package com.example.chatroom;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Chat_room implements Serializable {
    public String name;
    public ArrayList<Message> messages=new ArrayList<Message>();
    public String cid;
    //public ArrayList<Activegroupusers> ids = new ArrayList<Activegroupusers>();


    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
