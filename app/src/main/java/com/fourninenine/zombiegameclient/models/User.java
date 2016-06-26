package com.fourninenine.zombiegameclient.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.fourninenine.zombiegameclient.LoginActivity;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by morganebridges on 5/28/16.
 */
public class User{

    private long id;
    private String name;
    private double latitude;
    private double longitude;
    private int serum;
    private int ammo;

    public User(String name, long id, double latitude, double longitude, int serum, int ammo){
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.serum = serum;
        this.ammo = ammo;
    }

    public User(){}

    // Utility methods

    public static User getUser() {
        SharedPreferences preferences = LoginActivity.getAppContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        long id = preferences.getLong("id", -1);
        String name = preferences.getString("name", "noname");
        double latitude = Double.longBitsToDouble(preferences.getLong("latitude", -1));
        double longitude = Double.longBitsToDouble(preferences.getLong("longitude", -1));
        int serum = preferences.getInt("serum", -1);
        int ammo = preferences.getInt("ammo", -1);
        return  new User(name, id, latitude, longitude, serum, ammo);
    }

    public void save() {
        SharedPreferences preferences = LoginActivity.getAppContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("id", this.id);
        editor.putString("name", this.name);
        editor.putLong("latitude", Double.doubleToRawLongBits(this.latitude));
        editor.putLong("longitude", Double.doubleToRawLongBits(this.longitude));
        editor.putInt("serum", this.serum);
        editor.putInt("ammo", this.ammo);
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
}
