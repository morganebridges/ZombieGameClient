package com.fourninenine.zombiegameclient.services;
import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.models.Zombie;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Iterator;



/**
 * Created by morganebridges on 6/22/16.
 */
public class MapDrawingService {
    User user;
    GoogleMap map;

    public MapDrawingService(User user, GoogleMap map){
        this.user = user;
        this.map = map;
    }
    public GoogleMap placeZombies(Iterator<Zombie> zombIt){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        MarkerOptions userMarker = new MarkerOptions().position(user.getLocation()).title(user.getName());
        map.addMarker(userMarker);
        builder.include(user.getLocation());
        while(zombIt.hasNext()){
            Zombie zom = zombIt.next();
            MarkerOptions marker = new MarkerOptions().position(zom.getLocation()).title("Zombie");
            map.addMarker(marker);
            builder.include(zom.getLocation());
        }
        MarkerOptions marker = new MarkerOptions().position(user.getLocation()).title("User location");
        map.addMarker(marker);
        builder.include(user.getLocation());
        LatLngBounds bounds = builder.build();
        int padding = 6; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        map.moveCamera(cu);
        return map;
    }


}
