package com.fourninenine.zombiegameclient;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.fourninenine.zombiegameclient.httpServices.RESTServices.HttpUserService;
import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.models.utilities.ApplicationContextProvider;
import com.fourninenine.zombiegameclient.models.utilities.Globals;
import com.fourninenine.zombiegameclient.services.QuickstartPreferences;
import com.fourninenine.zombiegameclient.services.RegistrationIntentService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivityFinal extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    Context context;
    long userId;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /* Should be the one and only call to set the app context */
        context = this.getApplicationContext();
        ApplicationContextProvider.setContext(context);
        preferences = Globals.getPreferences();

        setContentView(R.layout.activity_login_activity_final);
        Button mNameSignInButton = (Button) findViewById(R.id.sign_in_button);
        attachSignInButtonHandler(mNameSignInButton);

        // Set up the login form.
        mNameView = (AutoCompleteTextView) findViewById(R.id.user_name);
        populateAutoComplete();

        //Set up UI variations for an existing vs a new user.
        if(User.isSavedUser()){
            mNameView.setText(preferences.getString(context.getString(R.string.user_name), "Noooo"));
            mNameView.setEnabled(false);
            if (mNameSignInButton != null) {
                mNameSignInButton.setText(context.getString(R.string.u_exists_login_btn_txt));
            }

        }else{
            mNameView.setHint(context.getString(R.string.username_hint));
            if (mNameSignInButton != null) {
                mNameSignInButton.setText(context.getString(R.string.u_not_exist_login_btn_txt));
            }


        }

        mNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
             /*   if ( (mNameView.getText().length() > 1 || id == EditorInfo.IME_NULL)) {
                    login();
                    return true;
                }else {
                    Globals.showDialog("Invalid username", "Please enter a username", LoginActivityFinal.this);
                }
                return false;*/
                loginLogic();
                return true;
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attachSignInButtonHandler(Button mNameSignInButton) {

        mNameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginLogic();

            }
        });
    }

    private void loginLogic(){

        if(User.isSavedUser()){

            login(preferences.getLong(context.getString(R.string.user_id), -1));
        }else{
            String name = (mNameView.getText().toString());
            if(!(name.length() > 1)){
                Globals.showDialog("Invalid Username", "No saved user, enter username to create.", LoginActivityFinal.this);
                return;
            }
            createUser(name);
        }
    }
    private void login(long uid) {

        HttpUserService userService = new HttpUserService();
        Call<User> call = userService.login(uid);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.body() == null){
                    System.out.println("lets take it from the top");
                    //This error is typically when bad residual data is left in our user prefs.
                    Globals.getPreferences().edit().clear().apply();
                    Globals.showDialog("Player Not Found", "There were problems logging in, we will need to " +
                            "create a new character for you.", LoginActivityFinal.this);
                    Intent loginIntent = new Intent(context, LoginActivityFinal.class);
                    startActivity(loginIntent);
                }

                User.save(response.body());
                /* +-+-+- If we have not yet registered for cloud messaging, do so -+-+-)*/
                if(!preferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false))
                    registerGCM(response);
                Intent mapIntent = new Intent(ApplicationContextProvider.getAppContext(), MainMapActivity.class);
                startActivity(mapIntent);

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Globals.showConnectionDialog(LoginActivityFinal.this);

            }
        });
    }
    private void createUser(String userName){
        HttpUserService userService = new HttpUserService();

        //Lets make sure that we clear the preferences
        Call<User> call = userService.createUser(userName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                User.save(response.body());

                registerGCM(response);
                Intent mapIntent = new Intent(ApplicationContextProvider.getAppContext(), MainMapActivity.class);
                startActivity(mapIntent);
            //TODO: get a location service call in here to try to get a jump  on that.

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                Globals.showConnectionDialog(LoginActivityFinal.this);


            }
        });
    }
    private User registerGCM(Response<User> response){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.user_shared_preferences), MODE_PRIVATE);
        User user = null;

        if(response != null){
            user = response.body();
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
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mNameView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivityFinal.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mNameView.setAdapter(adapter);
    }

    public void loginClicked(View view) {

    }

    public void cleanseData(View view) {
        Globals.getPreferences().edit().clear().apply();

        Intent loginIntent = new Intent(this, LoginActivityFinal.class);
        Globals.showDialog("<G>[U]<R>[U]:$Data Cleansed", "Sure, I'll keep your shameful secret. \n EOF",LoginActivityFinal.this);
        startActivity(loginIntent);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mName = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mName)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}

