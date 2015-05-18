package com.pattaya.pattayacallcenter.member.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.webservice.object.organize.OrgDetail;
import com.pattaya.pattayacallcenter.webservice.object.organize.UserIdObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PROSPORT on 3/2/2558.
 */
public class AdapterListViewInviteOrg extends BaseAdapter {

    View convertView;
    List<String> listselectdata = new ArrayList<>();
    List<String> listselectdataJid = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context; //รับ Context จาก CustomListViewActivity
    private List<OrgDetail> listData; //list ในการเก็บข้อมูลของ DataShow

    public AdapterListViewInviteOrg(Context context, List listData) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listData = listData;

    }

    public void resetAdapter(List list) {
        this.listData = list;
        notifyDataSetChanged();

    }

    public List getListSelect() {
        List<UserIdObject> userIdObjects = new ArrayList<>();
        for (String e : listselectdata) {
            userIdObjects.add(new UserIdObject(e));
        }
        return userIdObjects;
    }

    public List<String> getListSelectJid() {

        return listselectdataJid;
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
            convertView = mInflater.inflate(R.layout.list_add_friend_custom, null);
            //สร้างตัวเก็บส่วนประกอบของ List แต่ละอัน
            holder = new Holder();
            //เชื่อมส่วนประกอบต่างๆ ของ List เข้ากับ View
            holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            holder.txtDepart = (TextView) convertView.findViewById(R.id.txt_department);
            holder.btnSelect = (ImageButton) convertView.findViewById(R.id.btn_selectMember);
            holder.imageView = (RoundedImageView) convertView.findViewById(R.id.pic_profileAdapter);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.txtName.setText(listData.get(position).getDisplayName());
        holder.txtDepart.setVisibility(View.GONE);
       // holder.txtAlertFriend.setVisibility(View.GONE);
        holder.btnSelect.setVisibility(View.GONE);

        if (listselectdata.contains(listData.get(position).getUserId())) {
            holder.btnSelect.setVisibility(View.VISIBLE);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (listselectdata.contains(listData.get(position).getUserId())) {
                    int index = listselectdata.indexOf(listData.get(position).getUserId());
                    listselectdata.remove(listData.get(position).getUserId());

                    listselectdataJid.remove(index);
                    holder.btnSelect.setVisibility(View.GONE);
                } else {
                    int index = listselectdata.indexOf(listData.get(position).getUserId());
                    listselectdata.add(listData.get(position).getUserId());
                    listselectdataJid.add(listData.get(position).getjId());
                    holder.btnSelect.setVisibility(View.VISIBLE);
                }
            }
        });

        Glide.with(Application.getContext())
                .load(listData.get(position).getUserImage())
                .override(200,200)
                .fitCenter()
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .into(holder.imageView);
        return convertView;
    }

    public class Holder {
        RoundedImageView imageView;
        TextView txtName;
        TextView txtDepart;
        TextView txtAlertFriend;
        ImageButton btnSelect;
    }
}
