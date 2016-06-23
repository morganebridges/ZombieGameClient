package com.fourninenine.zombiegameclient.models;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;

/**
 * Created by morganebridges on 6/4/16.
 */
public class Zombie extends SugarRecord {
    double latitude;
    double longitude;
    int hp;
    long zombieKey;

    public Zombie(double latitude, double longitude, int hp, long key) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.hp = hp;
        this.zombieKey = key;
    }


    public LatLng getLocation(){
        return new LatLng(latitude, longitude);
    }
    public long getKey(){
        return zombieKey;
    }
}
