package com.fourninenine.zombiegameclient.models.utilities;

import android.provider.Settings;

import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.services.MyInstanceIDService;

/**
 * Created by morganebridges on 6/19/16.
 */
public class Globals {
    private static User currentUser;
    private static Globals instance;
    private static String userToken;

    private Globals Globals(){
        return this;
    }

    public static void setCurrentUser(User currentUser) {
        currentUser = currentUser;
    }

    public static Globals instance() {
        if(instance != null)
            return instance;
        else return new Globals();
    }

    public static String getUserToken() {
        MyInstanceIDService idService = new MyInstanceIDService();

        if(userToken != null)
            return userToken;
        else return idService.getToken();
    }

    public static void setUserToken(String userToken) {
        Globals.userToken = userToken;
    }
    public static User getCurrentUser() {
        return currentUser;
    }


}
