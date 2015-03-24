package discount.up.hurry.hurryupdiscount.Services.ConnectionDetectorService;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetectorService {

    private static Context context;
    private static ConnectionDetectorService internetDetector;

    private ConnectionDetectorService(Context newContext) {
        context = newContext;
    }

    public static ConnectionDetectorService getConnectionDetector(Context newContext){
        if(internetDetector == null)
            internetDetector = new ConnectionDetectorService(newContext);
        else
            context = newContext;
        return internetDetector;
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; ++i) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }

        }
        return false;
    }
}
