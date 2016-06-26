package com.fourninenine.zombiegameclient;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.fourninenine.zombiegameclient.models.utilities.Globals;

/**
 * Created by morganebridges on 6/4/16.
 */
public class MyApp extends MultiDexApplication {

    // Shut up, lint.  It's not a leak when it's the Application context.
    @SuppressLint("StaticFieldLeak")
    private static Application context;

    @Override
    public void onCreate(){
        super.onCreate();
        context = this;

        Intent loginIntent = new Intent(this, LoginActivity.class);
        //startActivity(loginIntent);
        Globals globals = Globals.instance();
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
    }

    public static Context getAppContext() throws IllegalStateException{
        if(MyApp.context != null)
            return MyApp.context;
        else throw new IllegalStateException("App context null!?!");
    }

}
