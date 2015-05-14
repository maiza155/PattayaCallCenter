package com.pattaya.pattayacallcenter.chat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseListMemberData;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseListMemberObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.GetCaseListData;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by SWF on 2/24/2015.
 */
public class XMPPServiceOfficial extends Service {

    static XMPPManageOfficial xmppManage = XMPPManageOfficial.getInstance();
    private static XMPPServiceOfficial instance = null;
    ThreadXMPP td = new ThreadXMPP();
    RestAdapter webserviceConnector = WebserviceConnector.getInstanceCase();
    RestFulQueary adapterRest = webserviceConnector.create(RestFulQueary.class);
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //showAppNotification();
        }
    };

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("XMPPSerVICE", "Service is started Thread >>" + td.isAlive());
        if (!td.isAlive()) {
            try {
                td.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.e("Service", "THREAD IS ALIVE");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Store our shared preference
        instance = null;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private class ThreadXMPP extends Thread {
        @Override
        public void run() {
            super.run();
            int time = 0;
            try {
                while (true) {
                    if (xmppManage.getmConnection() != null) {
                        if (!xmppManage.getmConnection().isConnected()) {
                            try {
                                xmppManage.login();
                                Presence p = new Presence(Presence.Type.available, "I am busy", 1, Presence.Mode.available);
                                xmppManage.getmConnection().sendPacket(p);
                            } catch (SmackException.NotConnectedException e) {
                                time = (time++ >= 6) ? 6 : time;
                                Thread.sleep(time * 1000);
                                e.printStackTrace();
                                Log.d("XMPPSerVICE", "error " + e);
                            }

                        } else {
                            Log.e("XMPPSerVICE", "Break .. .. ... .. ." + xmppManage.getmConnection().isConnected());
                            xmppManage.createPubSub();
                            SharedPreferences spConfig = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
                            String token = spConfig.getString("TOKEN", "null");
                            String clientId = spConfig.getString("CLIENT_ID", "null");

                            GetCaseListData getCaseListData = new GetCaseListData();
                            getCaseListData.setFilterType(3);

                            getCaseListData.setAccessToken(token);
                            getCaseListData.setClientId(clientId);

                            adapterRest.getCaseList(getCaseListData, new Callback<Response>() {
                                @Override
                                public void success(Response result, Response response2) {
                                    DatabaseChatHelper.init().clearCaseTable();
                                    BufferedReader reader;
                                    StringBuilder sb = new StringBuilder();
                                    try {
                                        reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                                        String line;

                                        try {
                                            while ((line = reader.readLine()) != null) {
                                                sb.append(line);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    String JsonConvertData = "{data:" + sb.toString() + "}";
                                    System.out.println(JsonConvertData);
                                    CaseListMemberObject caseListObject = new Gson().fromJson(JsonConvertData, CaseListMemberObject.class);
                                    for (CaseListMemberData e : caseListObject.getData()) {
                                        String roomName = "case-" + e.getComplaintId() + "@" + "conference.pattaya-data";
                                        xmppManage.setJoinRoom(roomName);
                                    }


                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    System.out.println("error = [" + error + "]");

                                }
                            });


                            stopSelf();
                            break;
                        }
                    } else {
                        Log.e("XMPPService", "null");

                        xmppManage.initConnection();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.e("XMPPSerVICE", "error " + e);
                        }

                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }


}
