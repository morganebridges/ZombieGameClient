package com.fourninenine.zombiegameclient;

import android.app.Application;

import org.junit.Test;

import static org.junit.Assert.*;

import com.fourninenine.zombiegameclient.httpServices.HttpUserService;
import com.fourninenine.zombiegameclient.models.User;
import com.orm.SugarContext;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void findUserByNameAsyncTest() throws Exception {
        System.out.println("API TEST");
        User me = new User("me", 123);
        HttpUserService requestService = HttpUserService.instance();

        Call<User> call = requestService.findUserByName("testTag");

        //System.out.println(call.request());
    }
    @Test
    public void findUserByNameSyncTest() throws Exception {
        HttpUserService requestService = HttpUserService.instance();
        Response<User> response =  requestService.findUserByNameSynchronous("testTag");

        System.out.println(response.body());

    }



}