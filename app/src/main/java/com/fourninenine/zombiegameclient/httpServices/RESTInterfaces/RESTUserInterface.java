package com.fourninenine.zombiegameclient.httpServices.RESTInterfaces;

import com.fourninenine.zombiegameclient.models.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * This interface will be the location where we actually define our REST services.
 * Created by morganebridges on 6/2/16.
 */
public interface RESTUserInterface {
    @POST("getuser")
    Call<User> findUserByName(@Query("name") String name);

    @POST("user/new")
    Call<User> createUser(@Body User user);

    @POST("getuser")
    Response<User> findUserByNameSynchronous(@Query("name") String name);



}