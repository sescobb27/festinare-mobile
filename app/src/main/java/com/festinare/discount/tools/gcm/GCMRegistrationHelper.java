package com.festinare.discount.tools.gcm;

import com.festinare.discount.R;
import com.festinare.discount.models.User;
import com.festinare.discount.tools.ConnectionDetector;
import com.festinare.discount.tools.http.UserHelper;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.festinare.discount.models.Mobile;
import com.festinare.discount.tools.SessionHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;

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

    public void registerGCM() {
        gcm = GoogleCloudMessaging.getInstance(context);

        try {
            gcmId = getRegistrationId();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            gcmId = "";
        }

        if (gcmId.isEmpty()) {
            updateGCMKey = true;
            ConnectionDetector connDetector = ConnectionDetector.getConnectionDetector(context.getApplicationContext());
            if (connDetector.isConnectedToInternet()) {
                registerInBackground();
            }else{
                showMessage(R.string.error_gcm_registration_title,R.string.error_gcm_registration_msg_connection);
            }
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
                    ex.printStackTrace();
                    showMessage(R.string.error_gcm_registration_title, R.string.error_gcm_registration_msg);
                }
                return gcmId;
            }

            @Override
            protected void onPostExecute(String gcmId) {
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
                userHelper.mobile(context, user, mobile, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // Persist the regID - no need to register again.
                        try {
                            mSessionHelper.setGCMKey(gcmId);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            showMessage(R.string.error_gcm_registration_title, R.string.error_gcm_registration_msg);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e(TAG, "", error);
                        showMessage(R.string.error_gcm_registration_title, R.string.error_gcm_registration_msg);
                    }

                });
            } catch (JSONException | UnsupportedEncodingException e) {
                showMessage(R.string.error_gcm_registration_title,R.string.error_gcm_registration_msg);
                e.printStackTrace();
            }
        }
    }

    /**Show simple messages to the user*/
    public void showMessage(int title, int msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ConnectionDetector connDetector = ConnectionDetector.getConnectionDetector(context.getApplicationContext());
                if (connDetector.isConnectedToInternet()) {
                    registerInBackground();
                }else{
                    showMessage(R.string.error_gcm_registration_title,R.string.error_gcm_registration_msg_connection);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ((AppCompatActivity)context).finish();
            }
        });
        builder.show();
    }

}
