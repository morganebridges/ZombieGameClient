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

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void apiTest() throws Exception {
        System.out.println("API TEST");
        Map hashMap = new HashMap<String, String>();
        hashMap.put("gamerTag", "testTag");
        User me = new User("me");
        HttpUserService requestService = new HttpUserService();

        Call<User> call = requestService.findUserByGamertag("testTag");
        System.out.println(call);
        //System.out.println(call.request());
    }



}