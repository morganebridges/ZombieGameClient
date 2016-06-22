package com.fourninenine.zombiegameclient.models;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;

/**
 * Created by morganebridges on 6/4/16.
 */
public class Zombie extends SugarRecord {
    LatLng location;
    int hp;

    public Zombie(LatLng location, int hp) {
        this.location = location;
        this.hp = hp;
    }


    public LatLng getLocation(){
        return location;
    }
}
