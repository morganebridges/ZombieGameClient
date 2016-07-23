package com.fourninenine.zombiegameclient.models;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.fourninenine.zombiegameclient.LoginActivityFinal;
import com.fourninenine.zombiegameclient.R;
import com.fourninenine.zombiegameclient.models.utilities.ApplicationContextProvider;
import com.fourninenine.zombiegameclient.models.utilities.Globals;
import com.google.android.gms.maps.model.LatLng;


import java.util.Deque;
import java.util.LinkedList;

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
    public static long lastModified = System.currentTimeMillis();


    private float perceptionRange;
    private float attackRange;

    private static final Object lockObject = new Object();


    public void setHp(int hp) {
        this.hp = hp;
    }

    private int hp;

    public float getPerceptionRange() {
        return perceptionRange;
    }

    public void setPerceptionRange(float perceptionRange) {
        this.perceptionRange = perceptionRange;
    }


    /* Just to be clear, to myself more than anyone else, we should never call any constructors for the user class. That is the server's job.
        These only exist so that we may instantiate the active user (very carefully and only when the data is verified).

     */
    public User(String name, long id, double latitude, double longitude, int serum, int ammo, String gcmId, int totalKills, float attackRange, float perceptionRange, int hp ){
        //only set an id if it is valid
        if(id > 0)
            this.id = id;
        this.name = name;
        this.attackRange = attackRange;
        this.latitude = latitude;
        this.longitude = longitude;
        this.serum = serum;
        this.ammo = ammo;
        this.gcmId = gcmId;
        this.totalKills = totalKills;
        this.previousLocations = new LinkedList<>();
        //just hard code a default
        this.perceptionRange = perceptionRange;
        this.hp = hp;
    }

    public User(){
        this.hp = 20;
        this.previousLocations = new LinkedList<>();
    }

    public User(String username) {
        this.name = username;
    }

    // Utility methods

    public static User getUser() {
        Context context = ApplicationContextProvider.getAppContext();
        SharedPreferences preferences = Globals.getPreferences();

        long id = preferences.getLong(
                context.getString(R.string.user_id), -1);
        if(id == -1){
            Intent loginIntent = new Intent(ApplicationContextProvider.getAppContext(), LoginActivityFinal.class);
            loginIntent.putExtra("error", true);

        }

        String name = preferences.getString(
                context.getString(R.string.user_name), "Invalid data");

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
        int hp = preferences.getInt(context.getString(R.string.hp), 20);
        float attackRange = preferences.getFloat(context.getString(R.string.user_attack_range),10);
        float perceptionRange = (preferences.getFloat(
                context.getString(R.string.user_perception_range), -1));

        User tempUser =  new User(name, id, latitude, longitude, serum, ammo, gcmId, totalKills, attackRange, perceptionRange, hp);
        if(!tempUser.isIdValid()){
            System.out.println(" **** Invalid user state from prefs (User.getUser())****");
            System.out.println(tempUser.toString());
            System.out.println("*** *** State of preferences *** ***");
            if(Globals.bugSmashing)
                throw new IllegalStateException("Data corruption in shared preferences");
        }
        return tempUser;

    }

    public static boolean save(User user) throws IllegalStateException{
        if(user == null ){
            Log.d("User.save - null arg", "A null user was passd to method that saves to prefs");
            if(Globals.bugSmashing)
                throw new IllegalStateException("(User.save) Null user passed into save method");
            return false;

        }
        else if(!isUserValid(user)){
            Log.d("User.save - null arg", "A null user was passd to method that saves to prefs");
            Log.d("User invalid", user.toString());
            if(Globals.bugSmashing)
                throw new IllegalStateException("(User.save) Attempting to corrupt master user record with bad data.");
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

        editor.putFloat(context.getString(R.string.user_perception_range),
                user.getAttackRange());
        editor.putFloat(context.getString(R.string.user_attack_range), user.attackRange);

        editor.apply();
        return true;
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
        setLatitude(location.latitude);
        setLongitude(location.longitude);
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
        Globals.getPreferences().edit().putString("gcmId", gcmId).apply();
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
        long idCheck = Long.MAX_VALUE;
        //See if w learn anything from just the ids
        if(!((idCheck = preferences.getLong(context.getString(R.string.user_id), -1)) > 0 && (idCheck < R.string.USERS_UPPER_BOUND)))
            return false;
        User testUser = getUser();
        return(isUserValid(getUser()));


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
        if(previousLocations == null)
            previousLocations = new LinkedList<Location>();
        if(previousLocations.size() > 5){
            previousLocations.removeFirst();
            previousLocations.addLast(newLocation);
        }
    }
    public boolean hasLocations(){
        return previousLocations.size() > 0;
    }

    public float getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(float attackRange) {
        this.attackRange = attackRange;
    }

    public int getHp() {
        return hp;
    }

    public static boolean isUserValid(User user){
        boolean valid = true;
        if(!user.isIdValid())
            return false;
        if(user.getName() == null)
            return false;
        return true;
    }
    public boolean isIdValid(){
        if(id < 1 || id > R.string.USERS_UPPER_BOUND)
            return false;
        return true;
    }
}
