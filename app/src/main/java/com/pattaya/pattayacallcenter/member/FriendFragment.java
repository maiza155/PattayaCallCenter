package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.pattaya.pattayacallcenter.member.Adapter.ExpandableListAdapterFriend;
import com.pattaya.pattayacallcenter.member.data.BusGetFriendObject;
import com.readystatesoftware.viewbadger.BadgeView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FriendFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, TextWatcher {

    private static FriendFragment fragment = null;
    ArrayList<Users> childListGroup = new ArrayList<>(); //list ในการเก็บข้อมูลของ DataShow
    ArrayList<Users> childListFriend = new ArrayList<>(); //list ในการเก็บข้อมูลของ DataShow
    ArrayList<Users> childListFavorite = new ArrayList<>(); //list ในการเก็บข้อมูลของ DataShow
    private ImageButton btn;
    private TextView titleTextView;
    private Intent intent;
    private ExpandableListView expandableListView;
    private ExpandableListAdapterFriend expandableListAdapterFriend;
    private List<String> groupList;
    private Map<String, List<Users>> laptopCollection;
    private EditText search;

    private String jid;
    private BadgeView badge;


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Boolean isFirst;
    private Boolean isStart = true;
    private int initBudge = 0;


    public FriendFragment() {
        // Required empty public constructor

    }

    public static FriendFragment newInstance() {
        if (fragment == null) {
            fragment = new FriendFragment();
            BusProvider.getInstance().register(fragment);

        }
        return fragment;
    }

    public void setActionBar(ActionBarActivity actionBar) {
        /** Set Title Center Actionbar */
        LayoutInflater inflater = LayoutInflater.from(actionBar);
        View v = inflater.inflate(R.layout.custom_actionbar_add_friend, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_right_menu);
        btn.setMinimumHeight(45);
        btn.setPadding(0, 0, 15, 0);
        titleTextView.setText(getResources().getString(R.string.friend));
        actionBar.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.getSupportActionBar().setCustomView(v);
        actionBar.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        actionBar.getSupportActionBar().setDisplayShowHomeEnabled(false);

        badge = new BadgeView(actionBar, v.findViewById(R.id.badge));
        badge.setBackgroundResource(R.drawable.custom_budget);
        badge.setGravity(Gravity.CENTER);
        badge.setTextSize(15);
        if (initBudge > 9) {
            badge.setText("N");
            badge.show();
        } else if (initBudge == 0) {
            badge.hide();
        } else {
            badge.setText("" + (initBudge));
            badge.show();
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //XMPPManage.getInstance().sendMessageNotify();
                intent = new Intent(getActivity(), FriendRequestActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }


    @Subscribe
    public void onSampleEvent(String event) {
        Activity activity = getActivity();
        if (activity != null) {
            if (event.matches("friendfragment")) {
                new queryTask().execute();
            } else if (event.matches("add_roster_complete")) {
                // new TaskGetFriend(getActivity()).execute();
                new queryTask().execute();
            } else if (event.matches("invited_update")) {
                if (badge != null) {
                    int oldBadge = (badge.getText().toString().isEmpty()) ? 0 : Integer.parseInt(badge.getText().toString());
                    initBudge = oldBadge + 1;
                    if (initBudge > 9) {
                        badge.setText("N");
                        badge.show();
                    } else if (initBudge == 0) {
                        badge.hide();
                    } else {
                        badge.setText("" + (initBudge));
                        badge.show();
                    }
                }
            }
        }

    }


    @Subscribe
    public void onSampleEvent(BusGetFriendObject busGetFriendObject) {
        Activity activity = getActivity();
        if (activity != null) {
            initBudge = busGetFriendObject.getCount();
            System.out.println("recive Data " + busGetFriendObject.getIsOk());
            System.out.println("recive Data " + busGetFriendObject.getCount());
            if (busGetFriendObject.getIsOk()) {
                if (badge != null) {
                    if (initBudge > 9) {
                        badge.setText("N");
                        badge.show();
                    } else if (initBudge == 0) {
                        badge.hide();
                    } else {
                        badge.setText("" + (initBudge));
                        badge.show();
                    }
                }

                new queryTask().execute();
            } else {
                Toast.makeText(Application.getContext(),
                        getResources().getString(R.string.cant_connect_server), Toast.LENGTH_SHORT)
                        .show();
            }
            mSwipeRefreshLayout.setRefreshing(false);

        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        SharedPreferences spUser = getActivity().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        SharedPreferences sp = getActivity().getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        isFirst = sp.getBoolean(MasterData.SHARED_CONFIG_IS_FIRST, false);
        jid = spUser.getString(MasterData.SHARED_USER_JID, null);

        if (isFirst) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(MasterData.SHARED_CONFIG_IS_FIRST, false);
            editor.commit();
        }
        new TaskGetFriend(getActivity());


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);


        search = (EditText) view.findViewById(R.id.search);
        expandableListView = (ExpandableListView) view.findViewById(R.id.list);
        //progressBar = (ProgressBar) view.findViewById(R.id.progressbar);


        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.container_list);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;


        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandableListView.setIndicatorBounds(width - getDipsFromPixel(30), width
                    - getDipsFromPixel(10));
        } else {
            expandableListView.setIndicatorBoundsRelative(width - getDipsFromPixel(30), width
                    - getDipsFromPixel(10));
        }

        groupList = new ArrayList<>();
        groupList.add(getResources().getString(R.string.favorite));
        groupList.add(getResources().getString(R.string.group));
        groupList.add(getResources().getString(R.string.friend));


        laptopCollection = new HashMap<>();
        laptopCollection.put(groupList.get(0), childListFavorite);
        laptopCollection.put(groupList.get(1), childListGroup);
        laptopCollection.put(groupList.get(2), childListFriend);

        expandableListAdapterFriend = new ExpandableListAdapterFriend(getActivity(), groupList, laptopCollection);
        expandableListView.setAdapter(expandableListAdapterFriend);

        if (isStart) {
            new queryTask().execute();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        search.addTextChangedListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        setActionBar((ActionBarActivity) getActivity());

    }

    public int getDipsFromPixel(float pixels) {

        final float scale = getResources().getDisplayMetrics().density;

        return (int) (pixels * scale + 0.5f);
    }


    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        new TaskGetFriend(getActivity());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString();
        new querySreachTask(text).execute();
    }


    class queryTask extends AsyncTask<Void, Void, Boolean> {
        ArrayList<Users> arrUsers;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isStart = false;
            childListGroup = new ArrayList();
            childListFriend = new ArrayList<>();
            childListFavorite = new ArrayList<>();
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            arrUsers = DatabaseChatHelper.init().getUsers();
            System.out.println("Data Comlete >>>>" + arrUsers);

            for (Users users : arrUsers) {
                System.out.println("Data Comlete >>>>" + users.getName());
                if (users.getType() == Users.TYPE_FRIEND) {
                    childListFriend.add(users);
                    if (users.getFavorite()) {
                        childListFavorite.add(users);
                    }
                } else if (users.getType() == Users.TYPE_GROUP) {
                    String temp = users.getJid().split("@")[0];
                    String isCase = temp.split("-")[0];
                    if (!isCase.matches("case") && !isCase.matches("complaint")) {
                        childListGroup.add(users);
                    }

                } else if (users.getType() == Users.TYPE_NOT_FRIEND) {


                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            Activity activity = getActivity();
            if (activity != null) {
                expandableListAdapterFriend.resetAdapter(groupList.get(0), childListFavorite);
                expandableListAdapterFriend.resetAdapter(groupList.get(1), childListGroup);
                expandableListAdapterFriend.resetAdapter(groupList.get(2), childListFriend);

                expandableListView.expandGroup(1);
                expandableListView.expandGroup(2);
                expandableListView.expandGroup(0);

                mSwipeRefreshLayout.setRefreshing(false);

            }


        }

    }

    class querySreachTask extends AsyncTask<Void, Void, Boolean> {
        ArrayList<Users> arrUsers;
        ArrayList<Users> arrFriend;
        ArrayList<Users> arrGroup;
        ArrayList<Users> arrFavor;
        String search;

        querySreachTask(String search) {
            this.search = search;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arrFriend = new ArrayList<>();
            arrGroup = new ArrayList<>();
            arrFavor = new ArrayList<>();
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            arrUsers = DatabaseChatHelper.init().searchUsers(search);
            for (Users users : arrUsers) {
                if (users.getType() == Users.TYPE_FRIEND) {
                    arrFriend.add(users);
                    if (users.getFavorite()) {
                        arrFavor.add(users);
                    }
                } else if (users.getType() == Users.TYPE_GROUP) {
                    String temp = users.getJid().split("@")[0];
                    String isCase = temp.split("-")[0];
                    if (!isCase.matches("case") && !isCase.matches("complaint")) {
                        arrGroup.add(users);
                    }

                } else if (users.getType() == Users.TYPE_NOT_FRIEND) {


                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            Activity activity = getActivity();
            if (activity != null) {
                expandableListAdapterFriend.resetAdapter(groupList.get(0), arrFavor);
                expandableListAdapterFriend.resetAdapter(groupList.get(1), arrGroup);
                expandableListAdapterFriend.resetAdapter(groupList.get(2), arrFriend);
                expandableListView.expandGroup(1);
                expandableListView.expandGroup(2);
                expandableListView.expandGroup(0);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }


}
