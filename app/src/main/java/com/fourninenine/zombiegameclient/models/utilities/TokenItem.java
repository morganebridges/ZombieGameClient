package com.fourninenine.zombiegameclient.models.utilities;

import android.os.Bundle;

import com.fourninenine.zombiegameclient.R;

/**
 * Created by morganebridges on 6/19/16.
 */
public class TokenItem {
    public String regId;
    public String authorizedEntity;
    public String scope;
    public Bundle options;

    public TokenItem(){}

    public TokenItem(String regId){
        this.regId = regId;
        this.scope = "GCM";
        String authorizedEntity =  R.string.PROJECT_ID + "";
    }
    public void setOptions(Bundle options){
        this.options = options;
    }


    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }
}
