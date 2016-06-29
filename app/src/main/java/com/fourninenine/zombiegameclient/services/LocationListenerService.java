package com.fourninenine.zombiegameclient.services;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.fourninenine.zombiegameclient.MainMapActivity;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by morganebridges on 6/28/16.
 */
public class LocationListenerService implements LocationListener {
    GoogleMap map;
    MainMapActivity mapActivity;
    public LocationListenerService(GoogleMap map, MainMapActivity activity){
        this.mapActivity = activity;
        this.map = map;
    }
    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
