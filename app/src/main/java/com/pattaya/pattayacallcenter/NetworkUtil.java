package com.pattaya.pattayacallcenter;

/**
 * Created by SWF on 5/18/2015.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.pattaya.pattayacallcenter.Data.NetworkObject;

public class NetworkUtil {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static NetworkObject getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        NetworkObject networkObject = new NetworkObject();
        String status = null;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = "Wifi enabled";
            networkObject.setIsConect(true);
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            networkObject.setIsConect(true);
            status = "Mobile data enabled";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            networkObject.setIsConect(false);
            status = "Not connected to Internet";
        }
        networkObject.setText(status);
        return networkObject;
    }
}
