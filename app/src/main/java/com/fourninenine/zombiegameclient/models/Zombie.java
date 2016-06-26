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

    public Zombie(long id, double latitude, double longitude, int hp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.hp = hp;
        this.id = id;
    }

    public LatLng getLocation(){
        return new LatLng(latitude, longitude);
    }

    public long getId(){
        return id;
    }
}
