package com.fourninenine.zombiegameclient.httpServices.RESTServices;

import android.content.SharedPreferences;

import com.fourninenine.zombiegameclient.httpServices.RESTInterfaces.RESTUserInterface;
import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.models.Zombie;
import com.fourninenine.zombiegameclient.models.utilities.Globals;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Query;

/**
 * Created by morganebridges on 5/30/16.
 */
public class HttpUserService implements RESTUserInterface {
    private static HttpUserService instance;

    public static final String BASE_URL = "http://52.39.83.97:8080";
    private Retrofit retrofit;
    private RESTUserInterface apiService;

    public HttpUserService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(RESTUserInterface.class);
    }
    /*
        Implement the singleton pattern.
     */
    public HttpUserService instance(){
        if(instance != null)
            return instance;
        instance = new HttpUserService();
        return instance;
    }
    /*
        This method current executes this request synchronously
     */
    @Override
    public Call<User> findUserByName(@Body String gamerTag) {

        User user = null;
        Call<User> call = apiService.findUserByName("testTag");

        //The enqueue method is commented out for purposes of unit testing, since it dispatches an execution call
        //asynchronously.




        System.out.println("Check for execution of call: " + call.isExecuted());

        System.out.println("CHeck for cancellation of call: " + call.isCanceled());

        return call;


    }

    @Override
    public Response<User> findUserByNameSynchronous(String name){
        Call<User> call = apiService.findUserByName(name);
        Response<User> response = null;
       try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public Call<User> registerWithGcm(@Query("gcmId") String token, @Query("key") long clientKey) {
        return apiService.registerWithGcm(token, clientKey);
    }

    /**
     * If a userID exists for this user, we use it, otherwise we pass a null.
     * @param userId
     * @return
     */
    @Override
    public Call<User> login(@Body long userId) {
        return apiService.login(userId);
    }

    @Override
    public Call<ArrayList<Zombie>> update(@Body User user) {
        Call<ArrayList<Zombie>> call = apiService.update(user);

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
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Zombie>> call, Throwable t) {
                System.out.println("ERROR");
                throw new IllegalStateException("An error was encountered with the API call");

            }
        });
        return call;    }


    @Override
    public Call<User> createUser(@Body User user) {
        return null;
    }

    public static void run() throws Exception {

    }
}
