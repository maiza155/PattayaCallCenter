package com.pattaya.pattayacallcenter.guest;


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
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.chat.XMPPManage;
import com.pattaya.pattayacallcenter.chat.XMPPService;
import com.pattaya.pattayacallcenter.customview.EmptyRecyclerView;
import com.pattaya.pattayacallcenter.customview.SlideMenuManage;
import com.pattaya.pattayacallcenter.guest.CaseList.ListAdapter;
import com.pattaya.pattayacallcenter.share.AlertSettingActivity;
import com.pattaya.pattayacallcenter.share.ChangePasswordActivity;
import com.pattaya.pattayacallcenter.share.LoginActivity;
import com.pattaya.pattayacallcenter.share.ProfileActivity;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseListDataObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseListObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.GetCaseListData;
import com.pattaya.pattayacallcenter.webservice.object.organize.GetInviteOrgObject;
import com.readystatesoftware.viewbadger.BadgeView;
import com.squareup.otto.Subscribe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class CaseListActivity extends ActionBarActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    EmptyRecyclerView mRecyclerView;
    ArrayList mListItem;
    LinearLayoutManager mLayoutManager;
    ListAdapter mAdapter;
    LinearLayout mEmptyView;
    View mMenuSetting;
    ImageButton btn;
    Button btnOwn, btnExit, btnOranize, btnPass, btnAlert, btnAdd;
    Intent intent;
    SlideMenuManage mSliderMange;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    RestAdapter webserviceConnector = WebserviceConnector.getInstanceCase();
    RestFulQueary adapterRest = null;

    RestAdapter webserviceConnector2 = WebserviceConnector.getInstance();
    final RestFulQueary queary = webserviceConnector2.create(RestFulQueary.class);
    int userId;
    String token;
    String clientId;
    ProgressDialog ringProgressDialog;
    int TAG_ORGANIZE = 808;
    BadgeView badge, badgeInSlide;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_list);
        BusProvider.getInstance().register(this);

        setActionBar();

        init();

        mRecyclerView = (EmptyRecyclerView) findViewById(R.id.list);
        mEmptyView = (LinearLayout) findViewById(R.id.empty);

        btnAdd = (Button) findViewById(R.id.add_case);

        mMenuSetting = findViewById(R.id.menu_slide);

        /**  menu Setting **/
        btnOwn = (Button) findViewById(R.id.btn_own);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnAlert = (Button) findViewById(R.id.btn_alert);
        btnOranize = (Button) findViewById(R.id.btn_organize);
        btnPass = (Button) findViewById(R.id.btn_pass);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.container_list);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        mListItem = new ArrayList();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setEmptyView(mEmptyView);


        mAdapter = new ListAdapter(mListItem, this);
        mRecyclerView.setAdapter(mAdapter);


        mSliderMange = new SlideMenuManage(mMenuSetting, btn, this);

        btnAdd.setOnClickListener(this);
        btnOwn.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnAlert.setOnClickListener(this);
        btnPass.setOnClickListener(this);
        btnOranize.setOnClickListener(this);
        adapterRest = webserviceConnector.create(RestFulQueary.class);

        ringProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.load_data), getResources().getString(R.string.please_wait), true);
        ringProgressDialog.setCancelable(false);
        new TaskQueary().execute();
        getListData();


        badgeInSlide = new BadgeView(this, btnOranize);
        badgeInSlide.setText("N");
        badgeInSlide.setTextSize(12);
        getOrg();

    }


    void init() {
        sp = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        userId = sp.getInt(MasterData.SHARED_USER_USER_ID, -10);
        sp.edit().putBoolean(MasterData.SHARED_IS_MEMBER, false).commit();
        sp = getSharedPreferences("APP_CONFIG", Context.MODE_PRIVATE);
        token = sp.getString("TOKEN", "null");
        clientId = sp.getString("CLIENT_ID", "null");
    }

    void setActionBar() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_case_list, null);
        TextView titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        titleTextView.setText(getResources().getString(R.string.case_complain));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        badge = new BadgeView(this, v.findViewById(R.id.badge));
        badge.setBackgroundResource(R.drawable.custom_budget);
        badge.setGravity(Gravity.CENTER);
        badge.setText("N");
        badge.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
        badge.setTextSize(12);
    }

    @Subscribe
    public void updateListData(String s) {
        if (s.matches("update_case_list")) {
            getListData();
        } else if (s.matches("case_count")) {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        } else if (s.matches("org_update")) {
            badge.show();
            badgeInSlide.show();
        }

    }


    void getListData() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        GetCaseListData getCaseListData = new GetCaseListData();
        getCaseListData.setFilterType(1);
        getCaseListData.setUserId(userId);
        getCaseListData.setAccessToken(token);
        adapterRest.getCaseList(getCaseListData, new Callback<Response>() {
            @Override
            public void success(Response result, Response response2) {
                DatabaseChatHelper.init().clearCaseTable();
                BufferedReader reader;
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
                System.out.println(JsonConvertData);
                CaseListObject caseListObject = new Gson().fromJson(JsonConvertData, CaseListObject.class);
                for (CaseListDataObject e : caseListObject.getData()) {
                    DatabaseChatHelper.init().addCase(e);
                    //System.out.println(e.getCaseName());
                }
                new TaskQueary().execute();


                ringProgressDialog.dismiss();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");
                ringProgressDialog.dismiss();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAG_ORGANIZE) {
            if (resultCode == Activity.RESULT_OK) {
                Boolean bool = data.getBooleanExtra("state", true);
                if (bool) {
                    finish();
                } else {
                    badge.hide();
                    badgeInSlide.hide();
                }


            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_case_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            intent = new Intent(getApplicationContext(), CaseAddAndEditActivity.class);
            startActivity(intent);
            mSliderMange.stateShowMenu(mSliderMange.SETTING_MENU_HIDE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if (btnAdd == v) {
            intent = new Intent(getApplicationContext(), CaseAddAndEditActivity.class);
            startActivity(intent);
            mSliderMange.stateShowMenu(mSliderMange.SETTING_MENU_HIDE);
        } else if (btnOwn == v) {
            intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            mSliderMange.stateShowMenu(mSliderMange.SETTING_MENU_HIDE);
        } else if (btnExit == v) {
            ringProgressDialog = ProgressDialog.show(this, null, getResources().getString(R.string.please_wait), true);
            try {
                XMPPManage.getInstance().disConnect();
                editor = sp.edit();
                editor.putString("TOKEN", null);
                editor.commit();
                CaseListActivity.this.stopService(new Intent(CaseListActivity.this, XMPPService.class));
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                LoginManager.getInstance().logOut();
                ringProgressDialog.dismiss();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                ringProgressDialog.dismiss();
            }

        } else if (btnAlert == v) {
            intent = new Intent(getApplicationContext(), AlertSettingActivity.class);
            startActivity(intent);
            mSliderMange.stateShowMenu(mSliderMange.SETTING_MENU_HIDE);
        } else if (btnPass == v) {
            intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
            startActivity(intent);
            mSliderMange.stateShowMenu(mSliderMange.SETTING_MENU_HIDE);
        } else if (btnOranize == v) {
            intent = new Intent(getApplicationContext(), OrganizeActivity.class);
            startActivityForResult(intent, TAG_ORGANIZE);
            mSliderMange.stateShowMenu(mSliderMange.SETTING_MENU_HIDE);

        }

    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        getListData();

    }


    void getOrg() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                return null;
            }
        }.execute();
        queary.getInviteOrg(userId, new Callback<GetInviteOrgObject>() {
            @Override
            public void success(GetInviteOrgObject getInviteOrgObject, Response response) {
                ringProgressDialog.dismiss();
                System.out.println("getInviteOrgObject = [" + getInviteOrgObject.getOrgId() + "], response = [" + response + "]");
                if (getInviteOrgObject.getOrgId() > 0) {
                    badge.show();
                    badgeInSlide.show();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    class TaskQueary extends AsyncTask<Void, Void, List<CaseListDataObject>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });

        }

        @Override
        protected List<CaseListDataObject> doInBackground(Void... params) {
            return DatabaseChatHelper.init().getCaseList();

        }

        @Override
        protected void onPostExecute(List<CaseListDataObject> caseListDataObjects) {
            super.onPostExecute(caseListDataObjects);
            mAdapter.resetAdapter(caseListDataObjects);
            mSwipeRefreshLayout.setRefreshing(false);


        }
    }
}

