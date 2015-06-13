package com.pattaya.pattayacallcenter.customview;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.R;

import java.util.concurrent.ExecutionException;


public class FullscreenActivity extends Activity {
    CustomImageVIewZoomAble imgDisplay;
    String path;
    String pathUrl;
    int STATE_DOWNLOAD = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        Intent i = getIntent();
        path = i.getStringExtra("path");
        pathUrl = i.getStringExtra("pathUrl");


        Button btnClose = (Button) findViewById(R.id.BtnCancel);
        Button btnLoad = (Button) findViewById(R.id.BtnDownload);

        imgDisplay = (CustomImageVIewZoomAble) findViewById(R.id.imgDisplay);

        new TaskBitmap().execute();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(STATE_DOWNLOAD == 0 && pathUrl != null){
                   showDialog(v.getContext(),pathUrl);

               }else{
                   Toast.makeText(Application.getContext(), "This image is downloaded", Toast.LENGTH_SHORT).show();
               }

            }
        });
    }

    public void showDialog(final Context context, final String file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Download File Image");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadImage(context, file);
            }
        });

        builder.show();
    }

    void downloadImage(Context context, String url) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //request.setDescription("Some descrition");
        request.setTitle("Download new image");
        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            STATE_DOWNLOAD = 1;
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "pattaya");

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    class TaskBitmap extends AsyncTask<Void, Void, Bitmap> {
        ProgressDialog ringProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ringProgressDialog = ProgressDialog.show(FullscreenActivity.this, null, getResources().getString(R.string.please_wait), true);
            ringProgressDialog.setCancelable(true);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            Bitmap bitmap = null;
            if (path != null) {
                try {
                    // resize
                    bitmap = Glide.with(Application.getContext())
                            .load(path)
                            .asBitmap()
                            .fitCenter()
                            .into(700, 700) // Width and height
                            .get();
                    // imgDisplay.setImageBitmap(bitmap);
                } catch (InterruptedException error) {
                    error.printStackTrace();
                } catch (ExecutionException error) {
                    error.printStackTrace();
                }

            }
            if (pathUrl != null) {

                try {
                    // resize
                    bitmap = Glide.with(Application.getContext())
                            .load(pathUrl)
                            .asBitmap()
                            .fitCenter()
                            .into(width, height) // Width and height
                            .get();
                    //imgDisplay.setImageBitmap(bitmap);
                } catch (InterruptedException error) {
                    error.printStackTrace();
                } catch (ExecutionException error) {
                    error.printStackTrace();
                }


            }
            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ringProgressDialog.dismiss();
            if (bitmap != null) {
                imgDisplay.setImageBitmap(bitmap);


            } else {
                final AlertDialog alertDialog = new AlertDialog.Builder(FullscreenActivity.this).create();
                alertDialog.setMessage(getResources().getString(R.string.cant_connect_server));
                alertDialog.setButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
                try {
                    alertDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }


}


