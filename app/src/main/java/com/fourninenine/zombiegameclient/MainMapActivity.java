package com.fourninenine.zombiegameclient;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fourninenine.zombiegameclient.httpServices.RESTInterfaces.RESTUserInterface;
import com.fourninenine.zombiegameclient.httpServices.RESTServices.HttpUserService;
import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.models.Zombie;
import com.fourninenine.zombiegameclient.models.dto.ClientUpdateDto;
import com.fourninenine.zombiegameclient.models.dto.UserActionDto;
import com.fourninenine.zombiegameclient.models.utilities.ApplicationContextProvider;
import com.fourninenine.zombiegameclient.models.utilities.Geomath;
import com.fourninenine.zombiegameclient.services.MapDrawingService;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    HashMap<Long, Zombie> zombies;
    Context context = ApplicationContextProvider.getAppContext();
    SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.user_shared_preferences), MODE_PRIVATE);

    //Related to targetting zombies.
    LongSparseArray<Marker> zombieMarkers;
    long targetZombieId;
    public static boolean enemySelected = false;
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
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45, -95), 16.2f));
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (zombieMarkers != null) {
                    long zomId = -1;
                    try {
                        zomId = Long.parseLong(marker.getTitle().split("/s")[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    targetZombieId = zomId;
                    enemySelected = true;
                    marker.setAlpha(0.3f);
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icopacityblack24));
                }
                if (marker.getPosition() != user.getLocation()) {
                    targetLocation = marker.getPosition();
                }
                return true;
            }
        });

    }
    public void selectMarker(Marker marker){
        marker.setAlpha(.5f);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) throws SecurityException {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        while (mLastLocation == null) {
            //wait(500);
            System.out.println("No Location Found");
        }

        user.setLocation(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        user.addLocation(mLastLocation);
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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //Action viewAction = null /*Action.newAction(
        //        Action.TYPE_VIEW, // TODO: choose an action type.
        /*        "MainMap Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.fourninenine.zombiegameclient.application")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);*/
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "MainMap Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.fourninenine.zombiegameclient.application")
//        );
//        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
    }

    private void placeMarker(String label, MarkerOptions options) {

    }

    private void placePlayerToken() {

    }

    private void updateMap() {
        //for now we are going to have this hard coded for ease of testing
        UserActionDto actionDto;
        RESTUserInterface userService = new HttpUserService();
        UserActionDto userAction = new UserActionDto(user.getId(), user.getLatitude(), user.getLongitude(), UserActionDto.Action.NOTHING);
        Call<ClientUpdateDto> updateCall = userService.update(userAction);
        updateCall.enqueue(new Callback<ClientUpdateDto>() {
            MapDrawingService drawService = new MapDrawingService(user, mMap);

            @Override
            public void onResponse(Call<ClientUpdateDto> call, Response<ClientUpdateDto> response) {
                System.out.println("On success callback");
                zombies = response.body().getZombies();
                populateMap();
            }

            @Override
            public void onFailure(Call<ClientUpdateDto> call, Throwable t) {
                System.out.println("ERROR");
                //throw new IllegalStateException("An error was encountered with the API call");

            }
        });


        System.out.println("Updated map.");


    }

    public void killNearest() {
        Button killBtn = (Button) findViewById(R.id.killButton);
        killBtn.setEnabled(false);
        HttpUserService userService = new HttpUserService();
        if (zombies != null && zombies.size() == 0) {
            showDialog("Quiet...", "There are no zombies around right now, sorry.");
            killBtn.setEnabled(true);
            return;
        }

        int zombieIndex = -1;
        Iterator<HashMap.Entry<Long, Zombie>> zombIt = zombies.entrySet().iterator();
        Zombie closest = null;
        double minDistance = Double.MAX_VALUE;
        double tempDistance = 666;
        long minKey = 666 * 666;
        while (zombIt.hasNext()) {
            Map.Entry next = zombIt.next();
            Zombie nextZom = (Zombie) next.getValue();
            if ((tempDistance = Geomath.getDistance(nextZom.getLocation(), user.getLocation(), "M")) < minDistance) {
                minDistance = tempDistance;
                closest = nextZom;
            }

        }
        //after we've found the closest zombie


        if (closest != null) {
            UserActionDto dto = new UserActionDto(user.getId(), user.getLatitude(), user.getLongitude(), UserActionDto.Action.ATTACK);
            dto.setTarget(closest.getId());
            Call<ClientUpdateDto> zombieCall = userService.update(dto);
            if (!alreadyAttacking)
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
    }

    public static GoogleMap getMap() {
        return mMap;
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        user.addLocation(mLastLocation);
        populateMap();
    }


    public GoogleMap populateMap() {

        mMap.clear();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(user.getLocation());
        if (zombies == null)
            updateMap();
        else {
            Iterator<HashMap.Entry<Long, Zombie>> zombIt = zombies.entrySet().iterator();
            while (zombIt.hasNext()) {
                Zombie zom = (Zombie) zombIt.next();
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
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.northdirection48));

        if (targetLocation != null) {

        }
        mMap.addMarker(userMarker);

        builder.include(user.getLocation());
        LatLngBounds bounds = builder.build();

        /*int padding = 20; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        float zoomLevel = mMap.getCameraPosition().zoom;
        CameraUpdate userUpdate = CameraUpdateFactory.newLatLngZoom(user.getLocation(), zoomLevel);*/

        CameraPosition cameraPosition =   new CameraPosition.Builder()
                .target(user.getLocation())
                .zoom(mMap.getCameraPosition().zoom)
                .bearing(mLastLocation.getBearing())
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        return mMap;
    }

    public Zombie resolveAttack(Call<ClientUpdateDto> call, int zomIndex) {
        final int zombieIndex = zomIndex;
        call.enqueue(new Callback<ClientUpdateDto>() {

            @Override
            public void onResponse(Call<ClientUpdateDto> call, Response<ClientUpdateDto> response) {
                System.out.println("On success callback");
                ClientUpdateDto dto  = response.body();
                zombies = dto.getZombies();
                Zombie zombie = zombies.get(dto.getTargetId());
                if (zombie == null) {
                    return;
                }
                //remove the zombie from the list
                if (!zombie.isAlive()) {
                    showDialog("Killer", "You have destroyzed Zombie " + zombie.getId());
                    zombies.remove(zombie.getId());
                }//else update the local zombie with the server's return object
                else if (zombie.isAlive()) {

                }
                populateMap();
                Button killButton = (Button) findViewById(R.id.killButton);
                killButton.setEnabled(true);
            }

            @Override
            public void onFailure(Call<ClientUpdateDto> call, Throwable t) {
                System.out.println("ERROR");
                Button killButton = (Button) findViewById(R.id.killButton);
                killButton.setEnabled(true);
            }
        });
        return null;

    }

    public void attackZombie(View view) {
        if (enemySelected) {
            killTargetZombie(targetZombieId);
        }else killNearest();
    }

    private void killTargetZombie(long targetZombieId) {
        HttpUserService service = new HttpUserService();
        UserActionDto dto = new UserActionDto(user.getId(), user.getLatitude(), user.getLongitude(),
                UserActionDto.Action.ATTACK);
        dto.setTarget(targetZombieId);

        Call<ClientUpdateDto> call = service.update(dto);
        call.enqueue(new Callback<ClientUpdateDto>() {
            @Override
            public void onResponse(Call<ClientUpdateDto> call, Response<ClientUpdateDto> response) {
                ClientUpdateDto dto = response.body();
                zombies = dto.getZombies();
                user = dto.getUser();
                User.save(user);
                populateMap();
            }

            @Override
            public void onFailure(Call<ClientUpdateDto> call, Throwable t) {
                System.out.println("WHAT HAPPENEND????");
            }
        });
    }

    public void showUserStats(View view) {

    }
}