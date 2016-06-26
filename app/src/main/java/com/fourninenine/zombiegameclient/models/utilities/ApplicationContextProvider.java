package com.fourninenine.zombiegameclient.models.utilities;

import android.app.Application;
import android.content.Context;

import com.fourninenine.zombiegameclient.LoginActivity;

public class ApplicationContextProvider {

    /**
     * Keeps a reference of the application context
     */
    private static Context sContext;

    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getAppContext() {
        return LoginActivity.getAppContext();
    }

}