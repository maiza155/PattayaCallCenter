package com.pattaya.pattayacallcenter.member.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.member.data.ForwardObject;
import com.pattaya.pattayacallcenter.webservice.object.UserDataObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by SWF on 2/10/2015.
 */
public class AdapterListToUser extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context context; //รับ Context จาก CustomListViewActivity
    private List<UserDataObject> listData; //list ในการเก็บข้อมูลของ DataShow
    private List<UserDataObject> selectedData;
    Map<Integer, UserDataObject> mapselected = new HashMap<>();


    public AdapterListToUser(Context context, ArrayList listData, ArrayList selectedData) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listData = listData;
        this.selectedData = selectedData;
        init();
    }

    void init() {
        for (UserDataObject e : selectedData) {
            mapselected.put(e.getUserId(), e);
        }
    }


    public void resetAdapter(List<UserDataObject> listData) {
        this.listData = listData;
        notifyDataSetChanged();
    }

    public ArrayList<ForwardObject> getSelected() {
        ArrayList<ForwardObject> data = new ArrayList<>();
        Iterator it = mapselected.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry map = (Map.Entry) it.next();
            UserDataObject object = (UserDataObject) map.getValue();
            data.add(new ForwardObject(object.getUserId(), object.getDisplayName(), object.getUserImage(), object.getjId()));
            System.out.println(((UserDataObject) map.getValue()).getDisplayName());
        }
        return data;
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

            convertView = mInflater.inflate(R.layout.list_add_friend_custom, null);
            //สร้างตัวเก็บส่วนประกอบของ List แต่ละอัน
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        //ดึงข้อมูลจาก listData มาแสดงทีละ position
        String name = (listData.get(position).getDisplayName().matches(" ")) ? "Unknown" : listData.get(position).getDisplayName();
        String image = (listData.get(position).getUserImage() == null
                || listData.get(position).getUserImage().matches("")) ? "Unknown" : listData.get(position).getUserImage();

        holder.txtName.setText(name);
        holder.txtDepart.setVisibility(View.GONE);

        Glide.with(context)
                .load(image)
                .override(100, 100)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .fitCenter()
                .into(holder.imageView);


        //holder.imageView.setImageResource(listData.get(position).getPic());
        boolean isSelect = isSelected(listData.get(position).getUserId());
        if (isSelect) {
            holder.btnSelect.setVisibility(View.VISIBLE);
        } else {
            holder.btnSelect.setVisibility(View.GONE);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelected(listData.get(position).getUserId())) {
                    holder.btnSelect.setVisibility(View.GONE);
                    mapselected.remove(listData.get(position).getUserId());
                } else {
                    holder.btnSelect.setVisibility(View.VISIBLE);
                    mapselected.put(listData.get(position).getUserId(), listData.get(position));
                }

            }
        });


        return convertView;
    }


    Boolean isSelected(int id) {
        boolean bool = false;
        if (mapselected.get(id) != null) {
            return true;
        }
        return bool;
    }


    private class Holder {
        RoundedImageView imageView;
        TextView txtName;
        TextView txtDepart;
        ImageButton btnSelect;

        public Holder(View v) {
            //เชื่อมส่วนประกอบต่างๆ ของ List เข้ากับ View
            txtName = (TextView) v.findViewById(R.id.txt_name);
            txtDepart = (TextView) v.findViewById(R.id.txt_department);
            btnSelect = (ImageButton) v.findViewById(R.id.btn_selectMember);
            imageView = (RoundedImageView) v.findViewById(R.id.pic_profileAdapter);

        }
    }
}
