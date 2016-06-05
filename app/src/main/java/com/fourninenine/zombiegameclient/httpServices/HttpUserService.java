package com.fourninenine.zombiegameclient.httpServices;

import com.fourninenine.zombiegameclient.models.User;

import java.io.IOException;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by morganebridges on 5/30/16.
 */
public class HttpUserService implements RESTUserInterface {
    public static final String BASE_URL = "http://52.39.83.97:8080";

    OkHttpClient client = new OkHttpClient();
    User user;
    Map<String, String> keyMap;
    Retrofit retrofit;
    RESTUserInterface apiService = retrofit.create(RESTUserInterface.class);
    public HttpUserService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


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


         call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                System.out.println("SUccess!!");
                int statusCode = response.code();
                //user = response.body();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("ERROR");
                throw new IllegalStateException("An error was encountered with the API call");
            }
        });

        System.out.println("Check for execution of call: " + call.isExecuted());

        System.out.println("CHeck for cancellation of call: " + call.isCanceled());

        return call;


    }
    /*
    @Override
    public Response<User> findUserByNameSynchronous(String name){
        Response<User> response = apiService.findUserByNameSynchronous(name);
        return response;
    }*/

    @Override
    public Call<User> createUser(@Body User user) {
        return null;
    }

    public static void run() throws Exception {

    }
}
