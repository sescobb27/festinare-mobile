package com.festinare.discount;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.festinare.discount.tools.SessionHelper;
import com.festinare.discount.ui.LoginActivity;
import com.festinare.discount.ui.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.Timer;
import java.util.TimerTask;


public class IndexActivity extends AppCompatActivity
{

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        if (checkPlayServices()) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {

                    SessionHelper sessionHelper = new SessionHelper(getApplicationContext());
                    String token = sessionHelper.getAPIToken();
                    if (!token.isEmpty()) {
                        Intent intent = new Intent(IndexActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent loginIntent = new Intent(IndexActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }
                }
            };

            Timer timerOnTask = new Timer();
            timerOnTask.schedule(task, 500);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e("FestinareDiscount", "There is not Google Play Services, This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
