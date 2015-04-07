package discount.up.hurry.hurryupdiscount.Services.GCMService;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import discount.up.hurry.hurryupdiscount.Models.Mobile;
import discount.up.hurry.hurryupdiscount.Services.SessionService.SessionService;

public class GCMRegistrationService {

    private GoogleCloudMessaging gcm;
    private Context context;
    private String gcmId;
    private SessionService mSessionService;
    private OnGCMRegister interestedInRegistration;
    private boolean updateGCMKey = false;

    private final String SENDER_ID = "";
    private final String TAG = "GCM::SERVICE";

    public GCMRegistrationService(Context context, OnGCMRegister interestedInRegistration) {
        this.context = context;
        this.mSessionService = new SessionService(context);
        this.interestedInRegistration = interestedInRegistration;
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

    public boolean needsGCMKeyUpdate () {
        return updateGCMKey;
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
        String registrationId = mSessionService.getGCMKey();
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = mSessionService.getRegisteredAppVersion();
        int currentVersion = mSessionService.getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
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
                    // Persist the regID - no need to register again.
                    mSessionService.setGCMKey(gcmId);
                    Log.i(TAG, "Saving regId on app version " + mSessionService.getRegisteredAppVersion());
                } catch (IOException ex) {
                    // TODO
                    ex.printStackTrace();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                } catch (PackageManager.NameNotFoundException ex) {
                    // TODO
                    ex.printStackTrace();
                }
                return gcmId;
            }

            @Override
            protected void onPostExecute(String gcmId) {
                Mobile mobile = new Mobile();
                mobile.setToken(gcmId);
                interestedInRegistration.onGCMRegister(mobile);
            }
        }.execute(null, null, null);
    }

}
