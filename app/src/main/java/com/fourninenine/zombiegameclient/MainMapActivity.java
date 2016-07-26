package com.fourninenine.zombiegameclient;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.TextView;
import com.fourninenine.zombiegameclient.httpServices.RESTInterfaces.RESTUserInterface;
import com.fourninenine.zombiegameclient.httpServices.RESTServices.HttpUserService;
import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.models.Zombie;
import com.fourninenine.zombiegameclient.models.dto.ClientUpdateDto;
import com.fourninenine.zombiegameclient.models.dto.UserActionDto;
import com.fourninenine.zombiegameclient.models.entityMediators.EntityTargetMediator;
import com.fourninenine.zombiegameclient.models.utilities.ApplicationContextProvider;
import com.fourninenine.zombiegameclient.models.utilities.Geomath;
import com.fourninenine.zombiegameclient.models.utilities.Globals;
import com.fourninenine.zombiegameclient.services.activityHelpers.CollectionProcessing;
import com.fourninenine.zombiegameclient.views.ResizeAnimation;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.geojson.GeoJsonLayer;

import org.json.JSONException;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //Map and location based fields
    public static GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    LocationRequest mLocationRequest;
    User user;
    CameraPosition lastCameraPosition;
    GeoJsonLayer mGameLayer;
    CircleOptions options;


    //Entity Tracking fields
    HashMap<Long, Zombie> zombies;
    HashMap<Long, Boolean> zFreshList;
    HashMap<String, Long> zombieMarkers;

    private long lastNotifiedNetworkFailure = System.currentTimeMillis();


    Context context = ApplicationContextProvider.getAppContext();
    SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.user_shared_preferences), MODE_PRIVATE);

    /* Related to targetting zombies. */
    public static EntityTargetMediator tgtObj;
    long targetZombieId;
    LatLng targetLocation;
    boolean targetting = false;
    /* /targetting */

    /* View manipulation and access fields */
    long lastGuruPrint;
    ResizeAnimation resizeAnimation;


    /* /views */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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

        /** TODO : put this back in but up in the hud **/
        String nameString = Globals.getUser().getName();
        //((TextView) findViewById(R.id.user_name)).setText(nameString);
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45, -95), 19f));
        lastCameraPosition = mMap.getCameraPosition();
        googleMap.getUiSettings().setScrollGesturesEnabled(false);



           /* try {
                mGameLayer = new GeoJsonLayer(mMap, R.raw.midnight_commander_theme, context);
            } catch (IOException e) {
              Log.d( "Read error", "Issue reading json file");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mGameLayer.addLayerToMap();
            */

        /* This event listener is going to handle the Case of clicking randomly on the map. If we start using layers for */
        /* GeoJSON we may need a //TODO : refacor this event listener into a layer if we are overlaying click events on the map. */

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

            @Override
            public void onMapClick(LatLng latLng) {

            }
        });
        mMap.setOnMarkerClickListener(  new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("onMarkerClick", "Marker clicked");
                boolean markerInRange;
                Double aRange = (double)(preferences.getFloat(context.getString(R.string.user_attack_range), 10));
                if(Geomath.getDistanceMeters(mLastLocation.getLatitude(), mLastLocation.getLongitude(), marker.getPosition().latitude,marker.getPosition().longitude) > aRange) {
                    printToGuru("Out of Range", "You're going to need a longer stick.");
                    return false;
                }
                return targetZombie(marker);
            }

        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if(lastCameraPosition != null && lastCameraPosition.zoom <= 18 && cameraPosition.zoom > 18 )
                    populateMap(true);

            }

        });

    }
    private boolean targetZombie2(Marker marker) {
      //  marker.getId()
        return false;

    }
    private boolean targetZombie(Marker marker) {
        marker.showInfoWindow();
        long zomId = -1;
        Zombie tgtZom;
        if(zombieMarkers == null)
            zombieMarkers = new HashMap<String, Long>();

        try {
            zomId = zombieMarkers.get(marker.getId());
            tgtZom = zombies.get(zomId);

            if(tgtZom == null){
                Log.d("Zombie Marker", "Your zombie target was null while looking for " + marker.getId() + " From: " + zombieMarkers.get(marker.getId()));
                Log.d("Null Target", "Your zombie target was null while looking for " + zomId);
                System.out.println(zombies.size());
                return false;
            }else{
                Log.d("Found Zombie", "Found zombie : " + tgtZom.getId()) ;
            }
            Log.d("Mmap.targetZombie", "finding target zombie");
            tgtObj = new EntityTargetMediator(EntityTargetMediator.Entities.ZENT, zomId, mLastLocation, marker.getId());
            System.out.println("Catch line break");

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("<MEB-DEBUG>", "<MEB> 7.21 ***** MainMapActivity Bad access to zombieMarkers or marker.getId()");
            printToGuru("Targetting Malfunction", "The targetting computer seems to be offline, please recalibrate and try");
            targetting = false;
            updateMap();
            return false;
        }

        marker.setAlpha(0.3f);
        double dist;
        boolean inRange =( dist = Geomath.getDistanceMeters(mLastLocation.getLatitude(), mLastLocation.getLongitude(), marker.getPosition().latitude, marker.getPosition().longitude)) <= user.getAttackRange();
        //double dist = Geomath.getDistanceMeters(mLastLocation.getLatitude(), mLastLocation.getLongitude(), marker.getPosition().latitude, marker.getPosition().longitude);
        Log.d("MapAct.targetZombie", "Distance: " + dist);
        if(inRange){
            targetting = true;
            drawCircle(marker.getPosition(), Color.CYAN, 10);
            Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.crosshairs);
            Bitmap bhalfsize=Bitmap.createScaledBitmap(b, 48, 48, false);
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(bhalfsize));
            marker.setRotation(mLastLocation.getBearing());

            //Create the location for the new zombie
            Location targetZomLoc = new Location("Zombie " + zomId);
            targetZomLoc.setLatitude(tgtZom.getLocation().latitude);
            targetZomLoc.setLongitude(tgtZom.getLocation().longitude);
            tgtZom.setLocation(targetZomLoc);

            CameraPosition cameraPosition =   new CameraPosition.Builder()
                    .target(user.getLocation())
                    .zoom(18.5f)
                    .bearing(mLastLocation.bearingTo((targetZomLoc)))
                    .tilt(65)
                    .build();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(user.getLocation());
            builder.include(tgtZom.getLocation());
            LatLngBounds bounds = builder.build();

            Log.d("Target zombie", "TgtZombie : " + tgtZom.toString());
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 17));
            return targetting;
        }else{
            printToGuru("Out of Range", "You could always throw something...");
            targetting = false;

        }
         return false;
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

        this.user = Globals.getUser();
        user.setLatitude(mLastLocation.getLatitude());
        user.setLongitude(mLastLocation.getLongitude());
        user.addLocation(mLastLocation);

        User.save(user);
        //new wank(mMap).execute();
        updateMap();
        startLocationUpdates();
    }

    public void guruOnline(){
        TextView myTextView=(TextView)findViewById(R.id.guru_text_view);
        myTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //findViewById(R.id.guru_text_view).set
            }
        });
        Typeface typeFace= Typeface.createFromAsset(getAssets(),"computer_pixel-7.ttf");
        myTextView.setTextSize(24f);
        myTextView.setTypeface(typeFace);
        String msg;
        if(user != null){
            user = User.getUser();
             msg = ("Welcome, " + user.getName() + ", its good to see you're still with us." +
                    "I certainly have learned not to take such small miracles for granted! Please " +
                    "feel free to reach me on the com by clicking my icon in the upper right hand corner" +
                    "of your screen.");
        }else{
             msg = "I'm not sure what you're trying to pull here, but this is a private channel and you are" +
                    " NOT displaying the proper credentials. I will redirect you back through security. Good day! (Login error)";

        }
        printToGuru("Welcome to the void!", msg);
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
        RESTUserInterface userService = new HttpUserService();
        User user = User.getUser();
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

                    if(beforeSize < zombies.size() && (System.currentTimeMillis() - preferences.getLong(context.getString(R.string.user_last_updated), 0) > 50000)) {
                        Globals.showDialog("New Enemies!", "New enemies nearby, they're coming outta the woodwork!", MainMapActivity.this);
                        preferences.edit().putLong(context.getString(R.string.user_last_updated), System.currentTimeMillis()).apply();
                    }
                }catch(NullPointerException e){
                    //TODO: replace magic number
                    if(System.currentTimeMillis() - lastNotifiedNetworkFailure < 60000)
                        return;
                    Globals.showDialog("Strange...", "Our network request was succesfull, but " +
                            "there was something wrong with the data", MainMapActivity.this);
                    lastNotifiedNetworkFailure = System.currentTimeMillis();
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
        Log.d("killNearest", "Kill nearest executging");
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
            if ((tempDistance = Geomath.getDistance(nextZom.getLocation().latitude, nextZom.getLocation().longitude, user.getLocation().latitude, user.getLocation().longitude, "M")) < minDistance) {
                minDistance = tempDistance;
                closest = nextZom;
            }

        }
        if(minDistance > user.getAttackRange())      {

        }

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
        Log.d("On location change", "ON LOCATION CHANGE");
        //Temp fix to avoid jumping too far. if jump more than .1 miles
        /*double tDist = Geomath.getDistanceMeters(mLastLocation.getLatitude(), mLastLocation.getLongitude(), location.getLatitude(), location.getLongitude());
        Log.d("Map.onLocationChanged", "Distance in meters: " + tDist);
        if(tDist > 100 && user != null){
            Globals.showDialog("You are too fast","You traveled: " + tDist + " in " + (System.currentTimeMillis() - User.lastModified) + " seconds",
                    MainMapActivity.this);
            return;
        }*/


        user.setLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        User.save(user);
        user.addLocation(mLastLocation);
        mLastLocation = location;

        populateMap();
    }

    public GoogleMap populateMap(){return populateMap(false);}
    public GoogleMap populateMap(Boolean isZoomedIn) {
        String setString = context.getString(R.string.guru_display_name) + " " + new Date();
        if(System.currentTimeMillis() - lastGuruPrint > 3000)
            ((TextView)findViewById(R.id.guru_text_view)).setText(setString);

        //While targetting we don't want to dump the markers we are usiung
        zombieMarkers.clear();
        mMap.clear();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (zombies == null){}
        else {
            Iterator<Zombie> zombIt = zombies.values().iterator();
            while (zombIt.hasNext()) {
                Zombie zom = (Zombie) zombIt.next();

                MarkerOptions marker = new MarkerOptions()
                        .position(zom.getLocation())
                        .title("Zombie " + zom.getId());
                int zomIconId;
                if(isZoomedIn){
                    System.out.println("Yeah");
                    zomIconId = R.drawable.zombiebot;

                } else zomIconId =R.drawable.zombie_hand_icon3;
                    Bitmap b = BitmapFactory.decodeResource(context.getResources(), zomIconId);
                Bitmap bhalfsize=Bitmap.createScaledBitmap(b, 48, 48, false);
                marker.icon(BitmapDescriptorFactory.fromBitmap(bhalfsize));
                marker.rotation(new Random().nextFloat());

                //
                Marker zomMarker;
                zomMarker = mMap.addMarker(marker);
                //if(!(zombieMarkers.values().contains(zom.getId()))){
                //}
                //zombieMarkers.values().
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

        drawCircle(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Color.RED, user.getAttackRange());
        drawCircle(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Color.BLUE, user.getPerceptionRange());
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
                .tilt(mMap.getCameraPosition().tilt)
                .build();

        /**
         * When in targetting mode, we don't animate the camera from here.
         */
        if(!targetting){
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 17));
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

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
                tgtObj = null;
                if (zombie == null) {
                    TextView totalKills = (TextView) findViewById(R.id.totalKills);
                    totalKills.setText("Total Kills: " + user.getTotalKills());
                    printToGuru("Evisceration", "Total destruction, just a stinkin smudge on the sidewalk.");

                    return;
                }
                //remove the zombie from the list
                if (!zombie.isAlive()) {
                    printToGuru("<|>g.u.r.u.<|> : ", "You've destroyed target " + zombie.getId() + " your account " +
                            "will be credited.");
                    zombies.remove(zombie.getId());
                }//else update the local zombie with the server's return object
                else if (zombie.isAlive()) {
                    zombies.put(zombie.getId(), zombie);
                    printToGuru("Damaged Foe", "You hit the zombie but... Do zombies get angry...?");

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
                tgtObj = null;
            }
        });
        return null;

    }

    public void attackZombie(View view) {

        if(zombies == null || zombies.size() < 1){
            Globals.showDialog("All quiet", "Theres no geeks. Theres no raiders, Too quiet...", MainMapActivity.this);
            return;
        }
        if (tgtObj != null) {
            Log.d("tgtObj not null", "Attacking a targetted zombie.");
            Log.d("tgtObj ", "ID : " + tgtObj.getEid());
            Log.d("tgtObj ", "MarkerId: " +  tgtObj.getMarkerId());
            Zombie tZombie = zombies.get(tgtObj.getEid())     ;
            LatLng zomLoc = tZombie.getLocation();
            if(isTargetInRange(zomLoc)) {

                printToGuru("Quick and Quiet", "Targetting Zombie wit: { ID: " + tgtObj.getEid() + "}");
                tZombie.setUnderAttack(true);
                killTargetZombie(tgtObj.getEid());

            }

        }else{
            printToGuru("No Targetting", "Lashing out at nearest target");
            killNearest();
        }
    }

    private boolean isTargetInRange(LatLng zomLoc) {
        double dist = Geomath.getDistance(user.getLatitude(), zomLoc.latitude, user.getLongitude() , zomLoc.longitude, "M");
         printToGuru("Calculating distance: ","Distance : " + dist);

        /*if(dist > user.getAttackRange()){

           printToGuru("Out of Range", "Target Locked, but is not within melee range. {ZID : " + zombies.get(tgtObj.getEid()) + "}");
           return false;
        }*/
        return true;
    }

    private void killTargetZombie(long targetZombieId) {
        Log.d("killTargetZombie", "HItting it with id: " + targetZombieId);
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
                   tgtObj = null;
                   targetting = false;
                   updateMap();
                }else{
                    CollectionProcessing.zombieListToMap(dto.getZombies());
                    user = dto.getUser();
                    User.save(user);
                    tgtObj = null;
                    targetting = false;
                    populateMap();
                }



            }

            @Override
            public void onFailure(Call<ClientUpdateDto> call, Throwable t) {
                if(System.currentTimeMillis() - lastNotifiedNetworkFailure > 50000)
                Globals.showDialog("Network Problems", "Sorry, but you seem to be experiencing network " +
                        "issues.", MainMapActivity.this);
                tgtObj = null;
            }
        });
    }

    public void showUserStats(View view) {

    }

    public void updateFromView(View view) {
        User thisUser = User.getUser();
        Globals.showDialog("Character Info",
                "< Name >: " + thisUser.getName() + "< ID > : " + thisUser.getId() + "\n< HP > : " + thisUser.getHp()
                + "\n< Attack Range > " + thisUser.getAttackRange() + "\n< Perception Range > : " + thisUser.getPerceptionRange()
                + "\n< Ammo > : " + thisUser.getAmmo(), MainMapActivity.this);
        updateMap();
    }

    private void drawCircle(LatLng thePoint, int theColor, double theRadius){


        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(thePoint);

        // Radius of the circle
        circleOptions.radius(theRadius);

        // Border color of the circle
        circleOptions.strokeColor(theColor);

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

    public void printToGuru(String tagline, String message){
        lastGuruPrint = System.currentTimeMillis();
        SharedPreferences prefs = Globals.getPreferences();
        TextView guVu = (TextView)findViewById(R.id.guru_text_view);
        guVu.setText("<{ GURU" + new Date()  + " }>: " + message + "\n");
        ViewPropertyAnimator animator =  guVu.animate()
                .setDuration(200)
                .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                    }
                });
       /* Timer timer = new Timer();
        timer.schedule(new TimerTask(){

                           @Override
                           public void run() {
                               TextView guVu = (TextView)findViewById(R.id.guru_text_view);
                               (MainMapActivity.this).printToGuru("", "<{ GURU " + new Date()  + " }>:");
                           }
                       }


                ,1, 2000);*/
    }

    public void guruGlitch(){
        Random rand = new Random();
        if(rand.nextInt(10) <= 8)
            return;
        TextView guVu =  (TextView)findViewById(R.id.guru_text_view);
        String currentMessage = (String)guVu.getText();
        guVu.animate().setDuration(400).alpha(5.67f);
        guVu.setText("<< ALERT -<> : SUSPICIOUS ACTIVITY has been detected in your sector >> :root$");
        guVu.append("\n Please be advised, during this particularly volatile time, community " +
                "central will be paying 4 times the usual bounty on Raider's heads. Happy hunting!");
        try {
            wait(200);
            guVu.setText("We will now return you to your regularly scheduled computing.");
            guVu.clearComposingText();
            guVu.setText(currentMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * to be implement  ed to allow us to visit the guru
     * @param view
     */
    public void guruActivity(View view) {
    }

    public void toggleGuruHandler(View view) {
        //Get the actual guru text view instead of the button's view

        View guTextView = findViewById(R.id.guru_text_view);
        int collapsedHeight = 0;
        int openHeight = 90;

        toggleGuruHandlerAux(guTextView, openHeight, collapsedHeight, false );
    }
    public synchronized void toggleGuruHandlerAux(View view, int openHeight, int collapsedHeight , boolean startCollapsed ){
        /* This should only evaluate to true the first time the button is pushed when the activity is rendered i*/
        if(resizeAnimation == null){

             resizeAnimation = new ResizeAnimation(
                    view,
                    openHeight,
                    collapsedHeight,
                    startCollapsed
            );
        }
        //TODO : Get rid of magic dduration
        resizeAnimation.setDuration(250);
        view.startAnimation(resizeAnimation);
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
