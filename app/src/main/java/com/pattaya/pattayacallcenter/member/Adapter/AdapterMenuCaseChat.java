package com.pattaya.pattayacallcenter.member.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pattaya.pattayacallcenter.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by SWF on 4/20/2015.
 */
public class AdapterMenuCaseChat extends BaseAdapter {

    Context context;
    private LayoutInflater mInflater;
    OnItemClickListener mItemClickListener;


    public static HashMap<Integer, MenuObject> mMenuData = new HashMap<>();
    public static HashMap<Integer, List<Integer>> mMenuList = new HashMap<>();
    List<MenuObject> listData = new ArrayList<>();

    public AdapterMenuCaseChat(Context context, List<Integer> menu) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        init();
        Set<Integer> set = new HashSet<>();
        for (int e : menu) {
            set.addAll(mMenuList.get(e));
        }
        for (int e : set) {
            listData.add(mMenuData.get(e));
        }

    }

    void init() {
        mMenuData.put(1, new MenuObject(R.drawable.asset_icon_normal_info_white, "รายละเอียดงาน", 1));
        mMenuData.put(2, new MenuObject(R.drawable.asset_icon_normal_edit, "เเก้ไข", 2));
        mMenuData.put(3, new MenuObject(R.drawable.asset_icon_normal_doc, "ผลลัพธ์", 3));
        mMenuData.put(4, new MenuObject(R.drawable.asset_icon_normal_delete, "ลบ", 4));
        mMenuData.put(5, new MenuObject(R.drawable.asset_icon_normal_fwd, "ส่งต่อ", 5));
        mMenuData.put(6, new MenuObject(R.drawable.asset_icon_normal_calendar, "วันที่ปฏิบัติงาน", 6));
        mMenuData.put(7, new MenuObject(R.drawable.asset_icon_normal_task, "ปิดเคส", 7));


        List<Integer> listOne = new ArrayList<>();
        listOne.add(1);
        listOne.add(5);
        listOne.add(6);
        listOne.add(3);
        listOne.add(7);


        mMenuList.put(1, listOne);

        List<Integer> listTwo = new ArrayList<>();
        listTwo.add(1);
        listTwo.add(2);
        listTwo.add(4);
        listTwo.add(3);
        listTwo.add(5);
        listTwo.add(7);


        mMenuList.put(2, listTwo);

        List<Integer> listThree = new ArrayList<>();
        listThree.add(1);
        listThree.add(3);
        listThree.add(2);
        listThree.add(5);
        mMenuList.put(3, listOne);


        List<Integer> listFour = new ArrayList<>();
        listFour.add(1);
        listFour.add(4);
        listFour.add(3);


        mMenuList.put(4, listFour);
    }

    public interface OnItemClickListener {
        public void onItemClick(int id);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public int getCount() {
        return listData.size();
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_case_menu, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.text.setText(listData.get(position).getName());
        holder.text.setCompoundDrawablesWithIntrinsicBounds(listData.get(position).getIcon(), 0, 0, 0);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(listData.get(position).getId());
                }
            }
        });


        return convertView;
    }


    class Holder {
        TextView text;

        Holder(View v) {
            text = (TextView) v.findViewById(R.id.title);

        }
    }

    public static class MenuObject {
        int icon;
        String name;
        int id;

        public MenuObject(int icon, String name, int id) {
            this.icon = icon;
            this.name = name;
            this.id = id;
        }

        public int getIcon() {
            return icon;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }
}
