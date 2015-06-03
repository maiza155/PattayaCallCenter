package com.pattaya.pattayacallcenter.member;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.Data.Users;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.member.Adapter.ExpandableListAdapter;
import com.pattaya.pattayacallcenter.member.data.BusGetFriendObject;
import com.pattaya.pattayacallcenter.member.data.InviteFriendObject;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;

public class FriendRequestActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final RestAdapter restAdapterOpenFire = RestAdapterOpenFire.getInstance();
    private final OpenfireQueary openfireQueary = restAdapterOpenFire.create(OpenfireQueary.class);
    public Button btn_addGroup;
    public Button btn_addFriend;
    // widget
    ImageButton btn;
    TextView titleTextView;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> groupList;
    private List<InviteFriendObject> childListGroup;
    private List<InviteFriendObject> childListFriend;
    private Map<String, List<InviteFriendObject>> laptopCollection;
    private Intent intent;
    private SharedPreferences sp;
    private String jid;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);
        BusProvider.getInstance().register(this);

        btn_addFriend = (Button) findViewById(R.id.btn_addFriend);
        btn_addGroup = (Button) findViewById(R.id.btn_addGroup);
        expandableListView = (ExpandableListView) findViewById(R.id.list);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.container_list);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandableListView.setIndicatorBounds(width - getDipsFromPixel(30), width
                    - getDipsFromPixel(10));
        } else {
            expandableListView.setIndicatorBoundsRelative(width - getDipsFromPixel(30), width
                    - getDipsFromPixel(10));
        }


        childListGroup = new ArrayList<>();
        childListFriend = new ArrayList<>();

        groupList = new ArrayList<>();
        groupList.add(getResources().getString(R.string.request_friend));
        groupList.add(getResources().getString(R.string.request_group));

        sp = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        jid = sp.getString(MasterData.SHARED_USER_JID, null);

        laptopCollection = new HashMap<>();
        laptopCollection.put(groupList.get(0), childListFriend);
        laptopCollection.put(groupList.get(1), childListGroup);

        expandableListAdapter = new ExpandableListAdapter(this, groupList, laptopCollection);
        expandableListView.setAdapter(expandableListAdapter);

        setActionBar();
        OnClick();
        init();

    }

    void init() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        new asynTask().execute();
        //new TaskGetFriend(this);


    }

    @Subscribe
    public void onSampleEvent(BusGetFriendObject busGetFriendObject) {

        System.out.println("recive Data>>>>>>>>>>>" + busGetFriendObject.getIsOk());
        mSwipeRefreshLayout.setRefreshing(false);
        if (busGetFriendObject.getIsOk()) {
            if (busGetFriendObject.getListGroupData().size() > 0) {
                expandableListAdapter.resetAdapter(groupList.get(1), busGetFriendObject.getListGroupData());
                expandableListView.expandGroup(1);
            }
            new asynTask().execute();
        } else {
            Toast.makeText(Application.getContext(),
                    getResources().getString(R.string.cant_connect_server), Toast.LENGTH_SHORT)
                    .show();

        }

    }

    public int getDipsFromPixel(float pixels) {

        final float scale = getResources().getDisplayMetrics().density;

        return (int) (pixels * scale + 0.5f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    private void OnClick() {

        btn_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), AddFriendActivity.class);
                //intent.putExtra("IdChat", id);
                v.getContext().startActivity(intent);
            }
        });
        btn_addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), CreateGroupActivity.class);
                //intent.putExtra("IdChat", id);
                v.getContext().startActivity(intent);
            }
        });
    }

    void setActionBar() {
        /** Set Title Center Actionbar */
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        btn.setPadding(0, 0, 0, 0);
        titleTextView.setText(getResources().getString(R.string.invite_add_friend));
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        new TaskGetFriend(this);

    }


    class asynTask extends AsyncTask<Void, Void, List<InviteFriendObject>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            childListFriend = new ArrayList<>();
            childListGroup = new ArrayList<>();
        }

        @Override
        protected List<InviteFriendObject> doInBackground(Void... params) {
            List<Users> list = DatabaseChatHelper.init().getUsers();
            for (Users e : list) {
                if (e.getType() == Users.TYPE_NOT_FRIEND) {
                    childListFriend.add(new InviteFriendObject(e.getJid(), e.getName(), e.getPic()));

                } else if (e.getType() == Users.TYPE_INVITE_GROUP) {
                    childListGroup.add(new InviteFriendObject(e.getJid(), e.getName(), e.getPic()));

                }
            }
            return childListFriend;
        }

        @Override
        protected void onPostExecute(List<InviteFriendObject> inviteFriendObjects) {
            super.onPostExecute(inviteFriendObjects);
            mSwipeRefreshLayout.setRefreshing(false);
            expandableListAdapter.resetAdapter(groupList.get(0), inviteFriendObjects);
            expandableListAdapter.resetAdapter(groupList.get(1), childListGroup);
            expandableListView.expandGroup(0);
            expandableListView.expandGroup(1);
        }
    }


}
