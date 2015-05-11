package com.pattaya.pattayacallcenter.member;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.guest.AdapterListViewCaseResult;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseResultData;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseResultListObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.GetComplainObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CaseResultMemberActivity extends ActionBarActivity {


    private ListView listView;
    // widget
    private ImageButton btn;
    private TextView titleTextView;
    private int caseId, complainId;
    private AdapterListViewCaseResult adapterListViewCaseResult;
    private SharedPreferences sp;
    private String token;
    private String clientId;

    private RestAdapter webserviceConnector = WebserviceConnector.getInstanceCase();
    private RestFulQueary adapterRest = null;
    private ArrayList<CaseResultData> listDataresult = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_result_member);


        adapterRest = webserviceConnector.create(RestFulQueary.class);
        listView = (ListView) findViewById(R.id.listview);
        adapterListViewCaseResult = new AdapterListViewCaseResult(this, listDataresult);
        listView.setAdapter(adapterListViewCaseResult);
        listView.setEmptyView(findViewById(R.id.empty));

        init();
        setActionBar();

        if (caseId != 0) {
            getResult();
        } else {
            System.out.println("No data");
        }


    }


    void init() {
        Bundle data = getIntent().getExtras();
        caseId = data.getInt("id");
        complainId = data.getInt("complainid");

        sp = getSharedPreferences("APP_CONFIG", Context.MODE_PRIVATE);
        token = sp.getString("TOKEN", "null");
        clientId = sp.getString("CLIENT_ID", "null");
    }

    void setActionBar() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);

        titleTextView.setText(getResources().getString(R.string.menu_case_member_result));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
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

    void getResult() {
        System.out.println(caseId);
        GetComplainObject getComplainObject = new GetComplainObject();
        getComplainObject.setAccessToken(token);
        getComplainObject.setPrimaryKeyId(caseId);
        getComplainObject.setClientId(clientId);

        adapterRest.getTaskList(getComplainObject, new Callback<Response>() {
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
                System.out.println(JsonConvertData);
                CaseResultListObject caseTypeObject = new Gson().fromJson(JsonConvertData, CaseResultListObject.class);
                adapterListViewCaseResult.resetAdapter(caseTypeObject.getData());


            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");

            }
        });

    }
}
