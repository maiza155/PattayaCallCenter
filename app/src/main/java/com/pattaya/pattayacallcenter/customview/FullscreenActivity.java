package com.pattaya.pattayacallcenter.customview;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.R;

import java.util.concurrent.ExecutionException;


public class FullscreenActivity extends Activity {
    CustomImageVIewZoomAble imgDisplay;
    String path;
    String pathUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        Intent i = getIntent();
        path = i.getStringExtra("path");
        pathUrl = i.getStringExtra("pathUrl");


        Button btnClose = (Button) findViewById(R.id.BtnCancel);

        imgDisplay = (CustomImageVIewZoomAble) findViewById(R.id.imgDisplay);

        new TaskBitmap().execute();


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });
    }

    class TaskBitmap extends AsyncTask<Void,Void,Bitmap> {
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
            if(bitmap !=null){
                imgDisplay.setImageBitmap(bitmap);

            }else{
                final AlertDialog alertDialog = new AlertDialog.Builder(FullscreenActivity.this).create();
                alertDialog.setMessage(getResources().getString(R.string.cant_connect_server));
                alertDialog.setButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
                alertDialog.show();
            }

        }
    }


}


