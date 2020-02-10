package com.example.chatroom;

import java.io.Serializable;

public class Usermessages implements Serializable {
    String date;
    String message;
    String msgid;
    String name;
    String time;
    String userid;
    String userimage;
    String groupname;
    String likes;

    public Usermessages() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public Usermessages(String date, String message, String msgid, String name, String time, String userid, String userimage, String groupname, String likes) {
        this.date = date;
        this.message = message;
        this.msgid = msgid;
        this.name = name;
        this.time = time;
        this.userid = userid;
        this.userimage = userimage;
        this.groupname = groupname;
        this.likes = likes;
    }
}
