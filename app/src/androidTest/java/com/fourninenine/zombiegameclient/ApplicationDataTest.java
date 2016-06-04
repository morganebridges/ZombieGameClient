package com.fourninenine.zombiegameclient;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.fourninenine.zombiegameclient.models.User;

import junit.framework.TestResult;

/**
 * The tests located here are ones that require an appliation context to properly test.
 *
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 *
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

    /**
     * A simple series of operations using sugar ORM calls.
     */
    public void testPersistence(){
        User user = new User("myUserName");

        System.out.println("User created");
        System.out.println("User's name: " + user.getName());
        //Save to the database, generating a user id.
        user.save();
        long id = user.getId();
        user = User.findById(User.class, id);
        //If the retrieved id from the database equal to the long value stored from the original object, test passes.
        assertEquals(user.getId().longValue(), id);

        //Update the row of the user object
        user.setName("newName");
        user.save();

        //Retrieve from the database again
        user = User.findById(User.class, id);

        assertEquals(user.getName(), "newName");

        User.delete(user);

        assertNull(User.findById(User.class, id));

    }


}