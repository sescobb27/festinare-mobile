package com.festinare.discount.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.festinare.discount.R;

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
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }

        }
        Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_LONG).show();
        return false;
    }
}
