package com.pattaya.pattayacallcenter.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom.ChatRoom;
import com.pattaya.pattayacallcenter.member.FriendRequestActivity;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.AccessUserObject;
import com.pattaya.pattayacallcenter.webservice.object.GetUserObject;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by SWF on 4/1/2015.
 */
public class NotifyInviteGroup {
    public static int NOTIFY_CHAT_ID = 301;
    public static NotificationManager mNotifyMgr = (NotificationManager) Application.getContext()
            .getSystemService(Application.getContext().NOTIFICATION_SERVICE);
    static NotifyInviteGroup notifyInviteGroup = null;
    final RestAdapter restAdapterOpenFire = RestAdapterOpenFire.getInstance();
    final OpenfireQueary openfireQueary = restAdapterOpenFire.create(OpenfireQueary.class);
    RestAdapter webserviceConnector = WebserviceConnector.getInstance();
    RestFulQueary adapterRest = webserviceConnector.create(RestFulQueary.class);
    SharedPreferences spConfig = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
    String token = spConfig.getString(MasterData.SHARED_CONFIG_TOKEN, null);
    String clientId = spConfig.getString(MasterData.SHARED_CONFIG_CLIENT_ID, null);

    public static NotifyInviteGroup getInstant() {
        if (notifyInviteGroup == null) {
            notifyInviteGroup = new NotifyInviteGroup();
        }
        return notifyInviteGroup;
    }

    public void init(String room, String jid) {
        new TaskQuearyData(jid, room).execute();
    }

    public void setNotifyInvite(String room, String name) {
        SharedPreferences spConfig = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        Boolean alertSound = spConfig.getBoolean(MasterData.SHARED_CONFIG_ALERT_SOUND, true);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(Application.getContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(room)
                        .setContentText(name)
                        .setAutoCancel(true);
        mBuilder.setLights(Color.BLUE, 500, 500);
        long[] pattern = {500, 500};
        mBuilder.setVibrate(pattern);
        if (alertSound) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
        }
        Log.e("TAG Notification ", "" + name);
        Intent notificationIntent = new Intent(Application.getContext(), FriendRequestActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        PendingIntent intent = PendingIntent.getActivity(Application.getContext(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent);

        BusProvider.getInstance().post("invited_update");

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        // Sets an ID for the notification
        int mNotificationId = NOTIFY_CHAT_ID;
        // Gets an instance of the NotificationManager service

        // Builds the notification and issues it.
        try {
            mNotifyMgr.notify(mNotificationId, notification);
        } catch (SecurityException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    class TaskQuearyData extends AsyncTask<Void, Void, Boolean> {
        String room;
        String jid;
        String roomName;
        String userName;
        Boolean[] isFin = {false, false};

        public TaskQuearyData(String jid, String room) {
            this.jid = jid;
            this.room = room;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            openfireQueary.getChatRoomDetail(room.split("@")[0], new Callback<ChatRoom>() {
                @Override
                public void success(final ChatRoom chatRoom, Response response) {
                    roomName = "Chat room : " + chatRoom.getNaturalName();
                    isFin[0] = true;
                    if (isFin[0] && isFin[1]) {
                        setNotifyInvite(roomName, userName);
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println("error = [" + error + "]");
                }
            });

            GetUserObject getUserObject = new GetUserObject(jid, token, clientId);
            adapterRest.getUser(getUserObject, new Callback<AccessUserObject>() {
                @Override
                public void success(AccessUserObject accessUserObject, Response response) {
                    userName = "invited from :" + accessUserObject.getDisplayName();
                    isFin[1] = true;
                    if (isFin[0] && isFin[1]) {
                        setNotifyInvite(roomName, userName);
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println("error = [" + error + "]");
                }
            });


            return null;
        }
    }


}
