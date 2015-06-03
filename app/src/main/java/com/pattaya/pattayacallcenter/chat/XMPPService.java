package com.pattaya.pattayacallcenter.chat;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;

import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom.ChatRoom;
import com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom.ChatRooms;
import com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom.Member;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;

import retrofit.RestAdapter;

/**
 * Created by SWF on 2/24/2015.
 */
public class XMPPService extends IntentService {

    static XMPPManage xmppManage = XMPPManage.getInstance();
    private static XMPPService instance = null;
    final RestAdapter restAdapterOpenFire = RestAdapterOpenFire.getInstance();
    final OpenfireQueary openfireQueary = restAdapterOpenFire.create(OpenfireQueary.class);
    // ThreadXMPP td = new ThreadXMPP();
    SharedPreferences sp = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
    String jid = sp.getString(MasterData.SHARED_USER_JID, null);

    public XMPPService() {
        super("XMPPSerVICE");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int time = 0;
        try {
            while (true) {
                if (xmppManage.getmConnection() != null) {
                    if (!xmppManage.getmConnection().isConnected()) {
                        try {
                            SharedPreferences sp = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
                            jid = sp.getString(MasterData.SHARED_USER_JID, null);

                            Log.e("XMPPSerVICE", jid + "  login wait 6 sec .. .. ... .. ." + xmppManage.getmConnection().isConnected());
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
                        Log.e("XMPPSerVICE", "Break .. .. ... .. ." + jid);
                        //xmppManage.createPubSub();
                        ChatRooms chatRooms = openfireQueary.getChatRoomInThread();
                        Log.e("XMPPSerVICE", "ChatRoom .. .. ... .. ." + chatRooms);
                        if (chatRooms != null) {
                            if (chatRooms.getChatRoom() != null) {
                                for (ChatRoom e : chatRooms.getChatRoom()) {
                                    final String room = e.getRoomName() + "@conference.pattaya-data";

                                    if (e.getMembers().getListMember() != null) {

                                        for (Member member : e.getMembers().getListMember()) {
                                            if (member.getText().matches(jid)) {
                                                final Presence presence = new Presence(Presence.Type.available);
                                                presence.setTo(room + "/" + xmppManage.getmConnection().getUser());
                                                //System.out.println("" + presence.toXML());
                                                if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                                                    // Current Thread is Main Thread.
                                                    System.out.println("ON MAIN THREAD >>>>>>>>>>>>>>>>>>>>>" + presence.toXML());
                                                }
                                                xmppManage.getmConnection().sendPacket(presence);

                                            }

                                        }

                                    }


                                }


                            }


                        }
                        stopSelf();
                        Log.e("XMPPService", "break");
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
        //stopSelf();
    }

//    public static boolean isInstanceCreated() {
//        return instance != null;
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.e("XMPPSerVICE", "Service is started Thread >>" + td.isAlive());
//        if (!td.isAlive()) {
//            try {
//                td.start();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            Log.e("Service", "THREAD IS ALIVE");
//        }
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        instance = this;
//
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        // Store our shared preference
//        instance = null;
//    }
//
//
//    private class ThreadXMPP extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            int time = 0;
//            try {
//                while (true) {
//                    if (xmppManage.getmConnection() != null) {
//                        if (!xmppManage.getmConnection().isConnected()) {
//                            try {
//                                SharedPreferences sp = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
//                                String jid = sp.getString(MasterData.SHARED_USER_JID, null);
//
//                                Log.e("XMPPSerVICE", jid + "  login wait 6 sec .. .. ... .. ." + xmppManage.getmConnection().isConnected());
//                                xmppManage.login();
//                                Presence p = new Presence(Presence.Type.available, "I am busy", 1, Presence.Mode.available);
//                                xmppManage.getmConnection().sendPacket(p);
//                            } catch (SmackException.NotConnectedException e) {
//                                time = (time++ >= 6) ? 6 : time;
//                                Thread.sleep(time * 1000);
//                                e.printStackTrace();
//                                Log.d("XMPPSerVICE", "error " + e);
//                            }
//
//                        } else {
//                            Log.e("XMPPSerVICE", "Break .. .. ... .. ." + xmppManage.getmConnection().isConnected());
//                            xmppManage.createPubSub();
//                            /*
//                            List<Users> arrUsers = DatabaseChatHelper.init().getUsers();
//                            for (Users e : arrUsers) {
//                                if (e.getType() == Users.TYPE_GROUP) {
//                                    System.out.println("join room >> " + e.getJid());
//                                    xmppManage.setJoinRoom(e.getJid());
//                                }
//
//                            }*/
////                            new AsyncTask<Void,Void,Boolean>(){
////
////                                @Override
////                                protected Boolean doInBackground(Void... params) {
////                                    ChatRooms chatRooms = openfireQueary.getChatRoomInThread();
////                                    if (chatRooms != null) {
////                                        if (chatRooms.getChatRoom() != null) {
////                                            for (ChatRoom e : chatRooms.getChatRoom()) {
////                                                final String room = e.getRoomName() + "@conference.pattaya-data";
////
////                                                if (e.getMembers().getListMember() != null) {
////
////                                                    for (Member member : e.getMembers().getListMember()) {
////                                                        if (member.getText().matches(jid)) {
////                                                            System.out.println("join room >> " + room);
////                                                           // xmppManage.setJoinRoom(room);
////                                                        }
////
////                                                    }
////
////                                                }
////
////
////                                            }
////
////
////                                        }
////
////
////                                    }
////                                    return null;
////                                }
////                            }.execute();
//
//
//                            stopSelf();
//                            Log.e("XMPPService", "break");
//                            break;
//                        }
//                    } else {
//                        Log.e("XMPPService", "null");
//
//                        xmppManage.initConnection();
//                        try {
//                            Thread.sleep(3000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                            Log.e("XMPPSerVICE", "error " + e);
//                        }
//
//                    }
//                }
//            } catch (Exception e) {
//                e.getMessage();
//            }
//        }
//    }


}
