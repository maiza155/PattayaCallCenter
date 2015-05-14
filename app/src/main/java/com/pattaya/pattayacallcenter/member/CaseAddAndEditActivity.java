package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.CountingTypedFile;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.ProgressListener;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.chat.PubsubObject;
import com.pattaya.pattayacallcenter.chat.XMPPManage;
import com.pattaya.pattayacallcenter.chat.jsonobject.ChatRoomObject;
import com.pattaya.pattayacallcenter.chat.jsonobject.Members;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.customview.CameraMange;
import com.pattaya.pattayacallcenter.customview.CustomGalleryActivity;
import com.pattaya.pattayacallcenter.customview.ExpandableHeightGridView;
import com.pattaya.pattayacallcenter.guest.CaseDetail.ImageData;
import com.pattaya.pattayacallcenter.guest.CaseDetail.ImageUpdateGridAdapter;
import com.pattaya.pattayacallcenter.guest.PlaceActivity;
import com.pattaya.pattayacallcenter.member.data.ForwardObject;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.AccessUserObject;
import com.pattaya.pattayacallcenter.webservice.object.GetUserObject;
import com.pattaya.pattayacallcenter.webservice.object.PersonalObject;
import com.pattaya.pattayacallcenter.webservice.object.UpdateResult;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseAssignObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseDataMemberObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseDataObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseListJidObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseTypeObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.GetComplainObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.ImageObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.JidData;
import com.pattaya.pattayacallcenter.webservice.object.casedata.ListTypeDataObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.OpenCaseAssignObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.OrgIdObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.UserIdObject;
import com.pattaya.pattayacallcenter.webservice.object.upload.FileListObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CaseAddAndEditActivity extends ActionBarActivity implements View.OnClickListener, View.OnCreateContextMenuListener {

    static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static SharedPreferences spConfig;
    private static SharedPreferences sp;
    RestAdapter webserviceConnectorUser = WebserviceConnector.getInstance();
    RestAdapter webserviceConnectorPersonal = WebserviceConnector.getInstancePersonal();
    ProgressDialog ringProgressDialog;
    private RestFulQueary adapterRestPersonal = null;
    private TextView txtPlace;
    private TextView txtTo;
    private EditText txtName;
    private EditText txtDetail;
    private EditText txtPhone;
    private CheckBox checkAnonymous;
    private List imageData;
    private CameraMange cameraMange;
    private ImageUpdateGridAdapter mGridAdapter;
    private ExpandableHeightGridView gridView;
    private List<ImageObject> listOldImageUrl;
    private Button btnPlace;
    private Button btnImage;
    private Button btnTo;
    private CaseDataObject dataPlace;
    // widget
    private ImageButton btn;
    private TextView titleTextView;
    private ArrayList<ForwardObject> arrayList;
    private Spinner spinner1, spinner2;
    private List<CaseTypeObject> listData = new ArrayList();
    private List<CaseTypeObject> listSubData = new ArrayList();
    private List<String> list2 = new ArrayList();
    private List<String> list = new ArrayList();
    private ArrayAdapter<String> adapter, adapter2;
    private OpenCaseAssignObject openCaseAssignObject;
    private RestAdapter webserviceConnector = WebserviceConnector.getInstanceCase();
    private RestFulQueary adapterRest = null;
    private RestFulQueary adapterRestUser = null;
    private RestAdapter webserviceConnectorUpload = WebserviceConnector.getInstanceUpload();
    private RestFulQueary adapterRestUpload = null;
    private RestAdapter openfireConnectorJson = RestAdapterOpenFire.getInstanceJson();
    private OpenfireQueary openfireQuearyJson = openfireConnectorJson.create(OpenfireQueary.class);
    private int selectItem = -1;
    private int STATE_FORWARD = 0;
    private int userId;
    private String token;
    private String clientId;
    private int caseId;
    private int complainId;
    private String displayName;
    private String displayImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_add_and_edit2);

        setActionbar();
        adapterRestUpload = webserviceConnectorUpload.create(RestFulQueary.class);
        adapterRest = webserviceConnector.create(RestFulQueary.class);
        adapterRestUser = webserviceConnectorUser.create(RestFulQueary.class);
        adapterRestPersonal = webserviceConnectorPersonal.create(RestFulQueary.class);

        checkAnonymous = (CheckBox) findViewById(R.id.check);
        txtDetail = (EditText) findViewById(R.id.edit_textdetail);
        txtName = (EditText) findViewById(R.id.edit_text_name);
        txtPhone = (EditText) findViewById(R.id.edit_text_phone);
        spinner1 = (Spinner) findViewById(R.id.sp1);
        spinner2 = (Spinner) findViewById(R.id.sp2);
        // init Data in spinner
        list2.add("ประเภท");
        list.add("บริการ");


        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, list2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter2);

        setTypeMain(0, spinner1);


        ////////////////////////////////////////////////////////////////////////////////////////////

        txtPlace = (TextView) findViewById(R.id.txt_place);
        txtTo = (TextView) findViewById(R.id.txt_to);
        btnImage = (Button) findViewById(R.id.btn_image);
        btnPlace = (Button) findViewById(R.id.btn_place);
        btnTo = (Button) findViewById(R.id.btn_to);
        btnImage.setOnClickListener(this);
        btnPlace.setOnClickListener(this);
        btnImage.setOnCreateContextMenuListener(this);
        btn.setOnClickListener(this);
        btnTo.setOnClickListener(this);


        arrayList = new ArrayList<>();

        dataPlace = new CaseDataObject();

        listData = new ArrayList<>();
        imageData = new ArrayList();
        cameraMange = new CameraMange(this);
        gridView = (ExpandableHeightGridView) findViewById(R.id.grid_view);
        gridView.setExpanded(true);
        mGridAdapter = new ImageUpdateGridAdapter(this, R.layout.custom_gridview_image, imageData);
        gridView.setAdapter(mGridAdapter);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    int pos = position - 1;
                    setTypeMain(listData.get(pos).getId(), spinner2);
                    selectItem = listData.get(pos).getId();
                    System.out.println(selectItem);
                    adapter2.clear();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    int pos = position - 1;
                    //setTypeMain(listData.get(pos).getId(), spinner2);
                    selectItem = listSubData.get(pos).getId();
                    System.out.println(selectItem);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        init();


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finish();
        startActivity(intent);

    }
    void init() {
        Intent intent = getIntent();
        caseId = intent.getIntExtra("id", 0);
        complainId = intent.getIntExtra("complainid", 0);
        System.out.println(caseId);
        System.out.println(complainId);

        if (caseId > 0) {
            System.out.println(caseId);
            getData();

        }

        sp = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        userId = sp.getInt(MasterData.SHARED_USER_USER_ID, -10);
        displayImage = sp.getString(MasterData.SHARED_USER_IMAGE, null);
        displayName = sp.getString(MasterData.SHARED_USER_DISPLAY_NAME, null);

        spConfig = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        token = spConfig.getString(MasterData.SHARED_CONFIG_TOKEN, null);
        clientId = spConfig.getString(MasterData.SHARED_CONFIG_CLIENT_ID, null);

    }

    void setActionbar() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        titleTextView.setText(getResources().getString(R.string.case_detail));
        titleTextView.setPadding(40, 0, 0, 0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    void setTextTO() {
        if (arrayList.size() > 0) {
            if (STATE_FORWARD == 1) {
                if (arrayList.size() == 1) {
                    txtTo.setText(arrayList.get(0).getName());
                } else if (arrayList.size() == 2) {
                    txtTo.setText(arrayList.get(0).getName() + " เเละ " + arrayList.get(1).getName());
                } else {
                    txtTo.setText(arrayList.get(0).getName() + " เเละอีก 2 หน่วยงาน");
                }

            } else if (STATE_FORWARD == 2) {
                if (arrayList.size() == 1) {
                    txtTo.setText(arrayList.get(0).getName());
                } else if (arrayList.size() == 2) {
                    txtTo.setText(arrayList.get(0).getName() + " เเละ " + arrayList.get(1).getName());
                } else {
                    txtTo.setText(arrayList.get(0).getName() + " เเละอีก 2 คน");
                }

            }
        } else {
            txtTo.setText("ไม่มีข้อมูล");
        }

    }

    void setTypeMain(int id, final Spinner view) {
        adapterRest.getTypeCaseList(id, new Callback<Response>() {
            @Override
            public void success(Response result, Response response2) {
                list = new ArrayList();
                list2 = new ArrayList();

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
                ListTypeDataObject caseTypeObject = new Gson().fromJson(JsonConvertData, ListTypeDataObject.class);
                if (view == spinner1) {
                    listData = new ArrayList();
                    for (CaseTypeObject e : caseTypeObject.getData()) {
                        list.add(e.getName());
                        listData.add(e);
                        System.out.println(e.getName());
                    }

                } else if (view == spinner2) {
                    listSubData = new ArrayList<>();
                    for (CaseTypeObject e : caseTypeObject.getData()) {
                        list2.add(e.getName());
                        listSubData.add(e);
                        System.out.println(e.getName());
                    }
                }
                if (view == spinner1) {
                    adapter.clear();
                    // listData.addAll(list);
                    adapter.add("บริการ");
                    adapter.addAll(list);
                } else if (view == spinner2) {

                    //adapter2.clear();
                    //listSubData
                    adapter2.add("ประเภท");
                    adapter2.addAll(list2);
                    spinner2.setSelection(0);

                }


                //adapter.notifyDataSetChanged();

            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");

            }
        });


    }

    OpenCaseAssignObject setCaseData() {
        openCaseAssignObject = new OpenCaseAssignObject();
        openCaseAssignObject.setCasesName(txtDetail.getText().toString());
        openCaseAssignObject.setContactInfo(dataPlace);
        openCaseAssignObject.setTypeCaseAssign(STATE_FORWARD);
        openCaseAssignObject.setAccessToken(token);
        if (selectItem != -1) {
            System.out.println("Item >>>>" + selectItem);
            openCaseAssignObject.setServiceTypeId(selectItem);
        }

        Calendar c = Calendar.getInstance();
        System.out.println(sdf.format(c.getTime())); //2014/08/06 16:00:22
        openCaseAssignObject.setComplaintDateString(sdf.format(c.getTime()));
        openCaseAssignObject.setCreateBy(userId);
        openCaseAssignObject.setClientId(clientId);
        if (STATE_FORWARD == 1) {
            List<OrgIdObject> list = new ArrayList<>();
            for (ForwardObject e : arrayList) {
                list.add(new OrgIdObject(e.getId()));
            }
            openCaseAssignObject.setCaseAssignList(list);
        } else if (STATE_FORWARD == 2) {
            List<UserIdObject> list = new ArrayList<>();
            for (ForwardObject e : arrayList) {
                list.add(new UserIdObject(e.getId()));
            }
            openCaseAssignObject.setCaseAssignList(list);
        }

        return openCaseAssignObject;
    }

    void uploadImage() {
        new TaskResizeUpload().execute();
    }

    void saveData() {
        runOnUiThread(new Runnable() {
            public void run() {
                ringProgressDialog = ProgressDialog.show(CaseAddAndEditActivity.this, getResources().getString(R.string.update_data), getResources().getString(R.string.please_wait), true);
                ringProgressDialog.setCancelable(true);
            }
        });


        final OpenCaseAssignObject openCaseAssignObject = setCaseData();
        if (openCaseAssignObject != null) {
            GetUserObject getUserObject = new GetUserObject(userId, token, clientId);

            // query user data from webservice
            adapterRestUser.getUser(getUserObject, new Callback<AccessUserObject>() {
                @Override
                public void success(AccessUserObject accessUserObject, Response response) {
                    Log.d("Profile User Access", " " + accessUserObject.toString());
                    if (accessUserObject != null) {
                        CaseDataObject caseDataObject = openCaseAssignObject.getContactInfo();
                        caseDataObject.setAddress(accessUserObject.getAddress());
                        caseDataObject.setDistrict(accessUserObject.getDistrict());
                        caseDataObject.setAmphur(accessUserObject.getAmphur());
                        caseDataObject.setProvince(accessUserObject.getProvince());
                        caseDataObject.setPostCode(accessUserObject.getPostCode());
                        caseDataObject.setEmail(accessUserObject.getEmail());
                        if (caseDataObject.getTelephone() == null || caseDataObject.getTelephone().isEmpty()) {
                            caseDataObject.setTelephone(accessUserObject.getMobile());
                        }
                        if (caseDataObject.getNameContact() == null || caseDataObject.getNameContact().isEmpty()) {
                            caseDataObject.setNameContact(accessUserObject.getDisplayName());
                        }


                        PersonalObject personalObject = new PersonalObject();
                        personalObject.setAddress(accessUserObject.getAddress());
                        personalObject.setAmphur(accessUserObject.getAmphur());
                        personalObject.setDistrict(accessUserObject.getDistrict());
                        personalObject.setEmail(accessUserObject.getEmail());
                        personalObject.setFirstname(accessUserObject.getFirstname());
                        personalObject.setLastname(accessUserObject.getLastname());
                        personalObject.setIdCard(accessUserObject.getIdCard());
                        personalObject.setProvince(accessUserObject.getProvince());
                        personalObject.setPostCode(accessUserObject.getPostCode());
                        personalObject.setPersonalImage(accessUserObject.getUserImage());
                        personalObject.setMobile(accessUserObject.getMobile());


                        adapterRestPersonal.savePersonalAndChkMobile(personalObject, new Callback<UpdateResult>() {
                            @Override
                            public void success(UpdateResult updateResult, Response response) {
                                System.out.println("updateResult adapterRestPersonal  = [" + updateResult.getResult() + "], response = [" + response + "]");
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                System.out.println("error = [" + error + "]");

                            }
                        });


                        openCaseAssignObject.setContactInfo(caseDataObject);

                        //save data
                        adapterRest.openCases(openCaseAssignObject, new Callback<UpdateResult>() {
                            @Override
                            public void success(UpdateResult updateResult, Response response) {
                                System.out.println(userId);
                                System.out.println("updateResult = [" + updateResult.getCasesId() + "], response = [" + response + "]");
                                if (updateResult.getResult()) {
                                    String roomName = "case-" + updateResult.getPrimaryKeyId();
                                    final ChatRoomObject chatRoomObject = new ChatRoomObject();
                                    chatRoomObject.setRoomName(roomName);
                                    chatRoomObject.setNaturalName(openCaseAssignObject.getCasesName());
                                    chatRoomObject.setDescription(roomName);
                                    List<String> listJid = new ArrayList<String>();
                                    final List<PubsubObject> listNotify = new ArrayList<PubsubObject>();
                                    Calendar c = Calendar.getInstance();
                                    if (STATE_FORWARD == 2) {
                                        Members members = new Members();
                                        for (ForwardObject e : arrayList) {
                                            if (e.getJid() != null) {
                                                listJid.add(e.getJid());
                                                PubsubObject pub = new PubsubObject();
                                                pub.setUsername(e.getJid().split("@")[0]);
                                                pub.setImage(displayImage);
                                                pub.setAction("เปิดเคส");
                                                pub.setDisplayData(c.getTime().toString());
                                                pub.setCaseId(updateResult.getCasesId());
                                                pub.setComplainId(updateResult.getPrimaryKeyId());
                                                pub.setName(displayName);
                                                pub.setTitle(openCaseAssignObject.getCasesName());
                                                listNotify.add(pub);
                                            }

                                        }
                                        members.setMember(listJid);
                                        chatRoomObject.setMembers(members);
                                    }

                                    openfireQuearyJson.createChatRoom(chatRoomObject, new Callback<Response>() {
                                        @Override
                                        public void success(Response response, Response response2) {
                                            //System.out.println("response = [" + response + "], response2 = [" + response2 + "]");
                                            for (PubsubObject e : listNotify) {
                                                Log.e("TAG", ">>>>" + e.getUsername());
                                                XMPPManage.getInstance().new TaskSendNotify(e).execute();
                                            }
                                            ringProgressDialog.dismiss();
                                            BusProvider.getInstance().post("update_case_list");
                                            Toast.makeText(getApplication(), "success", Toast.LENGTH_SHORT).show();
                                            finish();


                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            System.out.println("error = [" + error + "]");
                                            ringProgressDialog.dismiss();
                                            Toast.makeText(getApplication(), "Unable connect server. Please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                } else {

                                    ringProgressDialog.dismiss();
                                    Toast.makeText(getApplication(), "Data is  not correct. Please try again.", Toast.LENGTH_SHORT).show();
                                }


                            }

                            @Override
                            public void failure(RetrofitError error) {
                                ringProgressDialog.dismiss();
                                System.out.println("error = [" + error.getResponse() + "]");
                                Toast.makeText(getApplication(), "Unable connect server. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    ringProgressDialog.dismiss();
                    System.out.println("error get user data = [" + error + "]");
                    Toast.makeText(getApplication(), "Unable connect server. Please try again", Toast.LENGTH_SHORT).show();

                }
            });


        } else {
            ringProgressDialog.dismiss();
        }
    }

    void getData() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.load_data), getResources().getString(R.string.please_wait), true);
        ringProgressDialog.setCancelable(true);
        GetComplainObject getComplainObject = new GetComplainObject();
        getComplainObject.setAccessToken(token);
        getComplainObject.setClientId(clientId);
        getComplainObject.setPrimaryKeyId(caseId);

        adapterRest.getCaseData(getComplainObject, new Callback<CaseDataMemberObject>() {
            @Override
            public void success(CaseDataMemberObject caseDataMemberObject, Response response) {
                System.out.println("caseDataMemberObject = [" + caseDataMemberObject + "], response = [" + response + "]");
                STATE_FORWARD = caseDataMemberObject.getTypeCaseAssign();
                txtDetail.setText(caseDataMemberObject.getCasesName());
                ////////////////////////////////////////////////////////////////////////////////////
                if (caseDataMemberObject.getCaseAssignList() != null) {
                    for (CaseAssignObject e : caseDataMemberObject.getCaseAssignList()) {
                        ForwardObject object = new ForwardObject();
                        if (STATE_FORWARD == 1) {
                            object.setId(e.getOrganizeId());
                            object.setImage(e.getUserImage());
                            object.setName(e.getOrganizeName());
                            arrayList.add(object);

                        } else if (STATE_FORWARD == 2) {
                            object.setId(e.getUserId());
                            object.setImage(e.getUserImage());
                            object.setName(e.getUserName());
                            arrayList.add(object);
                        }

                    }

                    //set text in AssignText
                    setTextTO();
                }
                ////////////////////////////////////////////////////////////////////////////////////


                ////////////////////////////////////////////////////////////////////////////////////
                if (caseDataMemberObject.getContactInfo() != null) {
                    dataPlace = caseDataMemberObject.getContactInfo();

                    checkAnonymous.setChecked(dataPlace.getIsAnonymousString());

                    //////////// Set Address /////////////
                    String houseNumber = dataPlace.getHouseNumber();
                    houseNumber = (houseNumber == null || houseNumber.isEmpty()) ? "" : houseNumber + " ";

                    String village = dataPlace.getVillage();
                    village = (village == null || village.isEmpty()) ? "" : village + " ";

                    String soi = dataPlace.getSoi();
                    soi = (soi == null || soi.isEmpty()) ? "" : soi + " ";

                    String road = dataPlace.getRoad();
                    road = (road == null || road.isEmpty()) ? "" : road + "\n";

                    String more = dataPlace.getMoreInformation();
                    more = (more == null || more.matches("")) ? "" : "(" + more + ")";


                    String address = houseNumber + village + soi + road + more;
                    if (address.matches("")) {
                        address = "ไม่มีข้อมูล";
                    }


                    txtPlace.setText(address);
                    txtName.setText(dataPlace.getTelephone());
                    txtPhone.setText(dataPlace.getNameContact());


                    ////////////////////////Image View///////////////////////////
                    listOldImageUrl = new ArrayList();
                    if (dataPlace.getInfoImageList().size() > 0) {
                        System.out.println("Have  data");
                        //mGridAdapter.resetAdapter(caseMainObject.getContactInfo().getInfoImageList());
                        listOldImageUrl = dataPlace.getInfoImageList();
                        for (ImageObject e : listOldImageUrl) {
                            System.out.println("Have  data" + e.getInfoImage());
                            mGridAdapter.addItemUpdate(e.getInfoImage());
                        }
                    } else {
                        System.out.println("no data");
                    }
                }
                ringProgressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");
                ringProgressDialog.dismiss();
                Toast.makeText(getApplication(),
                        "Please check your internet connection.", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        });
    }

    void updateCase() {
        runOnUiThread(new Runnable() {
            public void run() {
                ringProgressDialog = ProgressDialog.show(CaseAddAndEditActivity.this, null, getResources().getString(R.string.please_wait), true);
                ringProgressDialog.setCancelable(true);
            }
        });

        final OpenCaseAssignObject openCaseAssignObject = setCaseData();

        openCaseAssignObject.setCasesNo(caseId);
        Gson gson = new Gson();
        String json = gson.toJson(openCaseAssignObject);
        System.out.println(json);

        if (openCaseAssignObject != null) {

            //saveCase
            adapterRest.saveCases(openCaseAssignObject, new Callback<UpdateResult>() {
                @Override
                public void success(UpdateResult updateResult, Response response) {
                    //adapterRest.getUserListJid();
                    ringProgressDialog.dismiss();
                    if (updateResult.getResult()) {
                        String roomName = "case-" + complainId;
                        final ChatRoomObject chatRoomObject = new ChatRoomObject();
                        chatRoomObject.setRoomName(roomName);
                        chatRoomObject.setNaturalName(openCaseAssignObject.getCasesName());
                        chatRoomObject.setDescription(roomName);

                        //get JidList for notify and Recreate chat room
                        adapterRest.getUserListJid(complainId, new Callback<Response>() {
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
                                CaseListJidObject listObject = new Gson().fromJson(JsonConvertData, CaseListJidObject.class);
                                Members members = new Members();
                                List<String> listJid = new ArrayList();
                                final List<PubsubObject> listNotify = new ArrayList<PubsubObject>();
                                Calendar c = Calendar.getInstance();
                                for (JidData e : listObject.getData()) {
                                    if (!e.getJid().isEmpty() && e.getJid() != null) {
                                        listJid.add(e.getJid());
                                        PubsubObject pub = new PubsubObject();
                                        pub.setUsername(e.getJid().split("@")[0]);
                                        pub.setImage(displayImage);
                                        pub.setAction("อัพเดทเคส");
                                        pub.setDisplayData(c.getTime().toString());
                                        //pub.setPrimarykey(complainId);
                                        pub.setComplainId(complainId);
                                        pub.setCaseId(caseId);
                                        pub.setName(displayName);
                                        pub.setTitle(openCaseAssignObject.getCasesName());

                                        listNotify.add(pub);
                                    }

                                }
                                members.setMember(listJid);
                                chatRoomObject.setMembers(members);

                                //Recreate chat room
                                openfireQuearyJson.createChatRoom(chatRoomObject, new Callback<Response>() {
                                    @Override
                                    public void success(Response response, Response response2) {
                                        //System.out.println("response = [" + response + "], response2 = [" + response2 + "]");
                                        for (PubsubObject e : listNotify) {

                                            Log.e("TAG", ">>>>" + e.getUsername());
                                            XMPPManage.getInstance().new TaskSendNotify(e).execute();
                                        }
                                        ringProgressDialog.dismiss();
                                        Toast.makeText(getApplication(), "success", Toast.LENGTH_SHORT).show();
                                        BusProvider.getInstance().post("update_case_list");
                                        finish();


                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        System.out.println("error = [" + error + "]");
                                        ringProgressDialog.dismiss();
                                        Toast.makeText(getApplication(), "Unable connect server. Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }

                            @Override
                            public void failure(RetrofitError error) {
                                System.out.println("error = [" + error + "]");
                                ringProgressDialog.dismiss();
                                Toast.makeText(getApplication(), "Unable connect server \n Please try again", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(getApplication(), "Data is  not correct \n Please try again.", Toast.LENGTH_SHORT).show();
                    }
                    System.out.println(userId);
                    System.out.println("updateResult = [" + updateResult.getResult() + "], response = [" + response + "]");

                }

                @Override
                public void failure(RetrofitError error) {
                    ringProgressDialog.dismiss();
                    System.out.println("error = [" + error.getResponse() + "]");
                    Toast.makeText(getApplication(), "Unable connect server \n Please try again", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ringProgressDialog.dismiss();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_case_add_and_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            //finish();
            String[] detail = txtDetail.getText().toString().split(" ");
            System.out.println("  length" + detail.length);
            if (txtDetail.getText().toString().isEmpty() || detail.length <= 0) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(txtDetail);
                Toast.makeText(getApplication(),
                        getResources().getString(R.string.please_enter_fill), Toast.LENGTH_SHORT)
                        .show();

            } else if (arrayList.size() == 0) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(txtTo);
                Toast.makeText(getApplication(),
                        "โปรดระบุผู้รับผิดชอบ", Toast.LENGTH_SHORT)
                        .show();
            } else {
                dataPlace.setIsAnonymousString(checkAnonymous.isChecked());
                dataPlace.setTelephone(txtPhone.getText().toString());
                dataPlace.setNameContact(txtName.getText().toString());
                uploadImage();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == btnImage) {
            v.showContextMenu();
        } else if (v == btnPlace) {
            Intent intent = new Intent(getApplication(), PlaceActivity.class);
            intent.putExtra("place", dataPlace);
            startActivityForResult(intent, MasterData.PLACE_DATA);
        } else if (v == btn) {
            finish();
        } else if (v == btnTo) {
            Intent intent = new Intent(getApplication(), CaseForwardToActivity.class);
            Log.e("Tag ", "" + arrayList.size());
            intent.putParcelableArrayListExtra("data", arrayList);
            intent.putExtra("state", STATE_FORWARD);
            startActivityForResult(intent, MasterData.FORWARD_TO_DATA);
        }

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(getResources().getString(R.string.image_select));
        menu.add(0, v.getId(), 0, getResources().getString(R.string.image_select_from_gallery)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mGridAdapter.getCount() < 6) {
                    Intent intent = new Intent(getApplication(), CustomGalleryActivity.class);
                    startActivityForResult(intent, MasterData.PICK_IMAGE_MULTIPLE);
                } else {
                    Toast.makeText(getApplication(), "You can upload about 6 photos per case.", Toast.LENGTH_SHORT).show();
                }


                return true;
            }
        });
        //groupId, itemId, order, title
        menu.add(0, v.getId(), 0, getResources().getString(R.string.image_capture)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mGridAdapter.getCount() < 6) {
                    cameraMange.captureImage();
                } else {
                    Toast.makeText(getApplication(), "You can upload about 6 photos per case.", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == cameraMange.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    File imgFile = new File(cameraMange.fileUri.getPath());
                    if (imgFile.exists()) {
                        if (mGridAdapter.getCount() < 6) {
                            mGridAdapter.addItem(imgFile.getPath());
                        } else {
                            Toast.makeText(getApplication(), "You can upload about 6 photos per case.", Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        /** Check Image From Gallery*/
        if (requestCode == MasterData.PICK_IMAGE_MULTIPLE) {
            if (resultCode == Activity.RESULT_OK) {
                String[] imagesPath = data.getStringExtra("data").split("\\|");
                for (int i = 0; i < imagesPath.length; i++) {
                    if (mGridAdapter.getCount() < 6) {
                        mGridAdapter.addItem(imagesPath[i]);
                    } else {
                        Toast.makeText(getApplication(), "You can upload about 6 photos per case.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                }

            }
        }

        if (requestCode == MasterData.PLACE_DATA) {
            if (resultCode == Activity.RESULT_OK) {
                dataPlace = data.getParcelableExtra("place");
                txtPlace.setText(dataPlace.toString());
            }
        }

        if (requestCode == MasterData.FORWARD_TO_DATA) {
            if (resultCode == Activity.RESULT_OK) {
                STATE_FORWARD = data.getIntExtra("state", 0);
                arrayList = (ArrayList<ForwardObject>) data.getSerializableExtra("data");
                setTextTO();

            }
        }


    }


    class TaskResizeUpload extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog ringProgressDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            ringProgressDialog = ProgressDialog.show(CaseAddAndEditActivity.this, getResources().getString(R.string.uploading), getResources().getString(R.string.please_wait), true);
            ringProgressDialog.setCancelable(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final List<ImageObject> listImageURL = new ArrayList<>();
            if (mGridAdapter.getData().size() > 0) {
                int imageCount = 0;
                for (ImageData e : mGridAdapter.getData()) {
                    if (e.getTag() == 1) {
                        imageCount++;
                        int randomNum = 500 + (int) ((Math.random() * 1204006080) / Math.random());
                        File file = new File(getCacheDir(), "pattaya-case" + randomNum);
                        try {
                            file.createNewFile();
                        } catch (IOException error) {
                            error.printStackTrace();
                        }
                        Bitmap bitmap = null;
                        try {
                            // resize
                            bitmap = Glide.with(Application.getContext())
                                    .load(e.getPath())
                                    .asBitmap()
                                    .fitCenter()
                                    .into(500, 500) // Width and height
                                    .get();
                        } catch (InterruptedException error) {
                            error.printStackTrace();
                        } catch (ExecutionException error) {
                            error.printStackTrace();
                        }

                        System.out.println(bitmap);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, MasterData.PERCEN_OF_IMAGE_FILE, bos);
                        byte[] bitmapdata = bos.toByteArray();
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(file);
                        } catch (FileNotFoundException error) {
                            error.printStackTrace();
                        }
                        try {
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                        } catch (IOException error) {
                            error.printStackTrace();
                        }
                        Log.e("File upload", "" + file.length());
                        Log.e("File upload", "" + file.getAbsolutePath());
                        final long totalSize = file.length();
                        System.out.println("FILE LENGTH" + ">>>>>>>>" + totalSize);


                        final int finalImageCount = imageCount;
                        ProgressListener listener = new ProgressListener() {
                            String fileID = "Please wait... \nimage" + finalImageCount + " upload complete";

                            @Override
                            public void transferred(long num) {
                                //publishProgress((int) ((num / (float) totalSize) * 100));
                                final int[] count = {Math.round(((num / (float) totalSize) * 100))};
                                runOnUiThread(new Thread(new Runnable() {
                                    public void run() {

                                        if (count[0] == 100) {
                                            ringProgressDialog.setMessage(fileID);

                                        }
                                    }
                                }));


                            }
                        };

                        adapterRestUpload.uploadImageCase(new CountingTypedFile("image/jpeg", file, listener), new Callback<Response>() {
                            @Override
                            public void success(Response result, Response response) {

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
                                FileListObject fileListObject = new Gson().fromJson(JsonConvertData, FileListObject.class);
                                System.out.println(fileListObject.getData().get(0).getUrl());
                                ImageObject imageObject = new ImageObject();
                                imageObject.setInfoImage(fileListObject.getData().get(0).getUrl());
                                listImageURL.add(imageObject);
                                if (listImageURL.size() == mGridAdapter.getData().size()) {
                                    dataPlace.setInfoImageList(listImageURL);
                                    if (caseId > 0) {
                                        updateCase();
                                    } else {
                                        saveData();
                                    }

                                    System.out.println("UPload Complete!!!");
                                    ringProgressDialog.dismiss();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                System.out.println("error = [" + error + "]");

                                Toast.makeText(getApplication(), "Cannot upload file please try again", Toast.LENGTH_SHORT).show();
                                ringProgressDialog.dismiss();

                            }
                        });
                    } else if (e.getTag() == 0) {
                        ImageObject imageObject = new ImageObject();
                        imageObject.setInfoImage(e.getPath());
                        listImageURL.add(imageObject);
                        if (listImageURL.size() == mGridAdapter.getData().size()) {
                            ringProgressDialog.dismiss();
                            dataPlace.setInfoImageList(listImageURL);
                            if (caseId > 0) {
                                updateCase();
                            } else {
                                saveData();
                            }
                            System.out.println("UPload Complete!!!");
                        }
                    }
                }

            } else {
                ringProgressDialog.dismiss();
                dataPlace.setInfoImageList(listImageURL);
                if (caseId > 0) {
                    updateCase();
                } else {
                    saveData();
                }

            }
            return null;
        }
    }
}

