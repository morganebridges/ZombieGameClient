package com.fourninenine.zombiegameclient;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.fourninenine.zombiegameclient.models.User;

import junit.framework.TestResult;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationDataTest extends ApplicationTestCase<MyApp> {
    private Application myApp;
    public ApplicationDataTest() {
        super(MyApp.class);
    }
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
        myApp = getApplication();
        myApp.onCreate();
    }
    public void testPersistence(){
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
        //If the retrieved id from the 
        assertEquals(user.getId().longValue(), id);
    }


}