package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
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
import android.widget.AbsListView;
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
        , AbsListView.OnScrollListener
        , SwipeRefreshLayout.OnRefreshListener
        , TextWatcher {


    public static int ACTIVITY_DETAIL = 607;
    static CaseFragment fragment;
    int ACTIVITY_SEARCH = 605;
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
    int pagesLoader = 1;
    private ArrayList<Integer> arrComplaintId = new ArrayList<>();
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
        list.setOnScrollListener(this);
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
        Activity activity = getActivity();
        if (activity != null) {
            if (s.matches("update_case_list")) {
                updateCaseList();

            } else if (s.matches("case_count")) {
                if (adpterListCase != null) {
                    adpterListCase.notifyDataSetChanged();
                }
            }
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
                        .error(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .override(300, 300)
                        .fitCenter()
                        .into(rootView);
            } else {
                getUserOfficialData();
            }

        } else {

            Glide.with(this)
                    .load(listUsers.get(0).getImage())
                    .error(R.drawable.com_facebook_profile_picture_blank_portrait)
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
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

    //Clicklistener
    @Override
    public void onClick(View v) {
        if (v == btnClick) {
            Intent intent = new Intent(getActivity(), CaseChatMemberActivity.class);
            startActivity(intent);

        } else if (v == btnCreate) {
            Intent intent = new Intent(getActivity(), CaseAddAndEditActivity.class);
            startActivity(intent);

        } else if (v == btnReport) {
            Intent intent = new Intent(getActivity(), com.pattaya.pattayacallcenter.guest.CaseAddAndEditActivity.class);
            startActivity(intent);

        } else if (v == btnAdvanceSearch) {
            Intent intent = new Intent(getActivity(), CaseAdvanceSearchActivity.class);
            startActivityForResult(intent, ACTIVITY_SEARCH);

        } else if (v == btn) {
            displayPopupWindow(v);
        } else if (v == txtEmpty) {
            mSwipeRefreshLayout.setRefreshing(true);
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
                    if (XMPPManageOfficial.getInstance() == null
                            || XMPPManageOfficial.getInstance().getmConnection() == null
                            || !XMPPManageOfficial.getInstance().getmConnection().isConnected()) {
                        getActivity().startService(new Intent(getActivity(), XMPPServiceOfficial.class));
                        System.out.println(dataPopUp.getImage());
                    }

                }
                Glide.with(v.getContext())
                        .load(dataPopUp.getImage())
                        .override(300, 300)
                        .error(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .fitCenter()
                        .into(rootView);
                mSwipeRefreshLayout.setRefreshing(true);
                getCaseList("");
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


    void getCaseListAdvanceSearch(final String s, final Boolean action, final int completed) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                txtEmpty.setVisibility(View.GONE);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                GetCaseListData getCaseListData = new GetCaseListData();
                if (isOfficial) {
                    getCaseListData.setFilterType(3);
                } else {
                    getCaseListData.setFilterType(2);
                    getCaseListData.setUserId(userId);
                }

                if (action != null) {
                    getCaseListData.setAction(action);

                }
                if (completed != 0) {
                    getCaseListData.setCompleted(completed);
                }
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
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }

                        } catch (IOException e) {
                            System.out.println("result = [" + result + "], response2 = [" + response2 + "]");
                            e.printStackTrace();
                        }
                        String JsonConvertData = "{data:" + sb.toString() + "}";
                        System.out.println(JsonConvertData);
                        CaseListMemberObject caseListObject = new Gson().fromJson(JsonConvertData, CaseListMemberObject.class);

                        dataList = caseListObject.getData();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Activity activity = getActivity();
                                if (activity != null) {
                                    progressBar.setVisibility(View.GONE);
                                    txtEmpty.setVisibility(View.VISIBLE);
                                    adpterListCase.resetAdpter(dataList);
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }

                            }
                        });


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println("error = [" + error + "]");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Activity activity = getActivity();
                                if (activity != null) {
                                    progressBar.setVisibility(View.GONE);
                                    if (adpterListCase.getCount() == 0) {
                                        txtEmpty.setVisibility(View.VISIBLE);
                                    }
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }


                            }
                        });

                    }
                });

                return null;
            }
        }.execute();

    }

    void getCaseList(final String s) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                txtEmpty.setVisibility(View.GONE);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                GetCaseListData getCaseListData = new GetCaseListData();
                if (isOfficial) {
                    getCaseListData.setFilterType(3);
                } else {
                    getCaseListData.setFilterType(2);
                    getCaseListData.setUserId(userId);
                }

                getCaseListData.setAccessToken(token);
                getCaseListData.setTextSearch(s);
                int itemPerPage;
                if (dataList.size() == 0) {
                    System.out.println("first page");
                    dataList = new ArrayList<>();
                    arrComplaintId = new ArrayList<>();
                    pagesLoader = 1;
                    itemPerPage = 10;
                    getCaseListData.setPageNo(pagesLoader);
                    getCaseListData.setItemPerPage(itemPerPage);
                } else {
                    if ((dataList.size() % 5) == 0) {
                        System.out.println("sub page mod 5");
                        pagesLoader = (dataList.size() / 5) + 1;
                        itemPerPage = 5;
                    } else if ((dataList.size() % 6) == 0) {
                        System.out.println("sub page mod 6");
                        pagesLoader = (dataList.size() / 6) + 1;
                        itemPerPage = 6;
                    } else if ((dataList.size() % 7) == 0) {
                        System.out.println("sub page mod 7");
                        pagesLoader = (dataList.size() / 7) + 1;
                        itemPerPage = 7;
                    } else if ((dataList.size() % 8) == 0) {
                        System.out.println("sub page mod 8");
                        pagesLoader = (dataList.size() / 8) + 1;
                        itemPerPage = 8;
                    } else if ((dataList.size() % 9) == 0) {
                        System.out.println("sub page mod 9");
                        pagesLoader = (dataList.size() / 9) + 1;
                        itemPerPage = 9;
                    } else {
                        System.out.println("sub page mod other");
                        pagesLoader = 2;
                        itemPerPage = dataList.size();
                    }
                    Log.e("TAG", "Page " + pagesLoader + " item " + itemPerPage);

                    getCaseListData.setPageNo(pagesLoader);
                    getCaseListData.setItemPerPage(itemPerPage);
                }


                adapterRest.getCaseList(getCaseListData, new Callback<Response>() {
                    @Override
                    public void success(final Response result, final Response response2) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DatabaseChatHelper.init().clearCaseTable();
                                BufferedReader reader;
                                StringBuilder sb = new StringBuilder();
                                try {
                                    reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        sb.append(line);
                                    }

                                } catch (IOException e) {
                                    System.out.println("result = [" + result + "], response2 = [" + response2 + "]");
                                    e.printStackTrace();
                                }
                                String JsonConvertData = "{data:" + sb.toString() + "}";
                                System.out.println(JsonConvertData);
                                final CaseListMemberObject caseListObject = new Gson().fromJson(JsonConvertData, CaseListMemberObject.class);

                                if (caseListObject.getData().size() > 0) {
                                    pagesLoader++;
                                    final ArrayList tempList = new ArrayList();
                                    for (CaseListMemberData e : caseListObject.getData()) {
                                        if (!arrComplaintId.contains(e.getComplaintId())) {
                                            arrComplaintId.add(e.getComplaintId());
                                            tempList.add(e);
                                        }
                                    }


                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Activity activity = getActivity();
                                                if (activity != null) {
                                                    progressBar.setVisibility(View.GONE);
                                                    txtEmpty.setVisibility(View.VISIBLE);
                                                    adpterListCase.addItem(tempList);
                                                    dataList = adpterListCase.getListData();
                                                    mSwipeRefreshLayout.setRefreshing(false);
                                                }

                                            }
                                        });
                                    }
                                }
                            }
                        }).start();


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println("error = [" + error + "]");
                        Activity activity = getActivity();
                        if (activity != null) {
                            progressBar.setVisibility(View.GONE);
                            if (adpterListCase.getCount() == 0) {
                                txtEmpty.setVisibility(View.VISIBLE);
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                    }
                });

                return null;
            }
        }.execute();

    }

    void getCaseListSearch(final String s) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                txtEmpty.setVisibility(View.GONE);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                GetCaseListData getCaseListData = new GetCaseListData();
                if (isOfficial) {
                    getCaseListData.setFilterType(3);
                } else {
                    getCaseListData.setFilterType(2);
                    getCaseListData.setUserId(userId);
                }

                getCaseListData.setAccessToken(token);
                getCaseListData.setTextSearch(s);
                int itemPerPage;
                if (dataList.size() == 0) {
                    System.out.println("first page");
                    dataList = new ArrayList<>();
                    arrComplaintId = new ArrayList<>();
                    pagesLoader = 1;
                    itemPerPage = 40;
                    getCaseListData.setPageNo(pagesLoader);
                    getCaseListData.setItemPerPage(itemPerPage);
                } else {
                    if ((dataList.size() % 5) == 0) {
                        System.out.println("sub page mod 5");
                        pagesLoader = (dataList.size() / 5) + 1;
                        itemPerPage = 5;
                    } else if ((dataList.size() % 6) == 0) {
                        System.out.println("sub page mod 6");
                        pagesLoader = (dataList.size() / 6) + 1;
                        itemPerPage = 6;
                    } else if ((dataList.size() % 7) == 0) {
                        System.out.println("sub page mod 7");
                        pagesLoader = (dataList.size() / 7) + 1;
                        itemPerPage = 7;
                    } else if ((dataList.size() % 8) == 0) {
                        System.out.println("sub page mod 8");
                        pagesLoader = (dataList.size() / 8) + 1;
                        itemPerPage = 8;
                    } else if ((dataList.size() % 9) == 0) {
                        System.out.println("sub page mod 9");
                        pagesLoader = (dataList.size() / 9) + 1;
                        itemPerPage = 9;
                    } else {
                        System.out.println("sub page mod other");
                        pagesLoader = 2;
                        itemPerPage = dataList.size();
                    }
                    Log.e("TAG", "Page " + pagesLoader + " item " + itemPerPage);

                    getCaseListData.setPageNo(pagesLoader);
                    getCaseListData.setItemPerPage(itemPerPage);
                }


                adapterRest.getCaseList(getCaseListData, new Callback<Response>() {
                    @Override
                    public void success(final Response result, final Response response2) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DatabaseChatHelper.init().clearCaseTable();
                                BufferedReader reader;
                                StringBuilder sb = new StringBuilder();
                                try {
                                    reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        sb.append(line);
                                    }

                                } catch (IOException e) {
                                    System.out.println("result = [" + result + "], response2 = [" + response2 + "]");
                                    e.printStackTrace();
                                }
                                String JsonConvertData = "{data:" + sb.toString() + "}";
                                System.out.println(JsonConvertData);
                                final CaseListMemberObject caseListObject = new Gson().fromJson(JsonConvertData, CaseListMemberObject.class);

                                if (caseListObject.getData().size() > 0) {
                                    pagesLoader++;
                                    final ArrayList tempList = new ArrayList();
                                    for (CaseListMemberData e : caseListObject.getData()) {
                                        if (!arrComplaintId.contains(e.getComplaintId())) {
                                            arrComplaintId.add(e.getComplaintId());
                                            tempList.add(e);
                                        }
                                    }


                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Activity activity = getActivity();
                                                if (activity != null) {
                                                    progressBar.setVisibility(View.GONE);
                                                    txtEmpty.setVisibility(View.VISIBLE);
                                                    adpterListCase.addItem(tempList);
                                                    dataList = adpterListCase.getListData();
                                                    mSwipeRefreshLayout.setRefreshing(false);
                                                }

                                            }
                                        });
                                    }
                                }
                            }
                        }).start();


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println("error = [" + error + "]");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Activity activity = getActivity();
                                if (activity != null) {
                                    progressBar.setVisibility(View.GONE);
                                    if (adpterListCase.getCount() == 0) {
                                        txtEmpty.setVisibility(View.VISIBLE);
                                    }
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }


                            }
                        });

                    }
                });

                return null;
            }
        }.execute();

    }

    void updateCaseList() {
        if (dataList.size() == 0) {
            arrComplaintId = new ArrayList<>();
        }
        GetCaseListData getCaseListData = new GetCaseListData();
        if (isOfficial) {
            getCaseListData.setFilterType(3);
        } else {
            getCaseListData.setFilterType(2);
            getCaseListData.setUserId(userId);
        }

        getCaseListData.setAccessToken(token);
        getCaseListData.setPageNo(1);
        getCaseListData.setItemPerPage(10);

        adapterRest.getCaseList(getCaseListData, new Callback<Response>() {
            @Override
            public void success(Response result, Response response2) {
                DatabaseChatHelper.init().clearCaseTable();
                BufferedReader reader;
                StringBuilder sb = new StringBuilder();
                try {
                    reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                } catch (IOException e) {
                    System.out.println("result = [" + result + "], response2 = [" + response2 + "]");
                    e.printStackTrace();
                }
                String JsonConvertData = "{data:" + sb.toString() + "}";
                System.out.println(JsonConvertData);
                final CaseListMemberObject caseListObject = new Gson().fromJson(JsonConvertData, CaseListMemberObject.class);

                if (caseListObject.getData().size() > 0) {
                    pagesLoader++;
                    final ArrayList tempList = new ArrayList();
                    for (CaseListMemberData e : caseListObject.getData()) {
                        if (!arrComplaintId.contains(e.getComplaintId())) {
                            arrComplaintId.add(e.getComplaintId());
                            tempList.add(e);
                        }
                    }

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Activity activity = getActivity();
                                if (activity != null) {
                                    progressBar.setVisibility(View.GONE);
                                    txtEmpty.setVisibility(View.VISIBLE);
                                    adpterListCase.addItemUpdate(tempList);
                                    dataList = adpterListCase.getListData();
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }

                            }
                        });
                    }
                }


            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Activity activity = getActivity();
                        if (activity != null) {
                            progressBar.setVisibility(View.GONE);
                            if (adpterListCase.getCount() == 0) {
                                txtEmpty.setVisibility(View.VISIBLE);
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        }


                    }
                });

            }
        });
    }

    void getUserOfficialData() {
        adapterRestUser.getUserOfficialData(userId, new Callback<OfficialObject>() {
            @Override
            public void success(OfficialObject officialObject, Response response) {
                if (officialObject.getId() > 0 && listUsers.size() == 1) {
                    listUsers.add(new DataPopUp(officialObject.getUserImage(), officialObject.getDisplayname()));
                    System.out.println("officialObject = [" + officialObject + "], response = [" + response + "]");

                    ImageView rootView = (ImageView) btn;
                    if (rootView != null && isOfficial) {
                        Glide.with(getActivity())
                                .load(listUsers.get(1).getImage())
                                .error(R.drawable.com_facebook_profile_picture_blank_portrait)
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
        updateCaseList();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mSwipeRefreshLayout.setRefreshing(true);
        String text = s.toString();
        dataList = new ArrayList<>();
        adpterListCase.resetAdpter(new ArrayList<CaseListMemberData>());
        getCaseList(text);

    }

    public void startCommentActivity(Intent i) {
        startActivityForResult(i, ACTIVITY_DETAIL);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_SEARCH) {
            if (resultCode == Activity.RESULT_OK) {
                Boolean action = data.getBooleanExtra("action", false);
                int completed = data.getIntExtra("completed", 0);
                String text = data.getStringExtra("text");
                System.out.println("action" + action + "" + completed + "  " + text);
                mSwipeRefreshLayout.setRefreshing(true);
                getCaseListAdvanceSearch(text, action, completed);

            }

        }
        if (requestCode == ACTIVITY_DETAIL) {
            if (resultCode == Activity.RESULT_OK) {
                String detail = data.getStringExtra("detail");
                int complainId = data.getIntExtra("complainId", 0);
                adpterListCase.updateItem(complainId, detail);
                Log.e("TAG", "detail >>" + detail + "    name " + detail);


            }


        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem == 0) {
            // check if we reached the top or bottom of the list
            View v = list.getChildAt(0);
            int offset = (v == null) ? 0 : v.getTop();
            if (offset == 0) {
                return;
            }
        }

        if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1
                && list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) {
            System.out.println("bottom in ListView ");
            if (!mSwipeRefreshLayout.isRefreshing()) {
                getCaseList("");
            }
            mSwipeRefreshLayout.setRefreshing(true);

            return;
        }
    }
}
