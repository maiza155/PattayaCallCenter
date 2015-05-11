package com.pattaya.pattayacallcenter.member.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.webservice.object.friend.DataListInviteFriend;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by SWF on 4/7/2015.
 */
public class ExpandableListAdapterAddFriend extends BaseExpandableListAdapter {

    Activity context;
    private Map<String, List<DataListInviteFriend>> laptopCollections;
    private List<String> laptops;

    String jid;

    Set<DataListInviteFriend> listSelectFriend = new HashSet<>();
    Set<DataListInviteFriend> listSelectGroup = new HashSet<>();


    public ExpandableListAdapterAddFriend(Activity context, List<String> groupList,
                                          Map<String, List<DataListInviteFriend>> laptopCollections) {
        this.context = context;
        this.laptopCollections = laptopCollections;
        this.laptops = groupList;

        SharedPreferences sp = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        jid = sp.getString(MasterData.SHARED_USER_JID, null);

    }


    public Set<DataListInviteFriend> getListSelectFriend() {
        return listSelectFriend;
    }

    public Set<DataListInviteFriend> getListSelectGroup() {
        return listSelectGroup;
    }

    public void resetAdapter(String key, List<DataListInviteFriend> data) {
        this.laptopCollections.put(key, data);
        notifyDataSetChanged();

    }


    @Override
    public int getGroupCount() {
        return laptops.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return laptopCollections.get(laptops.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return laptops.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return laptopCollections.get(laptops.get(groupPosition)).get(childPosition);

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String laptopName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.custom_header_expand, null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.txt_head);
        item.setText(laptopName + " (" + getChildrenCount(groupPosition) + ")");


        return convertView;
    }


    void removeAt(final int groupPosition, final int childPosition) {
        laptopCollections.get(getGroup(groupPosition)).remove(childPosition);
        notifyDataSetChanged();

    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final DataListInviteFriend laptop = (DataListInviteFriend) getChild(groupPosition, childPosition);
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.list_add_friend_custom, null);
            holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            holder.txtDepart = (TextView) convertView.findViewById(R.id.txt_department);
            holder.btnSelect = (ImageButton) convertView.findViewById(R.id.btn_selectMember);
            holder.imageView = (RoundedImageView) convertView.findViewById(R.id.pic_profileAdapter);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.btnSelect.setVisibility(View.GONE);

        if (groupPosition == 0 && listSelectFriend.contains(laptop)) {
            holder.btnSelect.setVisibility(View.VISIBLE);
        } else if (groupPosition == 1 && listSelectGroup.contains(laptop)) {
            holder.btnSelect.setVisibility(View.VISIBLE);
        }

        holder.txtName.setText(laptop.getDisplayName());
        String image = (laptop.getUserImage() == null || laptop.getUserImage().matches("")) ? "NO" : laptop.getUserImage();
        Glide.with(context)
                .load(image)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .override(100, 100)
                .fitCenter()
                .into(holder.imageView);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupPosition == 0) {
                    if (listSelectFriend.contains(laptop)) {
                        listSelectFriend.remove(laptop);
                        holder.btnSelect.setVisibility(View.GONE);
                    } else {
                        listSelectFriend.add(laptop);
                        holder.btnSelect.setVisibility(View.VISIBLE);
                    }

                } else if (groupPosition == 1) {
                    if (listSelectGroup.contains(laptop)) {
                        listSelectGroup.remove(laptop);
                        holder.btnSelect.setVisibility(View.GONE);

                    } else {
                        listSelectGroup.add(laptop);
                        holder.btnSelect.setVisibility(View.VISIBLE);
                    }


                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }


    class Holder {
        RoundedImageView imageView;
        TextView txtName;
        TextView txtDepart;
        ImageButton btnSelect;
    }


}
