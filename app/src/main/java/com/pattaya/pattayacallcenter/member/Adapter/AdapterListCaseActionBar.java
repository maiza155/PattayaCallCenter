package com.pattaya.pattayacallcenter.member.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.member.dummy.DataPopUp;

import java.util.ArrayList;

/**
 * Created by SWF on 2/9/2015.
 */
public class AdapterListCaseActionBar extends BaseAdapter {
    ArrayList<DataPopUp> data;
    Holder holder;
    OnItemClickListener mItemClickListener;
    private LayoutInflater mInflater;
    private Context context;

    public AdapterListCaseActionBar(ArrayList data, Context context) {
        this.data = data;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.custom_data_listview_popup, null);
            holder = new Holder(convertView,position);
            convertView.setTag(holder);

        }else{
            holder = (Holder) convertView.getTag();
        }

        holder.textView.setText(data.get(position).getName());


        Glide.with(context)
                .load(data.get(position).getImage())
                .override(200, 200)
                .error(R.drawable.com_facebook_profile_picture_blank_portrait)
                .fitCenter()
                .into(holder.imageView);
        return convertView;
    }

    public interface OnItemClickListener {
        public void onItemClick(View v, int position);
    }

    class Holder implements View.OnClickListener  {
        TextView textView;
        ImageView imageView;
        int position;

        Holder(View v ,int position) {
            textView = (TextView) v.findViewById(R.id.textview_popup);
            imageView = (ImageView) v.findViewById(R.id.imagepopup);
            this.position = position;
            v.setOnClickListener(this);
        }

        public int getPosition() {
            return position;
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v,getPosition());
            }

        }
    }
}
