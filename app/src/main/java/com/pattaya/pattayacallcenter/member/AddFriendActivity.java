package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.Data.Users;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.chat.XMPPManage;
import com.pattaya.pattayacallcenter.member.Adapter.ExpandableListAdapterAddFriend;
import com.pattaya.pattayacallcenter.member.data.InviteFriendObject;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.friend.DataListInviteFriend;
import com.pattaya.pattayacallcenter.webservice.object.friend.FriendAndGroupList;
import com.pattaya.pattayacallcenter.webservice.object.friend.GetListInviteFriendObject;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddFriendActivity extends ActionBarActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private final RestAdapter restAdapter = WebserviceConnector.getInstance();
    private final RestFulQueary restFulQueary = restAdapter.create(RestFulQueary.class);
    private List<String> groupList;
    private List<DataListInviteFriend> listfriend;
    private List<DataListInviteFriend> listGroup;
    private Map<String, List<DataListInviteFriend>> laptopCollection;
    private ExpandableListView expandableListView;
    private ExpandableListAdapterAddFriend adapterList;
    private ImageButton btn;
    private TextView titleTextView;
    private Button btnAddFriend;
    private EditText search;
    private XMPPManage xmppManage = XMPPManage.getInstance();
    private String jid;
    private Boolean isGroup;


    private ArrayList oldListData = new ArrayList();
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_and_group);
        isGroup = getIntent().getBooleanExtra("group", false);
        search = (EditText) findViewById(R.id.search);
        btnAddFriend = (Button) findViewById(R.id.btn_add_friend);
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

        listfriend = new ArrayList<>();
        listGroup = new ArrayList<>();

        laptopCollection = new HashMap<>();
        groupList = new ArrayList<>();


        if (isGroup) {
            ArrayList mamberlist = getIntent().getParcelableArrayListExtra("member");
            ArrayList outcastlist = getIntent().getParcelableArrayListExtra("outcast");
            oldListData.addAll(mamberlist);
            oldListData.addAll(outcastlist);

            groupList.add("friends");
            laptopCollection.put(groupList.get(0), listfriend);


        } else {
            ArrayList<Users> arrUsers = DatabaseChatHelper.init().getUsers();
            for (Users users : arrUsers) {
                if (users.getType() == Users.TYPE_FRIEND) {
                    oldListData.add(users.getJid());
                }
            }


            groupList.add("friends");
            groupList.add("groups");


            laptopCollection.put(groupList.get(0), listfriend);
            laptopCollection.put(groupList.get(1), listGroup);

        }


        adapterList = new ExpandableListAdapterAddFriend(this, groupList, laptopCollection);
        expandableListView.setAdapter(adapterList);


        btnAddFriend.setOnClickListener(this);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // System.out.println(s.length() + " / " + String.valueOf(s));
                getList(String.valueOf(s));
            }
        });


        init();

        setActionBar();

        setClickLisener();

        getList("");


    }


    void init() {
        SharedPreferences sp = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        jid = sp.getString(MasterData.SHARED_USER_JID, null);
    }


    void setActionBar() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        btn.setPadding(0, 0, 0, 0);
        titleTextView.setText(getResources().getString(R.string.add_friend));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }


    public int getDipsFromPixel(float pixels) {

        final float scale = getResources().getDisplayMetrics().density;

        return (int) (pixels * scale + 0.5f);
    }


    private void setClickLisener() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void getList(final String s) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                GetListInviteFriendObject getListInviteFriendObject = new GetListInviteFriendObject();
                getListInviteFriendObject.setFirstname(s);
                getListInviteFriendObject.setLastname(s);
                listfriend = new ArrayList<>();
                listGroup = new ArrayList<>();
                restFulQueary.getInviteFriend(getListInviteFriendObject, new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response2) {
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        try {
                            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                            String line;

                            try {
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        String JsonConvertData = "{data:" + sb.toString() + "}";
                        FriendAndGroupList Object = new Gson().fromJson(JsonConvertData, FriendAndGroupList.class);
                        for (DataListInviteFriend e : Object.getData()) {

                            if (e.getDataType().matches("USER") && e.getJid() != null && !e.getJid().matches(jid)) {
                                if (!oldListData.contains(e.getJid())) {
                                    listfriend.add(e);
                                }

                            } else if (e.getDataType().matches("GROUP") && !isGroup) {
                                listGroup.add(e);
                            }
                        }

                        if (!isGroup) {
                            adapterList.resetAdapter(groupList.get(1), listGroup);
                            expandableListView.expandGroup(1);
                        }

                        adapterList.resetAdapter(groupList.get(0), listfriend);
                        expandableListView.expandGroup(0);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.cant_connect_server), Toast.LENGTH_SHORT)
                                .show();

                    }
                });
                return null;
            }
        }.execute();


    }

    void addFriend() {

        new AsyncTask<Void, Void, Boolean>() {
            ProgressDialog ringProgressDialog;
            ArrayList<InviteFriendObject> inviteFriendObjects = new ArrayList<>();
            Boolean bool = false;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ringProgressDialog = ProgressDialog.show(AddFriendActivity.this, null, getResources().getString(R.string.please_wait), true);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                if (adapterList.getListSelectFriend().size() > 0 || adapterList.getListSelectGroup().size() > 0) {
                    for (DataListInviteFriend e : adapterList.getListSelectFriend()) {
                        System.out.println(e.toString());
                        if (isGroup) {
                            bool = true;
                            inviteFriendObjects.add(new InviteFriendObject(e.getJid(), e.getDisplayName(), e.getUserImage()));
                        } else {
                            //bool = xmppManage.createRoster(e.getJid(), e.getDisplayName());
                            final String[] group = {xmppManage.GROUP_FRIEND};
                            if (xmppManage.getmConnection() != null && xmppManage.getmConnection().isConnected()) {
                                Roster roster = xmppManage.getmConnection().getRoster();
                                if (roster.getGroup(xmppManage.GROUP_FRIEND) == null) {
                                    roster.createGroup(xmppManage.GROUP_FRIEND);
                                } else {
                                    roster.getGroup(xmppManage.GROUP_FRIEND);
                                }
                                Presence subscribe = new Presence(Presence.Type.subscribe);
                                subscribe.setTo(jid);
                                try {
                                    xmppManage.getmConnection().sendPacket(subscribe);
                                    roster.createEntry(e.getJid(), e.getDisplayName(), group);
                                    final Users mUser = new Users(e.getJid(), e.getDisplayName(), null, Users.TYPE_FRIEND);
                                    mUser.setPic(e.getUserImage());
                                    DatabaseChatHelper.init().addUsers(mUser);
                                    bool = true;
                                } catch (SmackException.NotConnectedException e1) {
                                    e1.printStackTrace();

                                } catch (SmackException.NotLoggedInException e2) {
                                    e2.printStackTrace();

                                } catch (XMPPException.XMPPErrorException e3) {
                                    e3.printStackTrace();

                                } catch (SmackException.NoResponseException e4) {
                                    e4.printStackTrace();

                                }

                            } else {
                                bool = false;
                            }


                        }

                    }


                    for (DataListInviteFriend e : adapterList.getListSelectGroup()) {
                        if (e.getUserList() != null) {
                            String[] userList = e.getUserList().split(",");
                            for (String user : userList) {
                                String[] data = user.split("\\|");
                                if (data.length > 1) {
                                    if (isGroup) {
                                        bool = true;

                                    } else {
                                        final String[] group = {xmppManage.GROUP_FRIEND};
                                        if (xmppManage.getmConnection() != null && xmppManage.getmConnection().isConnected()) {
                                            Roster roster = xmppManage.getmConnection().getRoster();
                                            if (roster.getGroup(xmppManage.GROUP_FRIEND) == null) {
                                                roster.createGroup(xmppManage.GROUP_FRIEND);
                                            } else {
                                                roster.getGroup(xmppManage.GROUP_FRIEND);
                                            }
                                            Presence subscribe = new Presence(Presence.Type.subscribe);
                                            subscribe.setTo(jid);
                                            try {
                                                xmppManage.getmConnection().sendPacket(subscribe);
                                                roster.createEntry(data[0], data[1], group);
                                                bool = true;

                                            } catch (SmackException.NotConnectedException e1) {
                                                e1.printStackTrace();

                                            } catch (SmackException.NotLoggedInException e2) {
                                                e2.printStackTrace();

                                            } catch (XMPPException.XMPPErrorException e3) {
                                                e3.printStackTrace();

                                            } catch (SmackException.NoResponseException e4) {
                                                e4.printStackTrace();

                                            }

                                        } else {
                                            bool = false;
                                        }

                                        // bool = xmppManage.createRoster(data[0], data[1]);
                                        System.out.println(bool);


                                    }

                                }


                            }

                        }

                    }
                }

                return null;
            }


            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (adapterList.getListSelectFriend().size() == 0 && adapterList.getListSelectGroup().size() == 0) {
                    Toast.makeText(getApplicationContext(), "Please select at least one item.", Toast.LENGTH_LONG).show();
                    System.out.println("Error Add Roster");
                } else {
                    if (bool && !isGroup) {
                        Toast.makeText(getApplicationContext(), "add friend success", Toast.LENGTH_LONG).show();
                        BusProvider.getInstance().post("add_roster_complete");
                        finish();
                    } else if (bool && isGroup) {
                        System.out.println("fin");
                        Intent intent = new Intent();
                        intent.putParcelableArrayListExtra("data", inviteFriendObjects);
                        setResult(Activity.RESULT_OK, intent);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "Please check your internet connection and try again.", Toast.LENGTH_LONG).show();
                    }
                }
                ringProgressDialog.dismiss();

            }
        }.execute();


    }


    @Override
    public void onClick(View v) {
        if (v == btnAddFriend) {
            addFriend();

        }

    }

    @Override
    public void onRefresh() {
        getList("");
    }
}
