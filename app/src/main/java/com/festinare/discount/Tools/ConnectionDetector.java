package com.festinare.discount.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {

    private static Context context;
    private static ConnectionDetector internetDetector;

    private ConnectionDetector(Context newContext) {
        context = newContext;
    }

    public static ConnectionDetector getConnectionDetector(Context newContext){
        if(internetDetector == null)
            internetDetector = new ConnectionDetector(newContext);
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
