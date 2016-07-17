package com.fourninenine.zombiegameclient.services.activityHelpers;

import android.content.Context;

import com.fourninenine.zombiegameclient.models.utilities.Globals;
import com.fourninenine.zombiegameclient.models.utilities.TokenItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This is a helper class we needed to create because there is a bug int he GCM code that makes it very difficult to use
 * the InstanceID class without passing null application context objects to the methodf. This is a workaround that
 * should probably be refactored later.
 */
public final class GCMHelper {

    static GoogleCloudMessaging gcm = null;

    static Context context= null;

    public GCMHelper (Context context)
    {
        GCMHelper.context = context;
    }

    public String GCMRegister (String SENDER_ID) throws Exception
    {
        String regid = "";
        //Check if Play store services are available.
        if(!checkPlayServices())
            throw new Exception("Google Play Services not supported. Please install and configure Google Play Store.");

        if (gcm == null) {
            gcm = GoogleCloudMessaging.getInstance(context);
        }
        regid = gcm.register(SENDER_ID);

        //whenever we receive a token, we add them to our collection
        TokenItem token = new TokenItem(regid);
        Globals.getTokenList().add(token);
        return regid;
    }


    private static boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }
}