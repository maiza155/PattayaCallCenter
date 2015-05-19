package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.member.Adapter.AdapterListToUser;
import com.pattaya.pattayacallcenter.member.data.ForwardObject;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.UserDataObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.listforward.GetUserObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.listforward.ListUserData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CaseForwardToUser extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, TextWatcher {


    private ListView listView;
    private ArrayList<UserDataObject> listData; //list ในการเก็บข้อมูลของ DataShow

    private ArrayList<UserDataObject> selectedData;
    private AdapterListToUser adapterListToUser;
    private EditText txtSearch;
    private TextView emptyView;

    private ProgressBar progressBar;

    // widget
    private ImageButton btn;
    private TextView titleTextView;


    private RestAdapter webserviceConnector = WebserviceConnector.getInstance();
    private RestFulQueary adapterRest = webserviceConnector.create(RestFulQueary.class);
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_forward_to_user);

        setActionBar();
        init();

        listData = new ArrayList<>();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.container_list);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        txtSearch = (EditText) findViewById(R.id.search);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        emptyView = (TextView) findViewById(R.id.empty);
        listView = (ListView) findViewById(R.id.list);
        adapterListToUser = new AdapterListToUser(this, listData, selectedData);
        listView.setAdapter(adapterListToUser);
        listView.setEmptyView(emptyView);

        getUserList();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserList();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        txtSearch.addTextChangedListener(this);
    }

    void init() {
        selectedData = new ArrayList<>();

        Bundle data = getIntent().getExtras();
        if (data != null) {
            ArrayList<ForwardObject> arrayList = data.getParcelableArrayList("data");
            for (ForwardObject e : arrayList) {
                UserDataObject userDataObject = new UserDataObject();
                userDataObject.setDisplayName(e.getName());
                userDataObject.setUserId(e.getId());
                userDataObject.setUserImage(e.getImage());
                selectedData.add(userDataObject);
            }
        }
    }

    void setActionBar() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);

        titleTextView.setText(getResources().getString(R.string.member_case_list_forward_respond));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

    }

    void getUserList() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        adapterRest.getUserList(new GetUserObject(), new Callback<Response>() {
            @Override
            public void success(Response result, Response response2) {
                BufferedReader reader;
                StringBuilder sb = new StringBuilder();
                try {
                    reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String JsonConvertData = "{data:" + sb.toString() + "}";
                ListUserData listUserData = new Gson().fromJson(JsonConvertData, ListUserData.class);


                progressBar.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                adapterListToUser.resetAdapter(listUserData.getData());
                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void failure(RetrofitError error) {
                progressBar.setVisibility(View.GONE);
                if (adapterListToUser.getCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                }

                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    void getUserListSearch(String s) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        adapterRest.getUserList(new GetUserObject(s, s), new Callback<Response>() {
            @Override
            public void success(Response result, Response response2) {
                BufferedReader reader;
                StringBuilder sb = new StringBuilder();
                try {
                    reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String JsonConvertData = "{data:" + sb.toString() + "}";
                ListUserData listUserData = new Gson().fromJson(JsonConvertData, ListUserData.class);


                progressBar.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                adapterListToUser.resetAdapter(listUserData.getData());
                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void failure(RetrofitError error) {
                progressBar.setVisibility(View.GONE);
                if (adapterListToUser.getCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                }

                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_case_forward_to_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            ArrayList<ForwardObject> data = adapterListToUser.getSelected();
            if (data.size() > 0) {
                Intent i = new Intent();
                i.putParcelableArrayListExtra("data", data);
                setResult(Activity.RESULT_OK, i);
                finish();
            } else {
                Toast.makeText(getApplication(),
                        getResources().getString(R.string.please_select), Toast.LENGTH_SHORT)
                        .show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {

        getUserList();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        getUserListSearch(s.toString());
    }
}
