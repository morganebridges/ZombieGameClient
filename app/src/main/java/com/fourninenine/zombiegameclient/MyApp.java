package com.fourninenine.zombiegameclient;

import android.app.Application;
import android.content.Intent;

import com.fourninenine.zombiegameclient.models.utilities.Globals;
import com.orm.SugarContext;

/**
 * Created by morganebridges on 6/4/16.
 */
public class MyApp extends Application{
    @Override
    public void onCreate(){
        super.onCreate();
        SugarContext.init(this);

        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        Globals globals = Globals.instance();
    }
    @Override
    public void onTerminate(){
        super.onTerminate();
        SugarContext.terminate();
    }
}
