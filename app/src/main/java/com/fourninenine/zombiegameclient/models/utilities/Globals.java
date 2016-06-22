package com.fourninenine.zombiegameclient.models.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fourninenine.zombiegameclient.LoginActivity;
import com.fourninenine.zombiegameclient.MyApp;
import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.services.MyInstanceIDService;
import com.fourninenine.zombiegameclient.services.activityHelpers.GCMHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;

/**
 * Created by morganebridges on 6/19/16.
 */
public class Globals {
    private static User currentUser;
    private static TokenItem userToken;
    private static Globals instance;
    private static ArrayList<TokenItem> tokenList = new ArrayList<>();

    private GCMHelper gcmHelper = new GCMHelper(MyApp.getAppContext());

    public static ArrayList<TokenItem> getTokenList() {
        if(tokenList != null)
            return tokenList;
        return new ArrayList<TokenItem>();
    }
    public TokenItem popToken(){
        return tokenList.remove(tokenList.size()-1);
    }

    public void addToken(TokenItem token){
        if(tokenList != null)
            tokenList.add(token);
        tokenList = new ArrayList<TokenItem>();
        tokenList.add(token);
    }

    private Globals Globals(){
        return this;
    }

    public static void setCurrentUser(User currentUser) {
        currentUser = currentUser;
    }
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    public static Globals instance() {
        if(instance != null)
            return instance;
        else return new Globals();
    }

    public static void setUserToken(TokenItem userToken) {
        Globals.userToken = userToken;
    }
    public static User getCurrentUser() {
        return currentUser;
    }
    public static TokenItem getUserToken(){
        return userToken;
    }

    public static boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(LoginActivity.getAppContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
               System.out.println("error resolving user for google stuff");
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }
    public static User getUser(){
        Context appContext = LoginActivity.getAppContext();
        SharedPreferences prefs = appContext.getSharedPreferences("prefs", 0);
        User user = User.find(User.class, "CLIENT_KEY=?", prefs.getLong("clientKey", -1)+"").remove(0);
        return user;
    }
}
