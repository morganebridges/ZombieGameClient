package com.fourninenine.zombiegameclient.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by morganebridges on 6/4/16.
 */

public class Zombie {
    double latitude;
    double longitude;
    int hp;
    long id;
    boolean alive;

    public Zombie(long id, double latitude, double longitude, int hp, boolean alive) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.hp = 5;
        this.id = id;
        this.alive = alive;
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
}
