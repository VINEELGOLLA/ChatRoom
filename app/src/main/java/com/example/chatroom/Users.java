package com.example.chatroom;

public class Users {
    private String id;
    private String username;
    private String imageurl;
    private String gender;
    private String city;

    public Users(String id, String username, String imageurl, String gender, String city, String status, String tripid) {
        this.id = id;
        this.username = username;
        this.imageurl = imageurl;
        this.gender = gender;
        this.city = city;
        this.status = status;
        this.tripid = tripid;
    }

    private String status;
    private String tripid;

    public String getTripid() {
        return tripid;
    }

    public void setTripid(String tripid) {
        this.tripid = tripid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Users(String id, String username, String imageurl, String gender, String city, String status) {
        this.id = id;
        this.username = username;
        this.imageurl = imageurl;
        this.gender = gender;
        this.city = city;
        this.status = status;
    }

    public Users() {
    }
}

