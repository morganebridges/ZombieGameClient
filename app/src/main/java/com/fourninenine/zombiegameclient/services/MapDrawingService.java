package com.fourninenine.zombiegameclient.services;

import android.os.AsyncTask;

import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.models.Zombie;
import com.fourninenine.zombiegameclient.models.utilities.Globals;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by morganebridges on 6/22/16.
 */
public class MapDrawingService {
    User user;
    GoogleMap map;
    Call<ArrayList<Zombie>> call;

    public MapDrawingService(User user, Call<ArrayList<Zombie>> call, GoogleMap map){
        this.call = call;
        this.user = user;
        this.map = map;
    }
    public GoogleMap draw(){
        /*call.enqueue(new Callback<ArrayList<Zombie>>() {
            @Override
            public void onResponse(Call<ArrayList<Zombie>> call, Response<ArrayList<Zombie>> response) {
                System.out.println("On success callback");

                ArrayList<Zombie> zombies = response.body();
                Iterator<Zombie> zombIt= zombies.iterator();
                placePins(zombIt);

            }

            @Override
            public void onFailure(Call<ArrayList<Zombie>> call, Throwable t) {
                System.out.println("ERROR");
                throw new IllegalStateException("An error was encountered with the API call");

            }
        });*/

        Iterator<Zombie> zombIt = Zombie.findAll(Zombie.class);
        placePins(zombIt);
        return map;
    }
    private GoogleMap placePins(Iterator<Zombie> zombIt){
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
        LatLngBounds bounds = builder.build();
        int padding = 6; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        map.moveCamera(cu);
        return map;
    }


}
