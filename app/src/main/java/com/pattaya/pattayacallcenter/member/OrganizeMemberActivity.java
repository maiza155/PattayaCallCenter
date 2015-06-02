package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.member.Adapter.AdapterListMemberOrganize;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.organize.GetOrgObject;
import com.pattaya.pattayacallcenter.webservice.object.organize.OrgData;
import com.pattaya.pattayacallcenter.webservice.object.organize.OrgDetail;
import com.pattaya.pattayacallcenter.webservice.object.organize.OrgListData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OrganizeMemberActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {
    private AdapterListMemberOrganize adapterListViewOrganizeMember; //Adapter List ที่เรากำหนดขึ้นเอง
    private List<OrgDetail> listDataOrganizeMember = new ArrayList(); //list ในการเก็บข้อมูลของ DataShow
    private ListView listViewDataOrganizeMember;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView txt_count;

    private Intent intent;
    private ImageButton btn;
    private TextView titleTextView;
    private RestAdapter webserviceConnector = WebserviceConnector.getInstance();
    private final RestFulQueary restqueary = webserviceConnector.create(RestFulQueary.class);
    private SharedPreferences spConfig;
    private SharedPreferences sp;

    // //// ///////////////////////////////////////////////////////////////////
    private String orgId, token, clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organize_member);
        setActionBar();
        init();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.container_list);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        txt_count = (TextView) findViewById(R.id.txt_count);
        listViewDataOrganizeMember = (ListView) findViewById(R.id.list_organize);
        adapterListViewOrganizeMember = new AdapterListMemberOrganize(getBaseContext(), listDataOrganizeMember, txt_count);
        listViewDataOrganizeMember.setAdapter(adapterListViewOrganizeMember);
        listViewDataOrganizeMember.setEmptyView(findViewById(R.id.empty));
        getUsersOrganize();
        setClickLisener();

    }

    void init() {
        spConfig = getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        sp = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        orgId = sp.getString(MasterData.SHARED_USER_ORGANIZE, null);
        token = spConfig.getString(MasterData.SHARED_CONFIG_TOKEN, null);
        clientId = spConfig.getString(MasterData.SHARED_CONFIG_CLIENT_ID, null);
    }

    void setActionBar() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);

        titleTextView.setText("สำนักงาน");
        titleTextView.setPadding(40, 0, 0, 0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void getUsersOrganize() {

        if (orgId != null) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
            final GetOrgObject getOrgObject = new GetOrgObject();
            getOrgObject.setAccessToken(token);
            getOrgObject.setClientId(clientId);
            getOrgObject.setOrgId(orgId);

            restqueary.getUserOrg(getOrgObject, new Callback<Response>() {
                @Override
                public void success(Response result, Response response) {
                    listDataOrganizeMember = new ArrayList();

                    //Try to get response body
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
                    System.out.println(JsonConvertData);
                    OrgListData orgobject = new Gson().fromJson(JsonConvertData, OrgListData.class);
                    listDataOrganizeMember = orgobject.getData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Activity activity = OrganizeMemberActivity.this;
                            if (activity != null) {
                                adapterListViewOrganizeMember.resetAdapter(listDataOrganizeMember);
                                mSwipeRefreshLayout.setRefreshing(false);
                            }

                        }
                    });

                }

                @Override
                public void failure(RetrofitError error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Activity activity = OrganizeMemberActivity.this;
                            if (activity != null) {

                                mSwipeRefreshLayout.setRefreshing(false);
                            }

                        }
                    });
                }
            });

            restqueary.getOrganizeData(getOrgObject, new Callback<OrgData>() {
                @Override
                public void success(final OrgData orgData, Response response) {
                    System.out.println(orgId);
                    if (orgData.getDisplayname() != null && !orgData.getDisplayname().isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Activity activity = OrganizeMemberActivity.this;
                                if (activity != null) {
                                    titleTextView.setText(orgData.getDisplayname());
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }

                            }
                        });

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Activity activity = OrganizeMemberActivity.this;
                            if (activity != null) {

                                mSwipeRefreshLayout.setRefreshing(false);
                            }

                        }
                    });

                }
            });


        }


    }


    private void setClickLisener() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_organize_member, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_invite) {
            intent = new Intent(getApplicationContext(), InviteOrgActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        getUsersOrganize();

    }
}
