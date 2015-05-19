package com.pattaya.pattayacallcenter.chat;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;

import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.sticker.StickerListObject;
import com.pattaya.pattayacallcenter.webservice.object.sticker.StickerObject;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class StrickLoaderService extends Service {
    RestAdapter webserviceConnector = WebserviceConnector.getInstanceCase();
    RestFulQueary adapterRest = null;
    DatabaseChatHelper databaseChatHelper = DatabaseChatHelper.init();
    private ArrayList<String> targetList;
    private int arraySize;
    private int duo;

    public StrickLoaderService() {

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Create Service Sticker");
        //android.os.Debug.waitForDebugger();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    byte[] BitmapEncodeByte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        //String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return byteArrayImage;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                System.out.println("starting service stricker");
                targetList = new ArrayList();
                //Log.e("Service Stricker", "Start Service");
                adapterRest = webserviceConnector.create(RestFulQueary.class);
                adapterRest.getStricket(new JSONObject(), new Callback<StickerListObject>() {
                    @Override
                    public void success(final StickerListObject stickerListObject, Response response) {
                        arraySize = stickerListObject.getStickerSettingList().size();
                        if (arraySize > 0) {
                            duo = stickerListObject.getStickerSettingList().size();
                            for (final StickerObject e : stickerListObject.getStickerSettingList()) {
                                //System.out.println("stickerListObject = [" + e.toString() + "], response = [" + response + "]");
                                new TaskLoadBitmap(e.getStickerImage(), e.getStickerSettingId()).execute();
                            }
                        } else {
                            stopSelf();
                            BusProvider.getInstance().post("sticker_fin_no_data");
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println("error = [" + error + "]");
                        stopSelf();
                        BusProvider.getInstance().post("sticker_fin_no_data");

                    }
                });

            }
        }.start();


        return (getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.ECLAIR) ? START_STICKY_COMPATIBILITY : START_STICKY;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = 0;
        int height = 0;
        float scaleWidth;
        float scaleHeight;
        Bitmap resizedBitmap = null;
        if (bm != null) {
            width = bm.getWidth();
            height = bm.getHeight();
            scaleWidth = ((float) newWidth) / width;
            scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        }
        // "RECREATE" THE NEW BITMAP
        return resizedBitmap;
    }

    class TaskLoadBitmap extends AsyncTask<Void, Void, Bitmap> {
        String url;
        int id;

        TaskLoadBitmap(String url, int id) {
            this.url = url;
            this.id = id;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = getBitmapFromURL(url);
            Bitmap bitmapTemp = getResizedBitmap(bitmap, 300, 300);
            if (bitmapTemp != null) {
                byte[] image = BitmapEncodeByte(bitmapTemp);
                databaseChatHelper.addStricker(String.valueOf(id), image);
                BusProvider.getInstance().post("sticker_update");
                targetList.add("" + id);
                if (targetList.size() == duo) {
                    stopSelf();
                    BusProvider.getInstance().post("sticker_fin");
                }
            } else {
                BusProvider.getInstance().post("sticker_fin");
            }


            return null;
        }
    }


}
