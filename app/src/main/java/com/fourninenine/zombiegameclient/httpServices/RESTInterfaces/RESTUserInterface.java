package com.fourninenine.zombiegameclient.httpServices.RESTInterfaces;

import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.models.Zombie;
import com.fourninenine.zombiegameclient.models.dto.UserActionDto;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

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
    Call<User> createUser(@Body String userName);

    @POST("getuser")
    Response<User> findUserByNameSynchronous(@Query("name") String name);

    @POST("gcm/register")
    Call<User> registerWithGcm(@Query("gcmId") String token, @Query("key") long clientKey);

    @POST("user/login")
    Call<User> login(@Body long userId);

    @POST("user/update")
    Call<ArrayList<Zombie>> update(@Body UserActionDto userActionDto);

    @POST("user/attack")
    Call<Zombie> attack(@Body UserActionDto userActionDto);

}
