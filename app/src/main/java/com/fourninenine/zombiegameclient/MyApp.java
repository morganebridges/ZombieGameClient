package com.fourninenine.zombiegameclient;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.fourninenine.zombiegameclient.models.utilities.Globals;
import com.fourninenine.zombiegameclient.services.GCM;
import com.orm.SugarContext;

/**
 * Created by morganebridges on 6/4/16.
 */
public class MyApp extends Application{
    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
        SugarContext.init(this);
        context =  getApplicationContext();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        Globals globals = Globals.instance();
    }
    @Override
    public void onTerminate(){
        super.onTerminate();
        SugarContext.terminate();
    }

    public static Context getAppContext() throws IllegalStateException{
        if(MyApp.context != null)
            return MyApp.context;
        else throw new IllegalStateException("App context null!?!");
    }
    public static void setContext(Context c) throws IllegalStateException{
        MyApp.context = c;
    }
}
