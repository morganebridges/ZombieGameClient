package com.fourninenine.zombiegameclient.models.utilities;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by morganebridges on 6/21/16.
 * This utility class is here to provide static methods to the services layer in order to make useful calculations.
 *
 */
public class Geomath {

    /**::  This routine calculates the distance between two points (given the
     /*::  latitude/longitude of those points). It is being used to calculate
     /*::  the distance between two locations using GeoDataSource (TM) prodducts
     /*::   Pretty sure we are using meters here. for the "default" unit
     /*::  Definitions:
     /*    South latitudes are negative, east longitudes are positive
     /*    This method returns the distance in MILES by default.
     /*/
    public static double getDistance(double lat1, double lon1, double lat2, double lon2, String unit){
        double theta = Math.abs(lon1 - lon2);
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }
        return dist;
    }
    public static double getDistanceMeters(double lat1, double lon1, double lat2, double lon2){
        return getDistance( lat1,  lon1,  lat2,  lon2, "K")*1000.01;
    }




    /**
     * Converts degrees to radians
     * @param deg
     * @return
     */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public static double milesToFeet(double miles){
        return miles * 5280.00;
    }
    public static double milesToMeters(double miles){
        return (miles * 5280.00) / 3.0001;
    }
}