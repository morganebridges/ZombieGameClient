package com.fourninenine.zombiegameclient.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by morganebridges on 6/4/16.
 */

public class Zombie {
    String label;
    double latitude;
    double longitude;
    int hp;
    long id;
    boolean alive;
    Location location;

    boolean underAttack;

    public Zombie(long id, double latitude, double longitude, int hp, boolean alive) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.hp = 5;
        this.id = id;
        this.alive = alive;
        this.label = "Zombie " + this.id + "[" + hashCode() + "]";
    }

    public LatLng getLocation(){
        return new LatLng(latitude, longitude);
    }

    public long getId(){
        return id;
    }

    public boolean isAlive(){
        return alive;
    }
    public void setLocation(double lat, double lng){
        Location location = new Location(this.label);
        location.setLatitude(lat);
        location.setLongitude(lng);
        setLocation(location);
    }

    public void setLocation(Location location) {
        this.location = location;
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
    }
    public boolean isUnderAttack() {
        return underAttack;
    }

    public void setUnderAttack(boolean underAttack) {
        this.underAttack = underAttack;
    }
}
