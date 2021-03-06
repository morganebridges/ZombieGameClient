package com.fourninenine.zombiegameclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.fourninenine.zombiegameclient.httpServices.RESTServices.HttpUserService;
import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.models.utilities.ApplicationContextProvider;
import com.fourninenine.zombiegameclient.models.utilities.DatabaseHelper;
import com.fourninenine.zombiegameclient.models.utilities.Globals;
import com.fourninenine.zombiegameclient.services.RegistrationIntentService;

import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity{


    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /* This part of the login controller is dedicated a) retrieving a uid from prefs or b) requesting
         * for a user id from the server which will then persist. If we want more sophisticated user creation/accessing
          * we will handle it later*/
        login();



    }

    /**
     * Asks the server for a user object. If we have a UID in shared prefs it will grab it,
     * otherwise we will get a new user from the server.
     * @return
     */
    private void login() {
        User user;
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.user_shared_preferences), MODE_PRIVATE);
        preferences.edit().putLong(context.getString(R.string.user_id), -1).apply();
        if(preferences.getLong(context.getString(R.string.user_id), -1) != -1){
            user = User.getUser();
        }
        else{
            Globals.showDialog("New User","Choose a user name", LoginActivity.this);

        }
        user = new User();

        HttpUserService userService = new HttpUserService();
        Call<User> call = userService.login(user.getId());

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                asyncLogin(response);


            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Globals.showConnectionDialog(LoginActivity);

            }
        });
    }

    private User asyncLogin(Response<User> response){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.user_shared_preferences), MODE_PRIVATE);
        User user = null;

        if(response != null){
            user = response.body();
            System.out.println("break here");
            if(user != null){
                User.save(user);
            } else System.out.println("User null after login");
            if (Globals.checkPlayServices()) {
                // Start IntentService to register this application with GCM.

                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }else{

            System.out.println("There was a problem with the response object");

        }
        return user;
    }
    public static Context getAppContext(){
        return context;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void navigateMap(View view){
        Intent mapIntent = new Intent(this, MainMapActivity.class);
        startActivity(mapIntent);
    }

}
