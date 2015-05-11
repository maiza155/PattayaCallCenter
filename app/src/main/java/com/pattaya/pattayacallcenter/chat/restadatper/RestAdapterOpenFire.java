package com.pattaya.pattayacallcenter.chat.restadatper;

import com.mobprofs.retrofit.converters.SimpleXmlConverter;
import com.pattaya.pattayacallcenter.chat.XMPPManage;

import retrofit.RestAdapter;

/**
 * Created by SWF on 3/11/2015.
 */
public class RestAdapterOpenFire {

    static RestAdapter restAdapter = null;
    static RestAdapter restAdapterJson = null;
    static String PORT = "9090";

    public static RestAdapter getInstance() {
        if (restAdapter == null) {
            restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://" + XMPPManage.HOST + ":" + PORT + "/plugins/restapi/v1")
                    .setConverter(new SimpleXmlConverter())
                    .build();
        }
        return restAdapter;
    }

    public static RestAdapter getInstanceJson() {
        if (restAdapterJson == null) {
            restAdapterJson = new RestAdapter.Builder()
                    .setEndpoint("http://" + XMPPManage.HOST + ":" + PORT + "/plugins/restapi/v1")
                    .build();
        }
        return restAdapterJson;
    }
}
