package com.pattaya.pattayacallcenter.member.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.webservice.object.organize.OrgDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PROSPORT on 7/2/2558.
 */
public class AdapterListMemberOrganize extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<OrgDetail> listData = new ArrayList(); //list ในการเก็บข้อมูลของ DataShow
    private TextView count;


    public AdapterListMemberOrganize(Context context, List listData, TextView count) {
        // TODO Auto-generated constructor stub
        this.count = count;
        this.mInflater = LayoutInflater.from(context);
        this.listData = listData;
        this.count.setText(String.valueOf(listData.size()));
    }

    public void resetAdapter(List data) {
        listData = data;
        this.count.setText(String.valueOf(listData.size()));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listData.size(); //ส่งขนาดของ List ที่เก็บข้อมุลอยู่
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Holder holder; //เก็บส่วนประกอบของ List แต่ละอัน

        if (convertView == null) {
            //ใช้ Layout ของ List เราเราสร้างขึ้นเอง (convertView.xml)
            convertView = mInflater.inflate(R.layout.list_member_organize, null);

            //สร้างตัวเก็บส่วนประกอบของ List แต่ละอัน
            holder = new Holder();

            //เชื่อมส่วนประกอบต่างๆ ของ List เข้ากับ View
            holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            holder.txtDepart = (TextView) convertView.findViewById(R.id.txt_department);
            holder.imageView = (RoundedImageView) convertView.findViewById(R.id.pic_profileAdapter);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }


        holder.txtName.setText(listData.get(position).getDisplayName());
        holder.txtDepart.setVisibility(View.GONE);
       // holder.imageView.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);


        Glide.with(Application.getContext())
                .load(listData.get(position).getUserImage())
                .override(200,200)
                .fitCenter()
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .into(holder.imageView);
        //ถ้าทำการเลือกที่ List จะแสดงข้อความ ว่าทำการเลือกที่ List ที่เท่าไร

        final View finalConvertView = convertView;


        convertView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });

        return convertView;
    }


    class Holder {
        RoundedImageView imageView;
        TextView txtName;
        TextView txtDepart;

    }


}
