package com.fourninenine.zombiegameclient;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fourninenine.zombiegameclient.httpServices.RESTInterfaces.RESTUserInterface;
import com.fourninenine.zombiegameclient.httpServices.RESTServices.HttpUserService;
import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.models.Zombie;
import com.fourninenine.zombiegameclient.models.dto.ClientUpdateDto;
import com.fourninenine.zombiegameclient.models.dto.UserActionDto;
import com.fourninenine.zombiegameclient.models.utilities.ApplicationContextProvider;
import com.fourninenine.zombiegameclient.models.utilities.Geomath;
import com.fourninenine.zombiegameclient.models.utilities.Globals;
import com.fourninenine.zombiegameclient.services.activityHelpers.CollectionProcessing;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;

import org.json.JSONException;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //Activity class level flag to ensure we only process one attack at a time
    static boolean alreadyAttacking = false;

    //Map and location based fields
    public static GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    LocationRequest mLocationRequest;
    User user;
    GeoJsonLayer mGameLayer;
    CircleOptions options;

    //Entity Tracking fields
    HashMap<Long, Zombie> zombies;
    HashMap<Long, Boolean> zFreshList;
    HashMap<String, Long> zombieMarkers;

    private long lastNotifiedNetworkFailure = System.currentTimeMillis();

    Context context = ApplicationContextProvider.getAppContext();
    SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.user_shared_preferences), MODE_PRIVATE);

    //Related to targetting zombies.
    long targetZombieId;
    public static boolean enemySelected = false;
    LatLng targetLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.getUser();
        zFreshList = new HashMap<>();
        zombieMarkers = new HashMap<>();
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45, -95), 18.2f));
        googleMap.getUiSettings().setScrollGesturesEnabled(false);

        try {
            mGameLayer = new GeoJsonLayer(getMap(), R.raw.geo_json_test, context);
            mGameLayer.addLayerToMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                long zomId = -1;
                try {
                    zomId = zombieMarkers.get(marker.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                targetZombieId = zomId;
                enemySelected = true;
                marker.setAlpha(0.3f);
                drawCircle(marker.getPosition());
                if(Geomath.getDistance(mLastLocation.getLatitude(), mLastLocation.getLongitude(), marker.getPosition().latitude, marker.getPosition().longitude, "M") < user.getAttackRange()){
                    Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.northdirection48);
                    Bitmap bhalfsize=Bitmap.createScaledBitmap(b, 48, 48, false);
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(bhalfsize));
                    marker.setRotation(mLastLocation.getBearing());

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
        new wank(mMap).execute();
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
        super.onStop();
        mGoogleApiClient.disconnect();


    }

    private void placeMarker(String label, MarkerOptions options) {

    }

    private void placePlayerToken() {

    }
    public static void updateStatic(){

    }
    public  void updateMap() {
        //for now we are going to have this hard coded for ease of testing
        try {
            scanEffect(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), MainMapActivity.this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UserActionDto actionDto;
        RESTUserInterface userService = new HttpUserService();
        UserActionDto userAction = new UserActionDto(user.getId(), user.getLatitude(), user.getLongitude(), UserActionDto.Action.NOTHING);
        Call<ClientUpdateDto> updateCall = userService.update(userAction);
        updateCall.enqueue(new Callback<ClientUpdateDto>() {
            @Override
            public void onResponse(Call<ClientUpdateDto> call, Response<ClientUpdateDto> response) {
                System.out.println("On success callback");
                try{
                    int beforeSize = 0;
                    if(zombies != null)
                        beforeSize = zombies.size();
                    zombies = CollectionProcessing.zombieListToMap(response.body().getZombies());

                    for(Zombie zombie:zombies.values()){
                        if(!zFreshList.containsKey(zombie.getId())){
                            zFreshList.put(zombie.getId(), true);
                        }
                    }
                    if(beforeSize < zombies.size())
                        Globals.showDialog("New Enemies!", "New enemies nearby, they're coming outta the woodwork!", MainMapActivity.this);

                }catch(NullPointerException e){
                    Globals.showDialog("Strange...", "Our network request was succesfull, but " +
                            "there was something wrong with the data", MainMapActivity.this);
                }

                populateMap();
            }

            @Override
            public void onFailure(Call<ClientUpdateDto> call, Throwable t) {
                System.out.println("ERROR");
                Globals.showConnectionDialog(MainMapActivity.this);
            }
        });


        System.out.println("Updated map.");


    }

    public void killNearest() {
        Button killBtn = (Button) findViewById(R.id.killButton);
        //killBtn.setEnabled(false);
        HttpUserService userService = new HttpUserService();
        if (zombies != null && zombies.size() == 0) {
            Globals.showDialog("Quiet...", "There are no zombies around right now, sorry.", MainMapActivity.this);
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
        if(minDistance > user.getAttackRange())
            System.out.println("Need some kind of feedback pane")
        //after we've found the closest zombie


        if (closest != null) {
            UserActionDto dto = new UserActionDto(user.getId(), user.getLatitude(), user.getLongitude(), UserActionDto.Action.ATTACK);
            dto.setTarget(closest.getId());
            Call<ClientUpdateDto> zombieCall = userService.update(dto);
            resolveAttack(zombieCall, zombieIndex);
            //zombies.remove(closest);
        }
        int tempKills = preferences.getInt(context.getString(R.string.user_total_kills), 0);
        preferences.edit().putInt(context.getString(R.string.user_total_kills), (++tempKills)).apply();
        user.setTotalKills(tempKills);
        TextView totalKillsView = (TextView) findViewById(R.id.mapTotalKillsView);
        String updateString = String.format(context.getString(R.string.map_total_kills_display), tempKills);
        totalKillsView.setText(updateString);
        //killBtn.setEnabled(true);
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
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        updateMap();
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
        if (zombies == null){}
        else {
            Iterator<HashMap.Entry<Long, Zombie>> zombIt = zombies.entrySet().iterator();
            while (zombIt.hasNext()) {
                Zombie zom = (Zombie) zombIt.next().getValue();

                MarkerOptions marker = new MarkerOptions()
                        .position(zom.getLocation())
                        .title("Zombie " + zom.getId());
                Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.mipmap.zombie_launcher_1);
                Bitmap bhalfsize=Bitmap.createScaledBitmap(b, 48, 48, false);
                marker.icon(BitmapDescriptorFactory.fromBitmap(bhalfsize));
                marker.rotation(new Random().nextFloat());

                Marker zomMarker = mMap.addMarker(marker);
                zombieMarkers.put(zomMarker.getId(), zom.getId());
                builder.include(zom.getLocation());

            }
        }
        //place the user marker
        MarkerOptions userMarker = new MarkerOptions()
                .position(user.getLocation())
                .title(user.getName())
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.northdirection48)).rotation(mLastLocation.getBearing());
        mMap.addMarker(userMarker);
        drawCircle(user.getLocation());



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
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        return mMap;
    }

    public Zombie resolveAttack(Call<ClientUpdateDto> call, int zomIndex) {
        call.enqueue(new Callback<ClientUpdateDto>() {
            @Override
            public void onResponse(Call<ClientUpdateDto> call, Response<ClientUpdateDto> response) {
                System.out.println("On success callback");
                ClientUpdateDto dto  = response.body();
                zombies = CollectionProcessing.zombieListToMap(dto.getZombies());
                Zombie zombie = zombies.get(dto.getTargetId());
                if (zombie == null) {

                    Globals.showDialog("WTF", "Zombie is null? No fucking way", MainMapActivity.this);
                    return;
                }
                //remove the zombie from the list
                if (!zombie.isAlive()) {
                    Globals.showDialog("Killer", "You have destroyzed Zombie " + zombie.getId(), MainMapActivity.this);
                    zombies.remove(zombie.getId());
                }//else update the local zombie with the server's return object
                else if (zombie.isAlive()) {
                    zombies.put(zombie.getId(), zombie);
                    Globals.showDialog("Damage", "You hit the zombie, but did not kill it", MainMapActivity.this);

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
        if(zombies == null || zombies.size() < 1){
            Globals.showDialog("All quiet", "Theres no geeks. Theres no raiders, Too quiet...", MainMapActivity.this);
            return;
        }
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
                if(dto == null || dto.getZombies() == null){
                   updateMap();
                }else{
                    Log.d("Null", "Something was null");
                    CollectionProcessing.zombieListToMap(dto.getZombies());
                    user = dto.getUser();
                    User.save(user);
                    populateMap();
                }



            }

            @Override
            public void onFailure(Call<ClientUpdateDto> call, Throwable t) {
                if(System.currentTimeMillis() - lastNotifiedNetworkFailure > 50000)
                Globals.showDialog("Network Problems", "Sorry, but you seem to be experiencing network " +
                        "issues.", MainMapActivity.this);
            }
        });
    }

    public void showUserStats(View view) {

    }

    public void updateFromView(View view) {
        updateMap();
    }
    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(30);

        // Border color of the circle
        circleOptions.strokeColor(Color.RED);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }
    public void scanEffect(LatLng userLocation, MainMapActivity activityContext) throws InterruptedException {



    }
    private int randomizeColor(){
        Random randomizer = new Random();
        switch(randomizer.nextInt()%4) {
            case 0:
                return Color.BLUE;
            case 1:
                return Color.CYAN;
            case 2:
                return Color.GRAY;
            case 3:
                return Color.WHITE;
            default : return Color.TRANSPARENT;
        }
    }

    public class wank extends AsyncTask<LatLng, CircleOptions, HashMap<CircleOptions, GoogleMap>> {

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
        protected HashMap<CircleOptions, GoogleMap> doInBackground(LatLng... params) {
            float perceptionRange = preferences.getFloat("perception_range", 35);
            HashMap<CircleOptions, GoogleMap> returnMap =  new HashMap<>();

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


    }

}
