package com.pattaya.pattayacallcenter.member.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.Data.LastMessageData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by PROSPORT on 6/2/2558.
 */
public class AdapterListDeleteChat extends BaseAdapter {
    private LayoutInflater mInflater;

    private Context context; //รับ Context จาก CustomListViewActivity
    private ArrayList<LastMessageData> listData = new ArrayList(); //list ในการเก็บข้อมูลของ DataShow
    View convertView;
    Boolean[] bool;


    public AdapterListDeleteChat(Context context, ArrayList<LastMessageData> listData) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listData = listData;
        bool = new Boolean[listData.size()];
    }

    public void reseAdapter(ArrayList<LastMessageData> listData) {
        this.listData = listData;
        bool = new Boolean[listData.size()];
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
        TextView txtMessage;
        TextView txtTimeSend;
        CheckBox chk_selectDelete;
    }


    public ArrayList<LastMessageData> selectedItem() {
        ArrayList<LastMessageData> list = new ArrayList<>();
        for (int i = 0; i < bool.length; i++) {
            if (bool[i] != null && bool[i] == true) {
                list.add(listData.get(i));
            }
        }
        return list;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final HolderListAdapter holderListAdapter; //เก็บส่วนประกอบของ List แต่ละอัน

        if (convertView == null) {
            //ใช้ Layout ของ List เราเราสร้างขึ้นเอง (convertView.xml)
            convertView = mInflater.inflate(R.layout.list_delete_chat_custom, null);

            //สร้างตัวเก็บส่วนประกอบของ List แต่ละอัน
            holderListAdapter = new HolderListAdapter();

            //เชื่อมส่วนประกอบต่างๆ ของ List เข้ากับ View
            holderListAdapter.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            holderListAdapter.txtMessage = (TextView) convertView.findViewById(R.id.txt_message);
            holderListAdapter.txtTimeSend = (TextView) convertView.findViewById(R.id.txt_timesend);
            holderListAdapter.chk_selectDelete = (CheckBox) convertView.findViewById(R.id.chk_selectDelete);
            holderListAdapter.imageView = (RoundedImageView) convertView.findViewById(R.id.pic_profileAdapter);

            convertView.setTag(holderListAdapter);
        } else {
            holderListAdapter = (HolderListAdapter) convertView.getTag();
        }
        String name = (listData.get(position).getUser() == null) ? "Unknown" : listData.get(position).getUser().getName();
        //ดึงข้อมูลจาก listData มาแสดงทีละ position


        if (name != null && !name.matches("")) {
            holderListAdapter.txtName.setText(name);
            // holderListAdapter.txtMessage.setText(listData.get(position).getMessage());
            holderListAdapter.txtTimeSend.setText(listData.get(position).getTime());
            String image = (listData.get(position).getUser().getPic() == null
                    || listData.get(position).getUser().getPic().isEmpty())
                    ? "No" : listData.get(position).getUser().getPic();


            Glide.with(context)
                    .load(image)
                    .error(R.drawable.com_facebook_profile_picture_blank_square)
                    .override(100, 100)
                    .fitCenter()
                    .into(holderListAdapter.imageView);
        } else {
            if (listData.get(position).getUser() == null) {
                listData.remove(position);
                notifyDataSetChanged();
                return convertView;
            }

        }


        //ถ้าทำการเลือกที่ List จะแสดงข้อความ ว่าทำการเลือกที่ List ที่เท่าไร


        convertView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (bool[position] != null) {
                    if (bool[position]) {
                        bool[position] = false;
                    } else {
                        bool[position] = true;
                    }
                } else {
                    bool[position] = true;

                }
                holderListAdapter.chk_selectDelete.setChecked(bool[position]);
            }
        });

        return convertView;
    }


}
