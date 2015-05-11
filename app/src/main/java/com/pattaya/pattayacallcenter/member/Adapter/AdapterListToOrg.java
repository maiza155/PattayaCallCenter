package com.pattaya.pattayacallcenter.member.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.member.data.ForwardObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.listforward.OrganizeObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by SWF on 2/10/2015.
 */
public class AdapterListToOrg extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context context; //รับ Context จาก CustomListViewActivity
    private ArrayList<OrganizeObject> listData; //list ในการเก็บข้อมูลของ DataShow
    private ArrayList<OrganizeObject> selectedData;
    Map<Integer, OrganizeObject> mapselected = new HashMap<>();


    public AdapterListToOrg(Context context, ArrayList listData, ArrayList selectedData) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listData = listData;
        this.selectedData = selectedData;
        init();
    }

    void init() {
        for (OrganizeObject e : selectedData) {
            mapselected.put(e.getOrgId(), e);
        }
    }


    public void resetAdapter(ArrayList<OrganizeObject> listData) {
        this.listData = listData;
        notifyDataSetChanged();
    }

    public ArrayList<ForwardObject> getSelected() {
        ArrayList<ForwardObject> data = new ArrayList<>();
        Iterator it = mapselected.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry map = (Map.Entry) it.next();
            OrganizeObject object = (OrganizeObject) map.getValue();
            data.add(new ForwardObject(object.getOrgId(), object.getOrgName(), null));
            System.out.println(((OrganizeObject) map.getValue()).getDisplayname());
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
            //ใช้ Layout ของ List เราเราสร้างขึ้นเอง (convertView.xml)
            convertView = mInflater.inflate(R.layout.list_add_friend_custom, null);
            //สร้างตัวเก็บส่วนประกอบของ List แต่ละอัน
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        //ดึงข้อมูลจาก listData มาแสดงทีละ position
        holder.txtName.setText(listData.get(position).getDisplayname());
        holder.txtDepart.setVisibility(View.GONE);

        //holder.imageView.setImageResource(listData.get(position).getPic());
        boolean isSelect = isSelected(listData.get(position).getOrgId());
        if (isSelect) {
            holder.btnSelect.setVisibility(View.VISIBLE);
        } else {
            holder.btnSelect.setVisibility(View.GONE);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelected(listData.get(position).getOrgId())) {
                    System.out.println(true);
                    holder.btnSelect.setVisibility(View.GONE);
                    mapselected.remove(listData.get(position).getOrgId());
                } else {
                    System.out.println(false);
                    holder.btnSelect.setVisibility(View.VISIBLE);
                    mapselected.put(listData.get(position).getOrgId(), listData.get(position));
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
