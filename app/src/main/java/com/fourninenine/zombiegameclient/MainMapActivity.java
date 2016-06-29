package com.fourninenine.zombiegameclient;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.fourninenine.zombiegameclient.httpServices.RESTInterfaces.RESTUserInterface;
import com.fourninenine.zombiegameclient.httpServices.RESTServices.HttpUserService;
import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.models.Zombie;
import com.fourninenine.zombiegameclient.models.dto.UserActionDto;
import com.fourninenine.zombiegameclient.services.LocationListenerService;
import com.fourninenine.zombiegameclient.services.MapDrawingService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    static GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    User user;
    ArrayList<Zombie> zombieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) throws SecurityException{
        mMap = googleMap;
        //Add a marker to my last location dnd center the camera.
        LocationListener locationListener;
        LocationRequest request = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        while(mLastLocation == null){
            System.out.println("Still null");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        }

        // requestLocationUpdates(mGoogleApiClient, request, locationListener);


        LatLng lastLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(lastLatLng).title("My Last Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLatLng));
        updateMap();
        System.out.println("Past updating the map");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) throws SecurityException{

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        while (mLastLocation == null) {
           System.out.println("No Location Found");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    protected void onStart(){
        mGoogleApiClient.connect();
        super.onStart();
        user = User.getUser();
    }
    @Override
    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void placeMarker(String label, MarkerOptions options){

    }

    private void placePlayerToken(){

    }
    private void updateMap(){
        //for now we are going to have this hard coded for ease of testing
        UserActionDto actionDt;
        user.setLocation(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        RESTUserInterface userService = new HttpUserService();
        UserActionDto userAction = new UserActionDto(user.getId(), user.getLatitude(), user.getLongitude(), UserActionDto.Action.NOTHING);
        Call<ArrayList<Zombie>> updateCall = userService.update(userAction);
        updateCall.enqueue(new Callback<ArrayList<Zombie>>() {
            MapDrawingService drawService = new MapDrawingService(user, mMap);

            @Override
            public void onResponse(Call<ArrayList<Zombie>> call, Response<ArrayList<Zombie>> response) {
                System.out.println("On success callback");

                ArrayList<Zombie> zombies = response.body();

                Iterator<Zombie> zombIt= zombies.iterator();
                drawService.placeZombies(zombIt);
            }
            @Override
            public void onFailure(Call<ArrayList<Zombie>> call, Throwable t) {
                System.out.println("ERROR");
                throw new IllegalStateException("An error was encountered with the API call");

            }
        });

        System.out.println("Updated map.");


    }

    public void killNearest(View view) {

    }
    public static GoogleMap getMap(){
        return mMap;
    }
}
