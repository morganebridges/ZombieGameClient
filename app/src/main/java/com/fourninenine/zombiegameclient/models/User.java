package com.fourninenine.zombiegameclient.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.fourninenine.zombiegameclient.R;
import com.fourninenine.zombiegameclient.models.utilities.ApplicationContextProvider;
import com.google.android.gms.maps.model.LatLng;


import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A user model class with methods to retrieve and save itself to shared preferences
 */
public class User{


    Context context = ApplicationContextProvider.getAppContext();
    private long id = R.string.INVALID_VALUE;
    private String name;
    private double latitude;
    private double longitude;
    private int serum;
    private int ammo;
    private String gcmId;
    private int totalKills;
    private Deque<Location> previousLocations;

    public User(String name, long id, double latitude, double longitude, int serum, int ammo, String gcmId, int totalKills){
        //only set an id if it is valid
        if(id > 0)
            this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.serum = serum;
        this.ammo = ammo;
        this.gcmId = gcmId;
        this.totalKills = totalKills;
        this.previousLocations = new LinkedList<Location>();
    }

    public User(){}

    public User(String username) {
        this.name = username;
    }

    // Utility methods

    public static User getUser() {
        Context context = ApplicationContextProvider.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(
                context.getString(R.string.user_shared_preferences), Context.MODE_PRIVATE);

        long id = preferences.getLong(
                context.getString(R.string.user_id), -1);
        String name = preferences.getString(
                context.getString(R.string.user_name), "generic jerk");
        double latitude = Double.longBitsToDouble(preferences.getLong(
                context.getString(R.string.user_latitude), -1));
        double longitude = Double.longBitsToDouble(preferences.getLong(
                context.getString(R.string.user_longitude), -1));
        int serum = preferences.getInt(
                context.getString(R.string.user_serum), -1);
        int ammo = preferences.getInt(
                context.getString(R.string.user_ammo), -1);
        String gcmId = preferences.getString(
                context.getString(R.string.user_gcmid), "");
        int totalKills = preferences.getInt(
                context.getString(R.string.user_total_kills), 0);

        return  new User(name, id, latitude, longitude, serum, ammo, gcmId, totalKills);
    }

    public static void save(User user) throws IllegalStateException{
        if(user == null){
            return;
        }
        Context context = ApplicationContextProvider.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(
                context.getString(R.string.user_shared_preferences), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(context.getString(R.string.user_id), user.getId());
        editor.putString(context.getString(R.string.user_name), user.name);
        editor.putLong(context.getString(R.string.user_latitude),
                Double.doubleToRawLongBits(user.latitude));
        editor.putLong(context.getString(R.string.user_longitude),
                Double.doubleToRawLongBits(user.longitude));
        editor.putInt(context.getString(R.string.user_serum), user.serum);
        editor.putInt(context.getString(R.string.user_ammo), user.ammo);
        editor.putString(context.getString(R.string.user_gcmid), user.gcmId);
        editor.putInt(context.getString(R.string.user_total_kills), user.totalKills);

        editor.apply();
    }

    // getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long uid){
    }

    public void setLocation(LatLng location) {
        this.latitude = location.latitude;
        this.longitude = location.longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng getLocation(){
        return new LatLng(latitude, longitude);
    }

    public long getId() {
        return id;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public int getSerum() {
        return serum;
    }

    public void setSerum(int serum) {
        this.serum = serum;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public void setTotalKills(int totalKills) {
        this.totalKills = totalKills;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public static boolean isSavedUser(){
        Context context = ApplicationContextProvider.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.user_shared_preferences), Context.MODE_PRIVATE);
        //Globals.showDialog("New User","Choose a user name", LoginActivity.this);
        return preferences.getLong(context.getString(R.string.user_id), -1) > 0;
    }
    public Location popLastLocation(){
        if(previousLocations.size() > 0){
            return previousLocations.removeFirst();
        }
        return null;
    }

    /**
     * We only want to hold on to the five most recent locations that we've checked in from.
     * @param newLocation
     */
    public void addLocation(Location newLocation){
        if(previousLocations.size() > 5){
            previousLocations.removeFirst();
            previousLocations.addLast(newLocation);
        }
    }
    public boolean hasLocations(){
        return previousLocations.size() > 0;
    }
}
