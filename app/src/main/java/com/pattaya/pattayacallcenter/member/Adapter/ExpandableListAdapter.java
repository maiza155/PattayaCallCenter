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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.XMPPManage;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.member.data.InviteFriendObject;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by SWF on 4/7/2015.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    Activity context;
    private Map<String, List<InviteFriendObject>> laptopCollections;
    private List<String> laptops;
    final RestAdapter restAdapterOpenFire = RestAdapterOpenFire.getInstanceJson();
    final OpenfireQueary openfireQueary = restAdapterOpenFire.create(OpenfireQueary.class);

    String jid;


    public ExpandableListAdapter(Activity context, List<String> groupList,
                                 Map<String, List<InviteFriendObject>> laptopCollections) {
        this.context = context;
        this.laptopCollections = laptopCollections;
        this.laptops = groupList;

        SharedPreferences sp = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        jid = sp.getString(MasterData.SHARED_USER_JID, null);

    }


    public void resetAdapter(String key, List<InviteFriendObject> data) {
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
        //mExpandableListView = (ExpandableListView) parent;
        //mExpandableListView.expandGroup(groupPosition);


        return convertView;
    }


    void removeAt(final int groupPosition, final int childPosition) {
        laptopCollections.get(getGroup(groupPosition)).remove(childPosition);
        notifyDataSetChanged();

    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final InviteFriendObject laptop = (InviteFriendObject) getChild(groupPosition, childPosition);
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.list_friend_request_custom, null);
            holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            holder.txtDepart = (TextView) convertView.findViewById(R.id.txt_department);
            holder.btnSelect = (ImageButton) convertView.findViewById(R.id.btn_acceptMember);
            holder.imageView = (RoundedImageView) convertView.findViewById(R.id.pic_profileAdapter);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        // holder.btnSelect.setVisibility(View.GONE);
        holder.txtName.setText(laptop.getName());
        String image = (laptop.getImage() == null || laptop.getImage().matches("")) ? "NO" : laptop.getImage();
        Glide.with(context)
                .load(image)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .override(100, 100)
                .fitCenter()
                .into(holder.imageView);
        holder.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupPosition == 0) {
                    Boolean bool = XMPPManage.getInstance().createRoster(laptop.getJid(), laptop.getName());
                    if (bool) {
                        removeAt(groupPosition, childPosition);

                        BusProvider.getInstance().post("add_roster_complete");
                    } else {
                        Toast.makeText(context,
                                context.getResources().getString(R.string.cant_connect_server), Toast.LENGTH_SHORT)
                                .show();
                    }

                } else if (groupPosition == 1) {
                    openfireQueary.updateChatRoom(laptop.getJid().split("@")[0], "members", jid, new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            System.out.println("response = [" + response + "], response2 = [" + response2 + "]");
                            removeAt(groupPosition, childPosition);
                            XMPPManage.setJoinRoom(laptop.getJid());
                            BusProvider.getInstance().post("add_roster_complete");
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            System.out.println("error = [" + error + "]");
                            Toast.makeText(context,
                                    context.getResources().getString(R.string.cant_connect_server), Toast.LENGTH_SHORT)
                                    .show();

                        }
                    });

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
