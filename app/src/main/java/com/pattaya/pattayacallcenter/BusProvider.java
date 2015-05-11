package com.pattaya.pattayacallcenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.otto.Bus;


/**
 * Created by SWF on 2/13/2015.
 */
public final class BusProvider extends  Bus {
    static BusProvider busProvider = null;

    public static BusProvider getInstance(){
        if(busProvider == null){
            Log.e("Bus", "Create Bus >> :)");
            busProvider = new BusProvider ();
            //bus = new Bus();
        }
        return busProvider;
    };


    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    BusProvider.super.post(event);
                }
            });
        }
    }


}
