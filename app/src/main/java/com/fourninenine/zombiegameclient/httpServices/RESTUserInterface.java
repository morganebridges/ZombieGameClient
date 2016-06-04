package com.fourninenine.zombiegameclient.httpServices;

import com.fourninenine.zombiegameclient.models.User;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * This interface will be the location where we actually define our REST services.
 * Created by morganebridges on 6/2/16.
 */
public interface RESTUserInterface {
    @POST("getuser")
    Call<User> findUserByGamertag(@Body String gamerTag);

    @POST("user/new")
    Call<User> createUser(@Body User user);



}
