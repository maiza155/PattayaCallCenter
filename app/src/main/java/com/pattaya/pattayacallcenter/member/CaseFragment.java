package com.pattaya.pattayacallcenter.member;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.chat.XMPPManageOfficial;
import com.pattaya.pattayacallcenter.chat.XMPPServiceOfficial;
import com.pattaya.pattayacallcenter.customview.SlideMenuManage;
import com.pattaya.pattayacallcenter.member.Adapter.AdapterListCaseActionBar;
import com.pattaya.pattayacallcenter.member.Adapter.AdpterListCase;
import com.pattaya.pattayacallcenter.member.dummy.DataPopUp;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.OfficialObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseListMemberData;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseListMemberObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.GetCaseListData;
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


public class CaseFragment extends Fragment implements View.OnClickListener
        , SwipeRefreshLayout.OnRefreshListener
        , TextWatcher {


    static CaseFragment fragment;
    View mSlideMenuImage;
    SlideMenuManage mSlideMenuManage;
    EditText txtSearch;
    View root;
    LayoutInflater inflater;
    ImageButton btnAdvanceSearch;
    Button btnReport;
    Button btnCreate;
    AdpterListCase adpterListCase;
    ListView list;
    SharedPreferences sp, spConfig;
    SharedPreferences.Editor editor;
    RestAdapter webserviceConnector = WebserviceConnector.getInstanceCase();
    RestFulQueary adapterRest = null;


    RestAdapter webserviceConnectorUser = WebserviceConnector.getInstance();
    RestFulQueary adapterRestUser = webserviceConnectorUser.create(RestFulQueary.class);

    String userName;
    String userImage;
    int userId;
    Boolean isOfficial;
    String token;
    String clientId;
    List<CaseListMemberData> dataList = new ArrayList<>();
    ArrayList<DataPopUp> listUsers = new ArrayList();
    View btnClick;
    ProgressBar progressBar;
    TextView txtEmpty;
    // widget
    View btn;
    TextView titleTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public CaseFragment() {
        // Required empty public constructor
    }

    public static CaseFragment newInstance() {
        if (fragment == null) {
            fragment = new CaseFragment();
            BusProvider.getInstance().register(fragment);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        listUsers = new ArrayList();

        sp = getActivity().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        userId = sp.getInt(MasterData.SHARED_USER_USER_ID, -10);
        userImage = sp.getString(MasterData.SHARED_USER_IMAGE, "No image");
        userName = sp.getString(MasterData.SHARED_USER_DISPLAY_NAME, "Unknown");
        isOfficial = sp.getBoolean(MasterData.SHARED_IS_OFFICIAL, false);

        spConfig = getActivity().getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        token = spConfig.getString("TOKEN", "null");
        clientId = spConfig.getString("CLIENT_ID", "null");
        adapterRest = webserviceConnector.create(RestFulQueary.class);
        listUsers.add(new DataPopUp(userImage, userName));
        getUserOfficialData();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        this.inflater = inflater;
        root = inflater.inflate(R.layout.fragment_case, container, false);
        btnReport = (Button) root.findViewById(R.id.btn_report);
        btnCreate = (Button) root.findViewById(R.id.btn_create);
        btnAdvanceSearch = (ImageButton) root.findViewById(R.id.btn_advanceSearch);
        txtSearch = (EditText) root.findViewById(R.id.search);
        // txtSearch.clearFocus();


        list = (ListView) root.findViewById(R.id.list);
        progressBar = (ProgressBar) root.findViewById(R.id.progress);
        txtEmpty = (TextView) root.findViewById(R.id.empty);
        list.setEmptyView(txtEmpty);


        //////////////////////////////////////////////////////////////////////////////////////
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.container_list);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        adpterListCase = new AdpterListCase(dataList, getActivity());

        list.setAdapter(adpterListCase);
        // btn.setOnClickListener(this);

        //////////////////////////////////////////////////////////////////////////////////////
        mSlideMenuImage = root.findViewById(R.id.menu_slide);
        mSlideMenuManage = new SlideMenuManage(mSlideMenuImage, getActivity());


        btnReport.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        btnAdvanceSearch.setOnClickListener(this);
        txtEmpty.setOnClickListener(this);
        if (dataList.size() == 0) {
            progressBar.setVisibility(View.VISIBLE);
            getCaseList("");
        }


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        txtSearch.addTextChangedListener(this);
    }

    @Subscribe
    public void updateListData(String s) {
        if (s.matches("update_case_list")) {
            getCaseList("");
        }

    }

    void setActionBar(ActionBarActivity actionBar) {
        /** Set Title Center Actionbar */
        LayoutInflater inflater = LayoutInflater.from(actionBar);
        View v = inflater.inflate(R.layout.custom_actionbar_case_fragment, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = v.findViewById(R.id.add_left_menu);
        btn.setOnClickListener(this);
        titleTextView.setText(getResources().getString(R.string.menu_case));
        titleTextView.setPadding(40, 0, 0, 0);
        actionBar.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.getSupportActionBar().setCustomView(v);
        actionBar.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        actionBar.getSupportActionBar().setDisplayShowHomeEnabled(false);

        ImageView rootView = (ImageView) btn;
        Log.e("isOfficial", "" + isOfficial);
        if (isOfficial) {
            if (listUsers.size() == 2) {
                Glide.with(this)
                        .load(listUsers.get(1).getImage())
                        .override(300, 300)
                        .fitCenter()
                        .into(rootView);
            } else {
                getUserOfficialData();
            }

        } else {

            Glide.with(this)
                    .load(listUsers.get(0).getImage())
                    .override(300, 300)
                    .fitCenter()
                    .into(rootView);
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        setActionBar((ActionBarActivity) getActivity());
        inflater.inflate(R.menu.menu_case_list, menu);
        //Button locButton = (Button) menu.findItem(R.id.action_add).getActionView();

        // mSlideMenuManage =  new SlideMenuManage(mSlideMenuImage,locButton,getActivity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        switch (item.getItemId()) {
            case R.id.action_add:
                mSlideMenuManage.eventShow();
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
        //int id = item.getItemId();

    }


    @Override
    public void onClick(View v) {
        if (v == btnClick) {
            Intent intent = new Intent(getActivity(), CaseChatMemberActivity.class);
            getActivity().startActivity(intent);

        } else if (v == btnCreate) {
            Intent intent = new Intent(getActivity(), CaseAddAndEditActivity.class);
            getActivity().startActivity(intent);

        } else if (v == btnReport) {
            Intent intent = new Intent(getActivity(), com.pattaya.pattayacallcenter.guest.CaseAddAndEditActivity.class);
            getActivity().startActivity(intent);

        } else if (v == btnAdvanceSearch) {
            Intent intent = new Intent(getActivity(), CaseAdvanceSearchActivity.class);
            getActivity().startActivity(intent);

        } else if (v == btn) {
            displayPopupWindow(v);
        } else if (v == txtEmpty) {
            getCaseList("");
        }
        mSlideMenuManage.stateShowMenu(mSlideMenuManage.SETTING_MENU_HIDE);
    }


    private void displayPopupWindow(final View anchorView) {
        final PopupWindow popup = new PopupWindow(getActivity());
        View layout = this.inflater.inflate(R.layout.custom_list_popup, null);
        ListView lv = (ListView) layout.findViewById(R.id.listview);
        final AdapterListCaseActionBar adapterListCaseActionBar = new AdapterListCaseActionBar(listUsers, getActivity());
        lv.setAdapter(adapterListCaseActionBar);

        adapterListCaseActionBar.SetOnItemClickListener(new AdapterListCaseActionBar.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                System.out.println("position :" + position);
                DataPopUp dataPopUp = (DataPopUp) adapterListCaseActionBar.getItem(position);
                ImageView rootView = (ImageView) anchorView;
                SharedPreferences.Editor editor = sp.edit();
                if (position == 0) {
                    editor.putBoolean(MasterData.SHARED_IS_OFFICIAL, false);
                    editor.commit();
                    isOfficial = false;
                    System.out.println(dataPopUp.getImage());
                    XMPPManageOfficial.getInstance().disConnect();

                } else {
                    editor.putBoolean(MasterData.SHARED_IS_OFFICIAL, true);
                    editor.commit();
                    isOfficial = true;
                    getActivity().startService(new Intent(getActivity(), XMPPServiceOfficial.class));
                    System.out.println(dataPopUp.getImage());
                }
                Glide.with(v.getContext())
                        .load(dataPopUp.getImage())
                        .override(300, 300)
                        .fitCenter()
                        .into(rootView);

                popup.dismiss();
            }
        });


        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        //popup.setCanceledOnTouchOutside(false);
        // Show anchored to button
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(anchorView);

    }


    void getCaseList(String s) {
        txtEmpty.setVisibility(View.GONE);
        GetCaseListData getCaseListData = new GetCaseListData();
        getCaseListData.setFilterType(2);
        getCaseListData.setUserId(userId);
        getCaseListData.setAccessToken(token);
        getCaseListData.setTextSearch(s);
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
                CaseListMemberObject caseListObject = new Gson().fromJson(JsonConvertData, CaseListMemberObject.class);

                dataList = caseListObject.getData();
                progressBar.setVisibility(View.GONE);
                txtEmpty.setVisibility(View.VISIBLE);


                adpterListCase.resetAdpter(dataList);
                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void failure(RetrofitError error) {
                progressBar.setVisibility(View.GONE);
                if (adpterListCase.getCount() == 0) {
                    txtEmpty.setVisibility(View.VISIBLE);
                }

                System.out.println("error = [" + error + "]");
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    void getUserOfficialData() {
        adapterRestUser.getUserOfficialData(userId, new Callback<OfficialObject>() {
            @Override
            public void success(OfficialObject officialObject, Response response) {
                if (listUsers.size() == 1) {
                    listUsers.add(new DataPopUp(officialObject.getUserImage(), officialObject.getDisplayname()));
                    System.out.println("officialObject = [" + officialObject + "], response = [" + response + "]");
                    ImageView rootView = (ImageView) btn;
                    if (rootView != null && isOfficial) {
                        Glide.with(getActivity())
                                .load(listUsers.get(1).getImage())
                                .override(300, 300)
                                .fitCenter()
                                .into(rootView);

                    }
                }

            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");
            }
        });
    }


    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        getCaseList("");
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
        getCaseList(text);

    }
}
