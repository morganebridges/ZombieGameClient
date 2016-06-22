package com.fourninenine.zombiegameclient.services;

import com.fourninenine.zombiegameclient.LoginActivity;
import com.fourninenine.zombiegameclient.models.utilities.Globals;
import com.fourninenine.zombiegameclient.models.utilities.TokenItem;
import com.fourninenine.zombiegameclient.models.utilities.TokenList;
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
        System.out.println("see application context");
        InstanceID iid = InstanceID.getInstance(LoginActivity.getAppContext());
        for(TokenItem tokenItem : tokenList.getMap()){

            try {
                iid.getToken(tokenItem.authorizedEntity,tokenItem.scope,tokenItem.options);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Exception thrown when iterating through tokens");
            }
            // send this tokenItem.token to your server
        }

    }
    public String retrieveTokenItem() throws IOException {
        String token = "ass";
        if(Globals.getUserToken() == null){
            InstanceID iid = InstanceID.getInstance(LoginActivity.getAppContext());

            try {
                token = iid.getToken("1066512751755", "GCM");
                Globals.setUserToken(new TokenItem(token));
                return token;
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Something went wrong with the token");
            }
        }
        return Globals.getUserToken().getRegId();
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