package com.fourninenine.zombiegameclient;

import com.fourninenine.zombiegameclient.httpServices.RESTServices.HttpUserService;
import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.models.utilities.Globals;
import com.fourninenine.zombiegameclient.services.MyInstanceIDService;
import com.fourninenine.zombiegameclient.services.QuickstartPreferences;
import com.fourninenine.zombiegameclient.services.RegistrationIntentService;
import com.fourninenine.zombiegameclient.services.activityHelpers.GCMHelper;
import com.google.android.gms.common.GoogleApiAvailability;

/*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.orm.SugarRecord;

import java.util.List;

import retrofit2.Call;

public class GCMTestActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcmtest);

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    public void registerGcm(View view) {
        //set the curent user
        User user;
        List<User> users = null;
        // Check to see if a user is set
        if (Globals.getCurrentUser() == null) {
            //grab a list of users
            users = SugarRecord.find(User.class, "key = ?", "1");
            if (users != null && users.size() < 1) {
                user = new User("Bill Nullington", 0);
                user.save();

            } else user = users.remove(0);
        }else user = Globals.getCurrentUser();

        GetGCM();

        Intent apiIntent = new Intent(this, RegistrationIntentService.class);
        startService(apiIntent);

        //if the list is empty, create a new user

        /*if(Globals.getUserToken() == null) {
            MyInstanceIDService idService = new MyInstanceIDService();
            Globals.setUserToken(idService.generateToken());
            HttpUserService userService = new HttpUserService();

            Call<User> call = userService.registerWithGcm(user.getId(), );
            RegistrationIntentService regService = new RegistrationIntentService();
            System.out.println("Before registering");
            regService.sendRegistrationToServer(Globals.getUserToken());
            System.out.println("After registration");
        }*/
    }
//**To avoid java.io.IOException: MAIN_THREAD**

    private void GetGCM() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GCMHelper gcmRegistrationHelper = new GCMHelper (
                            getApplicationContext());
                    String gcmRegID = gcmRegistrationHelper.GCMRegister(R.string.gcm_sender_id + "");
                    System.out.println("Here is another line to have");
                } catch (Exception bug) {
                    bug.printStackTrace();
                }

            }
        });

        thread.start();
    }


}
