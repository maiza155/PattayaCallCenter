package com.pattaya.pattayacallcenter.member.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.Data.Users;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.member.AddGroupActivity;
import com.pattaya.pattayacallcenter.member.ChatActivity;
import com.pattaya.pattayacallcenter.share.MemberDetailActivity;

import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;

/**
 * Created by SWF on 4/7/2015.
 */
public class ExpandableListAdapterFriend extends BaseExpandableListAdapter {

    final RestAdapter restAdapterOpenFire = RestAdapterOpenFire.getInstanceJson();
    final OpenfireQueary openfireQueary = restAdapterOpenFire.create(OpenfireQueary.class);
    Activity context;
    String jid;
    private Map<String, List<Users>> laptopCollections;
    private List<String> laptops;


    public ExpandableListAdapterFriend(Activity context, List<String> groupList,
                                       Map<String, List<Users>> laptopCollections) {
        this.context = context;
        this.laptopCollections = laptopCollections;
        this.laptops = groupList;

        SharedPreferences sp = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        jid = sp.getString(MasterData.SHARED_USER_JID, null);

    }


    public void resetAdapter(String key, List<Users> data) {
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
        final Users laptop = (Users) getChild(groupPosition, childPosition);
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.list_child_item_friend_custom, null);
            holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            holder.txtDepart = (TextView) convertView.findViewById(R.id.txt_department);
            holder.btnChat = (ImageButton) convertView.findViewById(R.id.btn_chat);
            holder.imageView = (RoundedImageView) convertView.findViewById(R.id.pic_profileAdapter);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        // holder.btnSelect.setVisibility(View.GONE);
        holder.txtName.setText(laptop.getName());
        String image = (laptop.getPic() == null || laptop.getPic().matches("")) ? "NO" : laptop.getPic();
        Glide.with(context)
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .override(100, 100)
                .fitCenter()
                .into(holder.imageView);

        holder.btnChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                //BusProvider.getInstance().post(listData.get(position));
                intent.putExtra("user", laptop);
                v.getContext().startActivity(intent);

            }

        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupPosition == 1) {
                    Intent intent = new Intent(v.getContext(), AddGroupActivity.class);
                    intent.putExtra("user", laptop);
                    v.getContext().startActivity(intent);

                } else {
                    Intent intent = new Intent(v.getContext(), MemberDetailActivity.class);
                    intent.putExtra("user", laptop);
                    v.getContext().startActivity(intent);

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
        ImageButton btnChat;
    }


}
