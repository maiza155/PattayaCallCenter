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
import android.widget.RemoteViews;

import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.guest.CaseChatActivity;
import com.pattaya.pattayacallcenter.guest.CaseDetailActivity;
import com.pattaya.pattayacallcenter.member.CaseChatMemberActivity;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseMainObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.GetComplainObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by SWF on 4/1/2015.
 */
public class NotifyCase {
    public static int NOTIFY_CHAT_ID = 311;
    public static NotificationManager mNotifyMgr = (NotificationManager) Application.getContext()
            .getSystemService(Application.getContext().NOTIFICATION_SERVICE);
    static String clientId;
    static String token;
    static RestAdapter webserviceConnector = WebserviceConnector.getInstanceCase();
    static RestFulQueary adapterRest = webserviceConnector.create(RestFulQueary.class);
    static Boolean alertSound;

    public static void setNotifyChat(PubsubObject object) {
        SharedPreferences spConfig = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        Boolean alertSound = spConfig.getBoolean(MasterData.SHARED_CONFIG_ALERT_SOUND, true);
        Boolean alert = spConfig.getBoolean(MasterData.SHARED_CONFIG_ALERT, true);

        SharedPreferences sp = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        Boolean isOfficial = sp.getBoolean(MasterData.SHARED_IS_OFFICIAL,false);
        System.out.println(object.getDisplayData());

        Long millis = Long.parseLong(object.getDisplayData(), 10);
        SimpleDateFormat mysdf = new SimpleDateFormat("d MMM yyyy HH:mm ");
        Date cal = new Date(millis);
        RemoteViews contentView = new RemoteViews(Application.getContext().getPackageName(),
                R.layout.notification_layout);

        contentView.setTextViewText(R.id.header, object.getAction());
        contentView.setTextViewText(R.id.title,"ชื่อเรื่อง:"+ Application.getContext().getResources().getString(R.string.tab) + object.getTitle());
        contentView.setTextViewText(R.id.name, "ชื่อผู้เเจ้ง:"+ Application.getContext().getResources().getString(R.string.tab) + object.getName());
        contentView.setTextViewText(R.id.date, "วันที:"+ Application.getContext().getResources().getString(R.string.tab) + mysdf.format(cal.getTime()));

        String content = "ชื่อเรื่อง :" + object.getTitle() + "\n" + "ชื่อผู้เเจ้ง :" + object.getName() + "\n";


        Log.e("Title",">>>>"+object.getName());

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(Application.getContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(object.getAction())
                        .setContentText("ชื่อเรื่อง :" + object.getTitle())
                                // Set Ticker Message
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                        .setAutoCancel(true);


        mBuilder.setLights(Color.BLUE, 500, 500);
        long[] pattern = {500, 500};
        mBuilder.setVibrate(pattern);
        if (alertSound) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
        }
        if (object.getCaseId() > 0) {
            Intent notificationIntent = new Intent(Application.getContext(), CaseChatMemberActivity.class);
            notificationIntent.putExtra("id", object.getCaseId());
            notificationIntent.putExtra("casename", object.getTitle());
            notificationIntent.putExtra("complainid", object.getComplainId());
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            PendingIntent intent = PendingIntent.getActivity(Application.getContext(), 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(intent);
        } else {
            Intent notificationIntent = new Intent(Application.getContext(), CaseDetailActivity.class);
            notificationIntent.putExtra("id_case", object.getCaseId());
            notificationIntent.putExtra("id", object.getComplainId());
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            PendingIntent intent = PendingIntent.getActivity(Application.getContext(), 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(intent);
        }

        SharedPreferences settings = Application.getContext().getSharedPreferences(MasterData.SHARED_CASE_COUNT, Context.MODE_PRIVATE);
        String caseStr = "id_" + object.getComplainId();
        int count = settings.getInt(caseStr, 0);
        Log.e("count ", "" + count);
        count = count + 1;
        settings.edit().putInt(caseStr, count).commit();
        BusProvider.getInstance().post("update_case_list");

        String jidRoom = "case-"+object.getComplainId()+"@conference.pattaya-data";
        Log.e("Notify - case",""+jidRoom);

        if(isOfficial){
            XMPPManageOfficial.getInstance().setJoinRoom(jidRoom);

        }else{
            XMPPManage.getInstance().setJoinRoom(jidRoom);
        }
        NotificationCompat.BigPictureStyle notiStyle = new
                NotificationCompat.BigPictureStyle();

        Notification notification = mBuilder.build();
        //  notification.contentView = contentView;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification.bigContentView = contentView;

        }

        //BusProvider.getInstance().post("update_case_list");
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

    public static void setNotifyChatCase(String name, String complainId, String msg) {
        SharedPreferences spConfig = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        alertSound = spConfig.getBoolean(MasterData.SHARED_CONFIG_ALERT_SOUND, true);
        token = spConfig.getString(MasterData.SHARED_CONFIG_TOKEN, "Null");
        clientId = spConfig.getString(MasterData.SHARED_CONFIG_CLIENT_ID, "Null");
        Log.e("TAG Notification ", "" + complainId);
        new TaskQueryCaseId(Integer.parseInt(complainId), msg, name).execute();
        SharedPreferences settings = Application.getContext().getSharedPreferences(MasterData.SHARED_CASE_COUNT, Context.MODE_PRIVATE);
        String caseStr = "id_" + complainId;
        int count = settings.getInt(caseStr, 0);
        Log.e("count ", "" + count);
        count = count + 1;
        settings.edit().putInt(caseStr, count).commit();
        Log.e("count ", "" + count);
        BusProvider.getInstance().post("case_count");


    }
    public static void cancelNotification(int notifyId) {
        mNotifyMgr.cancel(notifyId);
    }

    static class TaskQueryCaseId extends AsyncTask<Void, Void, Boolean> {
        int complainId;
        String name;
        String msg;

        public TaskQueryCaseId(int complainId, String msg, String name) {
            this.complainId = complainId;
            this.msg = msg;
            this.name = name;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            GetComplainObject getComplainObject = new GetComplainObject();
            getComplainObject.setAccessToken(token);
            getComplainObject.setClientId(clientId);
            getComplainObject.setPrimaryKeyId(complainId);
            adapterRest.getComplaintData(getComplainObject, new Callback<CaseMainObject>() {
                @Override
                public void success(CaseMainObject caseMainObject, Response response) {
                    RemoteViews contentView = new RemoteViews(Application.getContext().getPackageName(),
                            R.layout.notification_layout);
                    Log.e("TAG Notification CaseID", "" + caseMainObject.getRefCasesId());
                    String[] sticker = msg.split("<s>");
                    String[] image = msg.split("<img>");
                    String messages ;
                    if (sticker.length > 1) {
                        messages = "ส่งสติ๊กเกอร์";
                    } else if (image.length > 1) {
                        messages = "Image file";
                    } else {
                        messages = msg;
                    }


                    contentView.setTextViewText(R.id.header, "สนทนาเคส");
                    contentView.setTextViewText(R.id.title, "ชื่อเรื่อง:" + Application.getContext().getResources().getString(R.string.tab) + caseMainObject.getComplaintName());
                    contentView.setTextViewText(R.id.name, "ชื่อผู้เเจ้ง:" + Application.getContext().getResources().getString(R.string.tab) + name);
                    contentView.setTextViewText(R.id.date, "ข้อความ:" + Application.getContext().getResources().getString(R.string.tab) + messages);

                    String content = "ชื่อเรื่อง :" + caseMainObject.getComplaintName() + "\n" + "ชื่อผู้เเจ้ง :" + name + "\n";


                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(Application.getContext())
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setContentTitle("สนทนาเคส")
                                    .setContentText("ชื่อเรื่อง :" + caseMainObject.getComplaintName())
                                            // Set Ticker Message
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                                    .setAutoCancel(true);


                    mBuilder.setLights(Color.BLUE, 500, 500);
                    long[] pattern = {500, 500};
                    mBuilder.setVibrate(pattern);
                    if (alertSound) {
                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        mBuilder.setSound(alarmSound);
                    }
                    if (caseMainObject.getRefCasesId() > 0) {

                        Intent notificationIntent = new Intent(Application.getContext(), CaseChatMemberActivity.class);
                        notificationIntent.putExtra("id", caseMainObject.getRefCasesId());
                        notificationIntent.putExtra("casename", caseMainObject.getComplaintName());
                        notificationIntent.putExtra("complainid", complainId);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);


                        PendingIntent intent = PendingIntent.getActivity(Application.getContext(), 0,
                                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(intent);
                    } else {
                        Intent notificationIntent = new Intent(Application.getContext(), CaseChatActivity.class);
                        notificationIntent.putExtra("id", caseMainObject.getRefCasesId());
                        notificationIntent.putExtra("casename", caseMainObject.getComplaintName());
                        notificationIntent.putExtra("complainid", complainId);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);


                        PendingIntent intent = PendingIntent.getActivity(Application.getContext(), 0,
                                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(intent);
                    }


                    NotificationCompat.BigPictureStyle notiStyle = new
                            NotificationCompat.BigPictureStyle();

                    Notification notification = mBuilder.build();
                    //  notification.contentView = contentView;

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        notification.bigContentView = contentView;

                    }


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

                @Override
                public void failure(RetrofitError error) {

                }
            });

            return null;
        }
    }

}
