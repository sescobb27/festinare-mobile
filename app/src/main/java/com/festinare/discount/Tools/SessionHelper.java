package com.festinare.discount.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class SessionHelper {

    private static final long EXPIRATION_TIME_MS = 1000 * 3600 * 24 * 7 ;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String API_TOKEN = "API::TOKEN";
    private static final String GCM_KEY = "GCM::GCM_KEY";
    private static final String EXPIRATION_TIME = "GCM::ExpirationTime";
    private static final String APP_NAME = "FestinareDiscount";
    private static final String APP_VERSION = "FestinareDiscount::Version";


    public SessionHelper(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedFromContext()
    {
        return context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
    }

    public void setGCMKey(String gcmId) throws PackageManager.NameNotFoundException {
        sharedPreferences = getSharedFromContext();
        editor = sharedPreferences.edit();
        editor.putString(GCM_KEY, gcmId);
        editor.putInt(APP_VERSION, getAppVersion());
        editor.putLong(EXPIRATION_TIME, System.currentTimeMillis() + EXPIRATION_TIME_MS);
        editor.apply();
    }

    public String getGCMKey() {
        sharedPreferences = getSharedFromContext();
        return sharedPreferences.getString(GCM_KEY, "");
    }

    public String getAPIToken() {
        sharedPreferences = getSharedFromContext();
        return sharedPreferences.getString(API_TOKEN, "");
    }

    public void setAPIToken(String token) {
        sharedPreferences = getSharedFromContext();
        editor = sharedPreferences.edit();
        editor.putString(API_TOKEN, token);
        editor.apply();
    }

    public long getExpirationTime() {
        sharedPreferences = getSharedFromContext();
        return sharedPreferences.getLong(EXPIRATION_TIME, -1);
    }

    public int getRegisteredAppVersion() {
        sharedPreferences = getSharedFromContext();
        return sharedPreferences.getInt(APP_VERSION, Integer.MIN_VALUE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public int getAppVersion() throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0);
        return packageInfo.versionCode;
    }

}
