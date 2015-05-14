package com.pattaya.pattayacallcenter.webservice;

import retrofit.RestAdapter;

/**
 * Created by SWF on 3/11/2015.
 */
public class WebserviceConnector {

    static RestAdapter restAdapter = null;
    static RestAdapter restAdapterUpload = null;
    static RestAdapter restAdapterPost = null;
    static RestAdapter restAdapterCase = null;
    static RestAdapter restAdapterPersonal = null;
    static String URL = "http://58.181.163.115:8080/";
    //static String URL = "http://172.16.1.127:8080/";

    public static RestAdapter getInstance() {
        if (restAdapter == null) {
            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(URL + "AuthenService/services")
                    .build();
        }
        return restAdapter;
    }

    public static RestAdapter getInstanceUpload() {
        if (restAdapterUpload == null) {
            restAdapterUpload = new RestAdapter.Builder()
                    .setEndpoint(URL + "UploadService/rest/file")
                    .build();
        }
        return restAdapterUpload;
    }

    public static RestAdapter getInstanceCartdUI() {
        if (restAdapterPost == null) {
            restAdapterPost = new RestAdapter.Builder()
                    .setEndpoint(URL + "CardUIService/services")
                    .build();
        }
        return restAdapterPost;
    }


    public static RestAdapter getInstanceCase() {
        if (restAdapterCase == null) {
            restAdapterCase = new RestAdapter.Builder()
                    .setEndpoint(URL + "CaseService/services")
                    .build();
        }
        return restAdapterCase;
    }

    public static RestAdapter getInstancePersonal() {
        if (restAdapterPersonal == null) {
            restAdapterPersonal = new RestAdapter.Builder()
                    .setEndpoint(URL + "PersonalService/services")
                    .build();
        }
        return restAdapterPersonal;
    }

}
