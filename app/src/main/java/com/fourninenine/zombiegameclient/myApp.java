package com.fourninenine.zombiegameclient;

import android.app.Application;

import com.orm.SugarContext;

/**
 * Created by morganebridges on 6/4/16.
 */
public class MyApp extends Application{
    @Override
    public void onCreate(){
        super.onCreate();
        SugarContext.init(this);
    }
    @Override
    public void onTerminate(){
        super.onTerminate();
        SugarContext.terminate();
    }
}
