package com.festinare.discount.tools.gcm;

import com.festinare.discount.models.User;
import com.festinare.discount.tools.http.UserHelper;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.festinare.discount.models.Mobile;
import com.festinare.discount.tools.SessionHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

public class GCMRegistrationHelper {

    private GoogleCloudMessaging gcm;
    private Context context;
    private String gcmId;
    private SessionHelper mSessionHelper;
    private boolean updateGCMKey = false;

    private final String SENDER_ID = "516765447023";
    private final String TAG = "GCM::HELPER";

    private User user;

    public GCMRegistrationHelper(Context context, User user) {
        this.context = context;
        this.user=user;
        this.mSessionHelper = new SessionHelper(context);
    }

    public void getGcmRegistrationIdOrRegister() {
        gcm = GoogleCloudMessaging.getInstance(context);

        try {
            gcmId = getRegistrationId();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            gcmId = "";
        }

        if (gcmId.isEmpty()) {
            updateGCMKey = true;
            registerInBackground();
        }
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId() throws PackageManager.NameNotFoundException {
//        String registrationId = mSessionHelper.getGCMKey();
//        if (registrationId.isEmpty()) {
//            Log.i(TAG, "Registration not found.");
//            return "";
//        }
//        // Check if app was updated; if so, it must clear the registration ID
//        // since the existing registration ID is not guaranteed to work with
//        // the new app version.
//        int registeredVersion = mSessionHelper.getRegisteredAppVersion();
//        int currentVersion = mSessionHelper.getAppVersion();
//        if (registeredVersion != currentVersion) {
//            Log.i(TAG, "App version changed.");
//            return "";
//        }
//        return registrationId;



        String registrationId = mSessionHelper.getGCMKey();

        if (registrationId.length() == 0){
            Log.d(TAG, "Registro GCM no encontrado.");
            return "";
        }

        int registeredVersion = mSessionHelper.getRegisteredAppVersion();

        long expirationTime = mSessionHelper.getExpirationTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String expirationDate = sdf.format(new Date(expirationTime));

        Log.d(TAG, "Registro GCM encontrado (version=" + registeredVersion +
                ", expira=" + expirationDate + ")");

        int currentVersion = mSessionHelper.getAppVersion();

        if (registeredVersion != currentVersion)
        {
            Log.d(TAG, "New app version available");
            return "";
        }
        else if (System.currentTimeMillis() > expirationTime)
        {
            Log.d(TAG, "GCM has expired");
            return "";
        }

        return registrationId;

    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    gcmId = gcm.register(SENDER_ID);

                } catch (IOException ex) {
                    // TODO
                    ex.printStackTrace();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return gcmId;
            }

            @Override
            protected void onPostExecute(String gcmId) {
                    Log.i(TAG, "Saving regId on app version " + mSessionHelper.getRegisteredAppVersion());
                    Mobile mobile = new Mobile();
                    mobile.setToken(gcmId);
                    onGCMRegister(mobile);
            }
        }.execute(null, null, null);
    }

    public void onGCMRegister(Mobile mobile) {
        if (updateGCMKey) {
            UserHelper userHelper = new UserHelper();
            try {
                userHelper.mobile(context, user, mobile, new JsonHttpResponseHandler(
                        HTTP.UTF_8) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        // Persist the regID - no need to register again.
                        try {
                            mSessionHelper.setGCMKey(gcmId);
                        } catch (PackageManager.NameNotFoundException e) {
                            //TODO
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                        // TODO
                        Log.e(AsyncHttpClient.LOG_TAG, error.getMessage());
                    }
                });
            } catch (JSONException e) {
                // TODO
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                // TODO
                e.printStackTrace();
            }
        }
    }

}
