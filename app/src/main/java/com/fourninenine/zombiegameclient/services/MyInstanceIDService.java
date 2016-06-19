package com.fourninenine.zombiegameclient.services;

import com.fourninenine.zombiegameclient.R;
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
        InstanceID iid = InstanceID.getInstance(this);
        for(TokenItem tokenItem : tokenList.getMap()){

            try {
                iid.getToken(tokenItem.authorizedEntity,tokenItem.scope,tokenItem.options);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // send this tokenItem.token to your server
        }

    }
    public String getToken(){
        InstanceID instanceID = InstanceID.getInstance(this);
        String authorizedEntity =  R.string.PROJECT_ID + "";
        String scope = "GCM";
        String token1 = null;
        try {
            token1 = instanceID.getToken(authorizedEntity, scope);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(token1 == null){
            throw new IllegalStateException("Not the result we wanted.");
        }
        return token1;
    }
}