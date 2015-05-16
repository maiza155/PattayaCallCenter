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
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.member.ChatActivity;

/**
 * Created by SWF on 4/1/2015.
 */
public class NotifyChat {
    public static int NOTIFY_CHAT_ID = 309;
    public static NotificationManager mNotifyMgr = (NotificationManager) Application.getContext()
            .getSystemService(Application.getContext().NOTIFICATION_SERVICE);


    public static void setNotifyChat(String str, String jid, String msg) {
        SharedPreferences spConfig = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        Boolean alertSound = spConfig.getBoolean(MasterData.SHARED_CONFIG_ALERT_SOUND, true);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(Application.getContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(str)
                        .setContentText(msg)
                        .setAutoCancel(true);
        mBuilder.setLights(Color.BLUE, 500, 500);
        long[] pattern = {500, 500};
        mBuilder.setVibrate(pattern);
        if (alertSound) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
        }
        Log.e("TAG Notification ", "" + jid);
        Intent notificationIntent = new Intent(Application.getContext(), ChatActivity.class);
        notificationIntent.putExtra("jid", jid);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        PendingIntent intent = PendingIntent.getActivity(Application.getContext(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent);


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


    public static void cancelNotification(int notifyId) {
        mNotifyMgr.cancel(notifyId);
    }

}
