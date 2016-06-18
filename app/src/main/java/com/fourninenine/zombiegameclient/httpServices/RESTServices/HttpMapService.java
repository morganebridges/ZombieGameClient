package com.fourninenine.zombiegameclient.httpServices.RESTServices;

import com.fourninenine.zombiegameclient.httpServices.RESTInterfaces.RESTMapInterface;
import com.fourninenine.zombiegameclient.httpServices.RESTInterfaces.RESTUserInterface;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

    @Override
    public Call<LatLng[]> updateMap(@Body LatLng location) {
        Call<LatLng[]> call = apiService.updateMap(location);

        //The enqueue method is commented out for purposes of unit testing, since it dispatches an execution call
        //asynchronously.

        call.enqueue(new Callback<LatLng[]>() {
            @Override
            public void onResponse(Call<LatLng[]> call, Response<LatLng[]> response) {
                System.out.println("On success callback");
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                LatLng[] latLngs = response.body();
                for(int i = 0; i < latLngs.length; i++){
                    MarkerOptions marker = new MarkerOptions().position(latLngs[i]).title("Zombie" + i);
                    map.addMarker(marker);
                    builder.include(latLngs[i]);
                }
                LatLngBounds bounds = builder.build();
                int padding = 0; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                map.moveCamera(cu);

            }

            @Override
            public void onFailure(Call<LatLng[]> call, Throwable t) {
                System.out.println("ERROR");
                throw new IllegalStateException("An error was encountered with the API call");
            }
        });
        return call;
    }

}