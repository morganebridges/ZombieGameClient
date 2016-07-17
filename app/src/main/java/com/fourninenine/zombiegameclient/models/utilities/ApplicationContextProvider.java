package com.fourninenine.zombiegameclient.models.utilities;

import android.content.Context;

import com.fourninenine.zombiegameclient.LoginActivityFinal;

public class ApplicationContextProvider {
    /**
     * Keeps a reference of the application context
     */
    private static Context sContext;
    private static boolean isSet = false;

    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getAppContext() {
        return sContext;
    }
    public static void setContext(Context context){
        if(isSet)
            return;
        sContext = context;
        isSet = true;

    }

}