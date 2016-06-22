package com.fourninenine.zombiegameclient.httpServices.RESTServices;

import com.fourninenine.zombiegameclient.httpServices.RESTInterfaces.RESTMapInterface;
import com.fourninenine.zombiegameclient.httpServices.RESTInterfaces.RESTUserInterface;
import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.models.Zombie;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;

public class HttpMapService implements RESTMapInterface {
    private static HttpMapService instance;

    public static final String BASE_URL = "http://52.39.83.97:8080";
    private Retrofit retrofit;
    private RESTMapInterface apiService;
    private GoogleMap map;

    public HttpMapService(GoogleMap map) {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(RESTMapInterface.class);
        this.map = map;
    }
    /*
        Implement the singleton pattern.
     */

    /*@Override
    public Call<ArrayList<Zombie>> updateMap(@Body User user) {
        Call<ArrayList<Zombie>> call = apiService.updateMap(user);

        //The enqueue method is commented out for purposes of unit testing, since it dispatches an execution call
        //asynchronously.

        call.enqueue(new Callback<ArrayList<Zombie>>() {
            @Override
            public void onResponse(Call<ArrayList<Zombie>> call, Response<ArrayList<Zombie>> response) {
                System.out.println("On success callback");
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                ArrayList<Zombie> zombies = response.body();
                Iterator<Zombie> zombIt= zombies.iterator();
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

            }

            @Override
            public void onFailure(Call<ArrayList<Zombie>> call, Throwable t) {
                System.out.println("ERROR");
                throw new IllegalStateException("An error was encountered with the API call");

            }
        });
        return call;
    }*/

}