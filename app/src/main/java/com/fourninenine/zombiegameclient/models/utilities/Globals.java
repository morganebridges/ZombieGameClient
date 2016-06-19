package com.fourninenine.zombiegameclient.models.utilities;

import com.fourninenine.zombiegameclient.MyApp;
import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.services.MyInstanceIDService;
import com.fourninenine.zombiegameclient.services.activityHelpers.GCMHelper;

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

}
