package com.fourninenine.zombiegameclient.services;

import android.content.Context;

/**
 * Created by morganebridges on 6/19/16.
 */
public class GCM {
    private static GCM instance;
    public static GCM instance() throws Exception {
        if(instance != null)
            return instance;
        else throw new Exception("This shouldn't happen");
    }
    private final Context appContext;

    public GCM(Context appContext){
        this.appContext = appContext;
    }

    public Context getAppContext(){
        return appContext;
    }
}
