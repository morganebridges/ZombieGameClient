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



    @Test
    public void testSugarORM() throws Exception {
        User user = new User("myUserName");

        System.out.println("User created");
        System.out.println("User's name: " + user.getName());
        user.save();
        long id = user.getId();
        System.out.println("User's ID");

        //Null out user
        user = null;
        user = User.findById(User.class, id);
        if(user != null){
            System.out.println("Query for user produced user with name: " + user.getName());

        }
        assertEquals(user.getId().intValue(), id);


    }
}