package com.pattaya.pattayacallcenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pattaya.pattayacallcenter.Data.NetworkObject;

/**
 * Created by SWF on 5/18/2015.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        NetworkObject status = NetworkUtil.getConnectivityStatusString(context);
        BusProvider.getInstance().post(status);


    }
}