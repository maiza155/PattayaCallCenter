package com.pattaya.pattayacallcenter.member.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;

/**
 * Created by SWF on 4/18/2015.
 */
public class AdapterListForward extends BaseAdapter {
    private LayoutInflater mInflater;

    private Context context; //รับ Context จาก CustomListViewActivity
    public ArrayList<ForwardObject> listData = new ArrayList(); //list ในการเก็บข้อมูลของ DataShow
    View convertView;

    public AdapterListForward(Context context, ArrayList listData) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listData = listData;
    }

    public void resetAdapter(ArrayList listData) {
        this.listData = listData;
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

    public class HolderListAdapter {
        RoundedImageView imageView;
        TextView txtName;
        TextView txtDepart;
        ImageButton btnDelete;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        HolderListAdapter holder; //เก็บส่วนประกอบของ List แต่ละอัน

        if (convertView == null) {
            //ใช้ Layout ของ List เราเราสร้างขึ้นเอง (convertView.xml)
            convertView = mInflater.inflate(R.layout.list_add_group_custom, null);

            //สร้างตัวเก็บส่วนประกอบของ List แต่ละอัน
            holder = new HolderListAdapter();

            //เชื่อมส่วนประกอบต่างๆ ของ List เข้ากับ View
            holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            holder.txtDepart = (TextView) convertView.findViewById(R.id.txt_department);
            holder.btnDelete = (ImageButton) convertView.findViewById(R.id.btn_deleteMember);
            holder.imageView = (RoundedImageView) convertView.findViewById(R.id.pic_profileAdapter);

            convertView.setTag(holder);
        } else {
            holder = (HolderListAdapter) convertView.getTag();
        }

        //ดึงข้อมูลจาก listData มาแสดงทีละ position
        holder.txtName.setText(listData.get(position).getName());
        String image = (listData.get(position).getImage() == null
                || listData.get(position).getImage().matches("")) ? "No" : listData.get(position).getImage();

        Glide.with(context)
                .load(image)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .override(200, 200)
                .fitCenter()
                .into(holder.imageView);


        holder.txtDepart.setVisibility(View.GONE);
        //ถ้าทำการเลือกที่ List จะแสดงข้อความ ว่าทำการเลือกที่ List ที่เท่าไร

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //ShowConfirmDialog();
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("ยืนยันการลบข้อมูล");
                alert.setMessage("ยืนยันการลบข้อมูล");

                alert.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                alert.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        listData.remove(position);
                        notifyDataSetChanged();

                    }
                });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();


            }

        });


        return convertView;
    }


}