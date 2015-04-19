package com.festinare.discount.services.sessionService;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class SessionService {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String API_TOKEN = "API::TOKEN";
    private static final String GCM_KEY = "GCM::GCM_KEY";
    private static final String APP_NAME = "FestinareDiscount";
    private static final String APP_VERSION = "FestinareDiscount::Version";

    public SessionService(Context context) {
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

    public int getRegisteredAppVersion () {
        SharedPreferences prefs = getSharedFromContext();
        return prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public int getAppVersion() throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0);
        return packageInfo.versionCode;
    }

}
