package com.pattaya.pattayacallcenter.member.Adapter;

import android.content.Context;
import android.content.Intent;
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
    private LayoutInflater mInflater;
    List<String> imageList = new ArrayList<>();

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
        return convertView;
    }

    class Holder{
        ImageView image;
    }


}