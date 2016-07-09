package com.fourninenine.zombiegameclient;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fourninenine.zombiegameclient.httpServices.RESTInterfaces.RESTUserInterface;
import com.fourninenine.zombiegameclient.httpServices.RESTServices.HttpUserService;
import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.models.Zombie;
import com.fourninenine.zombiegameclient.models.dto.UserActionDto;
import com.fourninenine.zombiegameclient.models.utilities.ApplicationContextProvider;
import com.fourninenine.zombiegameclient.models.utilities.Geomath;
import com.fourninenine.zombiegameclient.services.MapDrawingService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //Activity class level flag to ensure we only process one attack at a time
    static boolean alreadyAttacking = false;


    static GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    LocationRequest mLocationRequest;
    User user;
    ArrayList<Zombie> zombies;
    Context context = ApplicationContextProvider.getAppContext();
    SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.user_shared_preferences), MODE_PRIVATE);

    LatLng targetLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.getUser();
        setContentView(R.layout.activity_main_map);
        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        createLocationRequest();
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

        //Set the user specific parts of the UI upon setup.
        String totalKillsString = String.format(context.getString(R.string.map_total_kills_display), user.getTotalKills());
        ((TextView) findViewById(R.id.mapTotalKillsView)).setText(totalKillsString);
        String nameString = user.getName();
        ((TextView) findViewById(R.id.mapNameField)).setText(nameString);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) throws SecurityException {
        mMap = googleMap;
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(45,-95) , 16.2f) );
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                } else {
                    marker.showInfoWindow();
                }
                if(marker.getPosition() != user.getLocation()){
                    targetLocation = marker.getPosition();
                }
                return true;
            }
        });

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) throws SecurityException {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        while (mLastLocation == null) {
            //wait(500);
            System.out.println("No Location Found");
        }
        user.setLocation(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        updateMap();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void placeMarker(String label, MarkerOptions options) {

    }

    private void placePlayerToken() {

    }

    private void updateMap() {
        //for now we are going to have this hard coded for ease of testing
        UserActionDto actionDt;
        RESTUserInterface userService = new HttpUserService();
        UserActionDto userAction = new UserActionDto(user.getId(), user.getLatitude(), user.getLongitude(), UserActionDto.Action.NOTHING);
        Call<ArrayList<Zombie>> updateCall = userService.update(userAction);
        updateCall.enqueue(new Callback<ArrayList<Zombie>>() {
            MapDrawingService drawService = new MapDrawingService(user, mMap);

            @Override
            public void onResponse(Call<ArrayList<Zombie>> call, Response<ArrayList<Zombie>> response) {
                System.out.println("On success callback");

                zombies = response.body();
                populateMap();
            }

            @Override
            public void onFailure(Call<ArrayList<Zombie>> call, Throwable t) {
                System.out.println("ERROR");
                //throw new IllegalStateException("An error was encountered with the API call");

            }
        });


        System.out.println("Updated map.");


    }

    public void killNearest(View view) {
        Button killBtn = (Button) findViewById(R.id.killButton);
        killBtn.setEnabled(false);
        HttpUserService userService = new HttpUserService();
        Zombie closest = null;
        if (zombies != null && zombies.size() == 0) {
            showDialog("Quiet...","There are no zombies around right now, sorry.");
            killBtn.setEnabled(true);
            return;
        }

        double minDistance = Double.MAX_VALUE;
        int zombieIndex = -1;
        for (int i = 0; i < zombies.size(); i++) {
            double thisDistance = Geomath.getDistance(zombies.get(i).getLocation().latitude, zombies.get(i).getLocation().longitude,
                    user.getLocation().latitude, user.getLocation().longitude, "M");
            if (thisDistance < minDistance) {
                minDistance = thisDistance;
                closest = zombies.get(i);
                zombieIndex = i;
            }
            System.out.println("This distance: " + thisDistance);
            System.out.println("Min distance: " + minDistance);
        }
        //after we've found the closest zombie
        if (closest != null) {
            UserActionDto dto = new UserActionDto(user.getId(), user.getLatitude(), user.getLongitude(), UserActionDto.Action.ATTACK);
            dto.setTarget(closest.getId());
            Call<Zombie> zombieCall = userService.attack(dto);
            if(!alreadyAttacking)
                resolveAttack(zombieCall, zombieIndex);
            //zombies.remove(closest);
        }
        int tempKills = preferences.getInt(context.getString(R.string.user_total_kills), 0);
        preferences.edit().putInt(context.getString(R.string.user_total_kills), (++tempKills)).apply();
        user.setTotalKills(tempKills);
        TextView totalKillsView = (TextView) findViewById(R.id.mapTotalKillsView);
        String updateString = String.format(context.getString(R.string.map_total_kills_display), tempKills);
        totalKillsView.setText(updateString);
        populateMap();
    }

    private void showDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainMapActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.drawable.zombiehand48);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static GoogleMap getMap() {
        return mMap;
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(8000);
        mLocationRequest.setFastestInterval(250);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Override
    public void onLocationChanged(Location location) {
        System.out.println("Location has Changed.");
        Log.d("On location change", "ON LOCATION CHANGE");
        user.setLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        mLastLocation = location;
        populateMap();
    }


    public GoogleMap populateMap(){

        mMap.clear();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(user.getLocation());
        if(zombies == null)
            updateMap();
        else{
            Iterator<Zombie> zombIt = zombies.iterator();
            while(zombIt.hasNext()){
                Zombie zom = zombIt.next();
                MarkerOptions marker = new MarkerOptions()
                        .position(zom.getLocation())
                        .title("Zombie " + zom.getId());


                mMap.addMarker(marker);
                builder.include(zom.getLocation());
            }
        }
        //place the user marker
        MarkerOptions userMarker = new MarkerOptions()
                .position(user.getLocation())
                .title(user.getName())
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.crosshairs));
        if(targetLocation != null){

        }
        mMap.addMarker(userMarker);

        builder.include(user.getLocation());
        LatLngBounds bounds = builder.build();

        int padding = 20; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        float zoomLevel = mMap.getCameraPosition().zoom;
        CameraUpdate userUpdate = CameraUpdateFactory.newLatLngZoom(user.getLocation(), zoomLevel);

        mMap.moveCamera(userUpdate);

        return mMap;
    }
    /*public static void showDialog(String title, String message, Activity activity) {
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
        alertDialog.show();
    }*/
    public Zombie resolveAttack(Call<Zombie> call, int zomIndex){
        final int zombieIndex = zomIndex;
        call.enqueue(new Callback<Zombie>() {

            @Override
            public void onResponse(Call<Zombie> call, Response<Zombie> response) {
                System.out.println("On success callback");
                Zombie zombie;
                zombie = response.body();
                if(zombie == null){
                    return;
                }
                //remove the zombie from the list
                if(!zombie.isAlive() && zombies.remove(zombieIndex) != null){
                    showDialog("Killer","You have destroyzed Zombie " + zombie.getId());
                }//else update the local zombie with the server's return object
                else if(zombie.isAlive()){
                    zombies.remove(zombieIndex);
                    zombies.add(zombieIndex, zombie);
                }
                populateMap();
                Button killButton = (Button) findViewById(R.id.killButton);
                killButton.setEnabled(true);
            }
            @Override
            public void onFailure(Call<Zombie> call, Throwable t) {
                System.out.println("ERROR");
                Button killButton = (Button) findViewById(R.id.killButton);
                killButton.setEnabled(true);
            }
        });
        return null;

    }
}
