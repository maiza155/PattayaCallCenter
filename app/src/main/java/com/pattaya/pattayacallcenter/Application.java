package com.pattaya.pattayacallcenter;


import android.content.Context;

import com.pattaya.pattayacallcenter.customview.FontOverride;

/**
 * Created by SWF on 2/11/2015.
 */
public final class Application extends android.app.Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        FontOverride.setDefaultFont(this, "MONOSPACE", "fonts/THSarabun.ttf");


    }

    public static Context getContext() {
        return mContext;
    }


}