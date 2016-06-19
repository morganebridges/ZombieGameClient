package com.fourninenine.zombiegameclient.services;

import android.content.Context;

import com.fourninenine.zombiegameclient.GCMTestActivity;
import com.fourninenine.zombiegameclient.MyApp;
import com.fourninenine.zombiegameclient.R;
import com.fourninenine.zombiegameclient.models.utilities.TokenItem;
import com.fourninenine.zombiegameclient.models.utilities.TokenList;
import com.fourninenine.zombiegameclient.services.activityHelpers.GCMHelper;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

import java.io.IOException;

public class MyInstanceIDService extends InstanceIDListenerService {
    public void onTokenRefresh() {
        refreshAllTokens();
    }

    private void refreshAllTokens() {
        // assuming you have defined TokenList as
        // some generalized store for your tokens
        TokenList tokenList = TokenList.Instance();
        Context appContext = MyApp.getAppContext();
        System.out.println("see application context");
        InstanceID iid = InstanceID.getInstance(appContext);
        for(TokenItem tokenItem : tokenList.getMap()){

            try {
                iid.getToken(tokenItem.authorizedEntity,tokenItem.scope,tokenItem.options);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // send this tokenItem.token to your server
        }

    }
    /*public String generateToken(){

        String authorizedEntity =  R.string.PROJECT_ID + "";
        String scope = "GCM";
        String token1 = null;
        GCMHelper helper = new GCMHelper(getApplicationContext());
        try {
            token1 = helper.
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(token1 == null){
            throw new IllegalStateException("Not the result we wanted.");
        }
        return token1;
    }*/
}