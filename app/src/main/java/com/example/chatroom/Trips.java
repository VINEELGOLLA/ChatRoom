package com.example.chatroom;

import java.io.Serializable;

public class Trips implements Serializable{
    public String source_name;
    public double sourcelat;
    public double sourcelng;
    public double destlat;
    public double destlng;
    public String dest_name;
    public String trip_id;
    public String status;
    public String driverid;
    public String riderid;
    public String driverlat;
    public String driverlong;
    public Trips(){
    }

    public String getDriverlat() {
        return driverlat;
    }

    public void setDriverlat(String driverlat) {
        this.driverlat = driverlat;
    }

    public String getDriverlong() {
        return driverlong;
    }

    public void setDriverlong(String driverlong) {
        this.driverlong = driverlong;
    }

    public Trips(String source_name, double sourcelat, double sourcelng, double destlat, double destlng, String dest_name, String trip_id, String status, String driverid, String riderid, String driverlat, String driverlong) {
        this.source_name = source_name;
        this.sourcelat = sourcelat;
        this.sourcelng = sourcelng;
        this.destlat = destlat;
        this.destlng = destlng;
        this.dest_name = dest_name;
        this.trip_id = trip_id;
        this.status = status;
        this.driverid = driverid;
        this.riderid = riderid;
        this.driverlat = driverlat;
        this.driverlong = driverlong;
    }

    public Trips(String source_name, double sourcelat, double sourcelng, double destlat, double destlng, String dest_name, String trip_id, String status, String driverid, String riderid) {
        this.source_name = source_name;
        this.sourcelat = sourcelat;
        this.sourcelng = sourcelng;
        this.destlat = destlat;
        this.destlng = destlng;
        this.dest_name = dest_name;
        this.trip_id = trip_id;
        this.status = status;
        this.driverid = driverid;
        this.riderid = riderid;

    }

    public String getSource_name() {
        return source_name;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public double getSourcelat() {
        return sourcelat;
    }

    public void setSourcelat(double sourcelat) {
        this.sourcelat = sourcelat;
    }

    public double getSourcelng() {
        return sourcelng;
    }

    public void setSourcelng(double sourcelng) {
        this.sourcelng = sourcelng;
    }

    public double getDestlat() {
        return destlat;
    }

    public void setDestlat(double destlat) {
        this.destlat = destlat;
    }

    public double getDestlng() {
        return destlng;
    }

    public void setDestlng(double destlng) {
        this.destlng = destlng;
    }

    public String getDest_name() {
        return dest_name;
    }

    public void setDest_name(String dest_name) {
        this.dest_name = dest_name;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }

    public String getRiderid() {
        return riderid;
    }

    public void setRiderid(String riderid) {
        this.riderid = riderid;
    }

    @Override
    public String toString() {
        return "Trips{" +
                "source_name='" + source_name + '\'' +
                ", sourcelat=" + sourcelat +
                ", sourcelng=" + sourcelng +
                ", destlat=" + destlat +
                ", destlng=" + destlng +
                ", dest_name='" + dest_name + '\'' +
                ", id='" + trip_id + '\'' +
                ", status='" + status + '\'' +
                ", driverid='" + driverid + '\'' +
                ", riderid='" + riderid + '\'' +
                '}';
    }
}