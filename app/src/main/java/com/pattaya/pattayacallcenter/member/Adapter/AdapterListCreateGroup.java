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
import com.pattaya.pattayacallcenter.member.data.InviteFriendObject;

import java.util.ArrayList;

/**
 * Created by PROSPORT on 3/2/2558.
 */
public class AdapterListCreateGroup extends BaseAdapter{
    private LayoutInflater mInflater;
    private Context context; //รับ Context จาก CustomListViewActivity
    private ArrayList<InviteFriendObject> listData = new ArrayList<>(); //list ในการเก็บข้อมูลของ DataShow
    View convertView;

    public AdapterListCreateGroup(Context context,ArrayList listData) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listData = listData;
    }

    public  void resetAdapter(ArrayList<InviteFriendObject> listData){
        this.listData = listData;
        notifyDataSetChanged();
    }

    public void removeAt(int position){
        this.listData.remove(position);
        notifyDataSetChanged();
    }


    public ArrayList<InviteFriendObject> getListData() {
        return listData;
    }

    public void setListData(ArrayList<InviteFriendObject> listData) {
        this.listData = listData;
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
        HolderListAdapter holderListAdapter; //เก็บส่วนประกอบของ List แต่ละอัน

        if (convertView == null) {
            //ใช้ Layout ของ List เราเราสร้างขึ้นเอง (convertView.xml)
            convertView = mInflater.inflate(R.layout.list_add_group_custom, null);

            //สร้างตัวเก็บส่วนประกอบของ List แต่ละอัน
            holderListAdapter = new HolderListAdapter();

            //เชื่อมส่วนประกอบต่างๆ ของ List เข้ากับ View
            holderListAdapter.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            holderListAdapter.txtDepart = (TextView) convertView.findViewById(R.id.txt_department);
            holderListAdapter.btnDelete = (ImageButton) convertView.findViewById(R.id.btn_deleteMember);
            holderListAdapter.imageView = (RoundedImageView) convertView.findViewById(R.id.pic_profileAdapter);

            convertView.setTag(holderListAdapter);
        } else {
            holderListAdapter = (HolderListAdapter) convertView.getTag();
        }



        //ดึงข้อมูลจาก listData มาแสดงทีละ position
       holderListAdapter.txtName.setText(listData.get(position).getName());
       // holderListAdapter.txtDepart.setText(listData.get(position).getDepart());
        String image = (listData.get(position).getImage().isEmpty())?"No Image ":listData.get(position).getImage();
        Glide.with(context)
        .load(image)
        .override(200,200)
        .error(R.drawable.com_facebook_profile_picture_blank_square)
        .fitCenter()
        .into(holderListAdapter.imageView);

        //ถ้าทำการเลือกที่ List จะแสดงข้อความ ว่าทำการเลือกที่ List ที่เท่าไร


        holderListAdapter.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAt(position);

            }

        });



        return convertView;
    }


}
