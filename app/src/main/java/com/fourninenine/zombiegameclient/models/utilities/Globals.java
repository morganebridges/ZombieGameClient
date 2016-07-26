package com.fourninenine.zombiegameclient.models.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.fourninenine.zombiegameclient.MyApp;
import com.fourninenine.zombiegameclient.R;
import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.services.activityHelpers.GCMHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;

import java.util.Random;

/**
 * CAN you really call that an error
 * Created by morganebridges on 6/19/16.
 */
public class Globals {
    private static AlertDialog showingAlert = null;
    private static User currentUser;
    private static TokenItem userToken;
    private static Globals instance;
    private static ArrayList<TokenItem> tokenList = new ArrayList<>();
    //This is a reference flag I'll use to see
    private static boolean inApp = false;

    //Turn on this flag if you want to have optional exceptions thrown at particular places in your workflow.
    public static boolean bugSmashing = true;

    private GCMHelper gcmHelper = new GCMHelper(MyApp.getAppContext());

    public static ArrayList<TokenItem> getTokenList() {
        if(tokenList != null)
            return tokenList;
        return new ArrayList<TokenItem>();
    }
    public TokenItem popToken(){
        return tokenList.remove(tokenList.size()-1);
    }

    public void addToken(TokenItem token){
        if(tokenList != null)
            tokenList.add(token);
        tokenList = new ArrayList<TokenItem>();
        tokenList.add(token);
    }
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    public static Globals instance() {
        if(instance != null)
            return instance;
        else return new Globals();
    }

    public static void setUserToken(TokenItem userToken) {
        Globals.userToken = userToken;
    }
    public static TokenItem getUserToken(){
        return userToken;
    }

    public static boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(ApplicationContextProvider.getAppContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
               System.out.println("error resolving user for google stuff");
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }
    public static User getUser() {
        return User.getUser();
    }
    public static void showDialog(String title, String message, Activity activity) {
        if(showingAlert != null)
            showingAlert.dismiss();
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.drawable.zombiehand48);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        showingAlert = alertDialog;
        alertDialog.show();
    }
    public static SharedPreferences getPreferences(){
        Context context = ApplicationContextProvider.getAppContext();
        return context.getSharedPreferences(context.getString(R.string.user_shared_preferences), Context.MODE_PRIVATE);
    }
    public static void showConnectionDialog(Activity activity){
        Random rnd = new Random();
        String[] offlineQuips = {
                "They must have chewed through the wiring, I'm not getting ANYTHING",
                "This never happened when I had boost mobile.",
                "This reception is worse than an biter wedding in Alabama",
                "Keep sharp, we're flying blind out here.",
                "Looks like its just you and me, kid",
                "God damn phone company, well at least they spent their last days cutting corners and not rotting flesh",
                "I'm sorry, but central synchronization is offline while main power is being redirected to directed energy weapons in the" +
                        " an effort to repel a horde. Please try again shortly."
        };
        String message = offlineQuips[rnd.nextInt(offlineQuips.length)];
        showDialog("Connection Issues", message, activity);


    }
    public static double metersToDegrees(double meters){
        return meters / 71695.8;
    }
    public static void printToGuru(String title, String message, Activity caller){

    }
}

