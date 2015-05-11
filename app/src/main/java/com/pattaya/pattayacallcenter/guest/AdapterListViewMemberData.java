package com.pattaya.pattayacallcenter.guest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by PROSPORT on 29/1/2558.
 */
public class AdapterListViewMemberData extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context context; //รับ Context จาก CustomListViewActivity
    private ArrayList<OrganizeMemberListActivity.DataShow> listData = new ArrayList<OrganizeMemberListActivity.DataShow>(); //list ในการเก็บข้อมูลของ DataShow
    View convertView;
    public AdapterListViewMemberData(Context context, ArrayList<OrganizeMemberListActivity.DataShow> listData) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
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

    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        HolderListAdapter holderListAdapter; //เก็บส่วนประกอบของ List แต่ละอัน

        if(convertView == null)
        {
            //ใช้ Layout ของ List เราเราสร้างขึ้นเอง (convertView.xml)
            convertView = mInflater.inflate(R.layout.list_member_custom, null);

            //สร้างตัวเก็บส่วนประกอบของ List แต่ละอัน
            holderListAdapter = new HolderListAdapter();

            //เชื่อมส่วนประกอบต่างๆ ของ List เข้ากับ View
            holderListAdapter.txtName = (TextView) convertView.findViewById(R.id.txt_nameAdapter);

            holderListAdapter.imageView = (RoundedImageView) convertView.findViewById(R.id.pic_profileAdapter);

            convertView.setTag(holderListAdapter);
        }else{
            holderListAdapter = (HolderListAdapter) convertView.getTag();
        }

        //ดึงข้อมูลจาก listData มาแสดงทีละ position
        holderListAdapter.txtName.setText(listData.get(position).getName());
        holderListAdapter.imageView.setImageResource(listData.get(position).getPic());


        //ถ้าทำการเลือกที่ List จะแสดงข้อความ ว่าทำการเลือกที่ List ที่เท่าไร

        final View finalConvertView = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.e("Click", "" + v.getBackground().hashCode() + "---State >");
                parent.setBackgroundResource(R.drawable.list_member_select_event);
               // v.setSelected(true);
                int color = R.color.yellowColor;
                color = v.getBackground().hashCode();
                //v.setBackgroundResource( R.color.greenColor);
                Toast.makeText(context, "List " + position + " click!!", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

}
