package discount.up.hurry.hurryupdiscount.Services.SessionService;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionService {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public static final String API_TOKEN = "API::TOKEN";
    public static final String GCM_KEY = "GCM::GCM_KEY";
    private static final String APP_NAME = "HurryUpDiscount";

    public SessionService(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedFromContext()
    {
        return context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
    }

    public void setGCMkey(String gcmId){
        sharedPreferences = getSharedFromContext();
        editor = sharedPreferences.edit();
        editor.putString(GCM_KEY, gcmId);
        editor.apply();
    }

    public void setAPIToken(String token){
        sharedPreferences = getSharedFromContext();
        editor = sharedPreferences.edit();
        editor.putString(API_TOKEN, token);
        editor.apply();
    }
}
