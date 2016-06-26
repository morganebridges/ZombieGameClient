package com.fourninenine.zombiegameclient.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by morganebridges on 5/28/16.
 */
    public class User{
    private long id;
    private long clientKey;
    private String name;
    private double latitude;
    private double longitude;
    public User(String name, long id, double latitude, double longitude){
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public User(){}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setId(long uid){
    }

    public void setClientKey(long clientKey){
        this.clientKey = clientKey;
    }

    public long getClientKey() {
        return clientKey;
    }



    public void setLocation(LatLng location) {
        this.latitude = location.latitude;
        this.longitude = location.longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public LatLng getLocation(){
        return new LatLng(latitude, longitude);
    }

    public long getId() {
        return id;
    }
}
