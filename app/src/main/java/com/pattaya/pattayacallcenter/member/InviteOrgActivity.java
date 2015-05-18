package com.pattaya.pattayacallcenter.member;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.PubsubObject;
import com.pattaya.pattayacallcenter.chat.XMPPManage;
import com.pattaya.pattayacallcenter.member.Adapter.AdapterListViewInviteOrg;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.GetInviteUserObject;
import com.pattaya.pattayacallcenter.webservice.object.UpdateResult;
import com.pattaya.pattayacallcenter.webservice.object.organize.OrgListData;
import com.pattaya.pattayacallcenter.webservice.object.organize.SendInviteObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class InviteOrgActivity extends ActionBarActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private final Gson gson = new Gson();
    private AdapterListViewInviteOrg adapterListViewInviteOrg; //Adapter List ที่เรากำหนดขึ้นเอง
    private List listDataAddFriend = new ArrayList(); //list ในการเก็บข้อมูลของ DataShow
    private ListView listViewDataAddFriend;
    private ImageButton btn;
    private TextView titleTextView;
    private Button btnInvite;
    private EditText txt_search;
    private RestAdapter webserviceConnector = WebserviceConnector.getInstance();
    private final RestFulQueary restqueary = webserviceConnector.create(RestFulQueary.class);
    private GetInviteUserObject getInviteUserObject;
    private SharedPreferences sp;
    private int userId;
    private String orgId;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        setActionBar();

        btnInvite = (Button) findViewById(R.id.btn_add_friend);
        txt_search = (EditText) findViewById(R.id.search);
        getInviteUserObject = new GetInviteUserObject("");
        btnInvite.setText(getResources().getString(R.string.send_invite));

        sp = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        orgId = sp.getString(MasterData.SHARED_USER_ORGANIZE, null);
        userId = sp.getInt(MasterData.SHARED_USER_USER_ID, -10);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.container_list);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        adapterListViewInviteOrg = new AdapterListViewInviteOrg(this, listDataAddFriend);
        listViewDataAddFriend = (ListView) findViewById(R.id.list_inviteFriend);
        listViewDataAddFriend.setAdapter(adapterListViewInviteOrg);
        listViewDataAddFriend.setEmptyView(findViewById(R.id.empty));


        btn.setOnClickListener(this);
        btnInvite.setOnClickListener(this);
        getInviteName();
        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getInviteUserObject.setUsername(String.valueOf(s));
                getInviteName();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    void setActionBar() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_back, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        btn.setPadding(0, 0, 0, 0);
        titleTextView.setText(getResources().getString(R.string.send_invite));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    void getInviteName() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {

                restqueary.getInviteUser(getInviteUserObject, new Callback<Response>() {
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
                        final OrgListData orgobject = new Gson().fromJson(JsonConvertData, OrgListData.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapterListViewInviteOrg.resetAdapter(orgobject.getData());
                                mSwipeRefreshLayout.setRefreshing(false);

                            }
                        });

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });

                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.cant_connect_server), Toast.LENGTH_SHORT)
                                .show();

                    }
                });
                return null;
            }
        }.execute();


    }

    void sendInvite() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.load_data), getResources().getString(R.string.please_wait), true);
        final SendInviteObject sendInviteObject = new SendInviteObject();
        sendInviteObject.setUserList(adapterListViewInviteOrg.getListSelect());
        sendInviteObject.setOrgId(orgId);
        sendInviteObject.setInviteByUserId(userId);
        String json2 = gson.toJson(sendInviteObject);
        System.out.println(json2);

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                restqueary.sendInvite(sendInviteObject, new Callback<UpdateResult>() {
                    @Override
                    public void success(UpdateResult updateResult, Response response) {
                        System.out.println("updateResult = [" + updateResult.getResult() + "], response = [" + response + "]");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ringProgressDialog.dismiss();
                            }
                        });
                        if (updateResult.getResult()) {
                            for (String e : adapterListViewInviteOrg.getListSelectJid()) {
                                if (!e.isEmpty()) {
                                    PubsubObject pub = new PubsubObject();
                                    pub.setUsername(e.split("@")[0]);
                                    pub.setAction("org");
                                    pub.setTitle("คำเชิญเข้ากลุ่ม");
                                    XMPPManage.getInstance().new TaskSendNotify(pub).execute();
                                }

                            }


                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.update_data_success), Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Please try again", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println("error = [" + error + "]");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ringProgressDialog.dismiss();
                            }
                        });

                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.cant_connect_server), Toast.LENGTH_SHORT)
                                .show();
                    }
                });

                return null;
            }
        }.execute();

    }


    @Override
    public void onClick(View v) {
        if (v == btn) {
            finish();
        } else if (v == btnInvite) {
            if (adapterListViewInviteOrg.getListSelect().size() == 0) {
                Toast.makeText(getApplicationContext(), "Please select at least one item.", Toast.LENGTH_LONG).show();
            } else {
                sendInvite();
            }
        }

    }


    @Override
    public void onRefresh() {
        getInviteName();
    }
}
