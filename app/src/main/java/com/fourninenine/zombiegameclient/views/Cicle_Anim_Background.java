/*package com.fourninenine.zombiegameclient.views;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fjorgeDevelopers on 7/26/16.
 public class wank extends AsyncTask<LatLng, CircleOptions, HashMap<CircleOptions, GoogleMap>> {


public class wank extends AsyncTask<LatLng, CircleOptions, HashMap<CircleOptions, GoogleMap>> {
 SharedPreferences preferences = a
 private LatLng userLocation;
 private GoogleMap map;



 public wank(GoogleMap map){
 this.map = map;
 }

 @SuppressWarnings("unchecked")
 @Override
 protected void onProgressUpdate(CircleOptions... item) {
 super.onProgressUpdate(item);
 System.out.println("In progress update, circleOptions arg : " + item[0]);
 if(map != null){
 map.addCircle(item[0]);
 }
 }

 @Override
 protected void onPostExecute(HashMap<CircleOptions, GoogleMap> optionsMapMap) {
 for(Map.Entry<CircleOptions, GoogleMap> entry : optionsMapMap.entrySet()){
 if(entry != null)
 entry.getValue().addCircle(entry.getKey());
 }


 }
 @Override
 protected HashMap<CircleOptions, GoogleMap> doInBackground(LatLng... params){

 }
 float perceptionRange = preferences.getFloat("perception_range", 35);
 HashMap<CircleOptions, GoogleMap> returnMap a=  new HashMap<>();

 for (int i = 0; i < perceptionRange; i++) {
 LatLng itLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
 System.out.println("visual effect do in background, iteration : " + i);
 if (options == null)
 options = new CircleOptions();
 options
 .center(itLocation)
 .radius(i * 2)
 .fillColor(randomizeColor())
 .strokeColor(Color.CYAN);
 //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.latitude, userLocation.longitude), (18.2f - (float)(i*.15))));
 synchronized (new Object()){
 try {
 wait(300);
 returnMap.put(options, map) ;
 System.out.println("Returning circle/hashmap after Sleeping");
 return returnMap;
 } catch (Exception e) {
 e.printStackTrace();

 }
 }


 }
 return returnMap;
 }
}*/