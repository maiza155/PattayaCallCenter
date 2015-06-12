package com.pattaya.pattayacallcenter.member.Adapter;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.FullscreenActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SWF on 3/25/2015.
 */
public  class ImageAdapterList extends BaseAdapter {
    Context context;
    List<String> imageList = new ArrayList<>();
    private LayoutInflater mInflater;

    public ImageAdapterList(Context context, List data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        imageList = data;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.custom_list_image , null);
            holder = new Holder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);

        }else{
            holder = (Holder) convertView.getTag();
        }


        Glide.with(context).load(imageList.get(position))
                .error(R.drawable.img_not_found)
                .placeholder(R.drawable.loading)
                .override(400, 400)
                .fitCenter()
                .into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullscreenActivity.class);
                intent.putExtra("pathUrl", imageList.get(position));
                intent.putExtra("position", position);
                v.getContext().startActivity(intent);
            }
        });

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(v.getContext(), imageList.get(position));
                return false;
            }
        });
        return convertView;
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
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "pattaya");

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }



    class Holder{
        ImageView image;
    }


}