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
import com.pattaya.pattayacallcenter.member.Adapter.AdapterListToOrg;
import com.pattaya.pattayacallcenter.member.data.ForwardObject;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.casedata.listforward.GetOrgObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.listforward.ListOrgData;
import com.pattaya.pattayacallcenter.webservice.object.casedata.listforward.OrganizeObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CaseForwardToOrganize extends ActionBarActivity implements View.OnClickListener
        , SwipeRefreshLayout.OnRefreshListener, TextWatcher {

    private AdapterListToOrg adapterList;//Adapter List ที่เรากำหนดขึ้นเอง
    private ArrayList<OrganizeObject> listData;  //list ในการเก็บข้อมูลของ DataShow
    private ArrayList<OrganizeObject> selectedData;
    private ListView listView;
    private TextView emptyView;
    private EditText search;

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
        setContentView(R.layout.activity_case_forward_to_organize);

        listData = new ArrayList<>();
        selectedData = new ArrayList<>();

        init();
        setActionBar();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.container_list);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        /** /////////////////////////////////////////////////////////////////// */

        // init widget
        search = (EditText) findViewById(R.id.search);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        emptyView = (TextView) findViewById(R.id.empty);
        listView = (ListView) findViewById(R.id.list);

        //set listView in adapter
        adapterList = new AdapterListToOrg(this, listData, selectedData);
        listView.setAdapter(adapterList);
        listView.setEmptyView(emptyView);

        //query data from server
        getListOrg();


        // Click
        btn.setOnClickListener(this);
        emptyView.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        search.addTextChangedListener(this);
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

    void init() {
        Bundle data = getIntent().getExtras();
        if (data != null) {
            ArrayList<ForwardObject> arrayList = data.getParcelableArrayList("data");
            for (ForwardObject e : arrayList) {
                selectedData.add(new OrganizeObject(e.getName(), e.getName(), e.getId()));
            }
        }

    }

    public void getListOrg() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        adapterRest.getOrganizeList(new GetOrgObject(), new Callback<Response>() {
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
                ListOrgData listOrgData = new Gson().fromJson(JsonConvertData, ListOrgData.class);

                listData = new ArrayList();
                for (OrganizeObject e : listOrgData.getData()) {
                    listData.add(e);
                }
                progressBar.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                adapterList.resetAdapter(listData);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                progressBar.setVisibility(View.GONE);
                if (listData.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                }
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    public void getListOrgSearch(String s) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        adapterRest.getOrganizeList(new GetOrgObject(s), new Callback<Response>() {
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
                ListOrgData listOrgData = new Gson().fromJson(JsonConvertData, ListOrgData.class);

                listData = new ArrayList();
                for (OrganizeObject e : listOrgData.getData()) {
                    listData.add(e);
                }
                progressBar.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                adapterList.resetAdapter(listData);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                progressBar.setVisibility(View.GONE);
                if (listData.size() == 0) {
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
            ArrayList<ForwardObject> data = adapterList.getSelected();
            if (data.size() > 0) {
                Intent i = new Intent();
                i.putParcelableArrayListExtra("data", adapterList.getSelected());
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
    public void onClick(View v) {
        if (v == btn) {
            finish();
        } else if (v == emptyView) {
            getListOrg();
        }

    }

    @Override
    public void onRefresh() {
        getListOrg();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        getListOrgSearch(s.toString());
    }
}
