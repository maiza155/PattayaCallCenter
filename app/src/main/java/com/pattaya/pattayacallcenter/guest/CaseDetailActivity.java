package com.pattaya.pattayacallcenter.guest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.ExpandableHeightGridView;
import com.pattaya.pattayacallcenter.customview.SlideMenuManage;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.UpdateResult;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseDataObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseMainObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.GetComplainObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.ImageObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CaseDetailActivity extends ActionBarActivity implements View.OnClickListener {
    AdapteGridViewShow mGridAdapter;
    ExpandableHeightGridView gridView;
    List<ImageObject> imageData;
    SlideMenuManage mlSlideMenuManage;
    View viewMenu;
    Button btnResultDetail;
    Button btnChat;
    Button btnEdit;
    Button btnDelete;
    Intent intent;
    TextView txtTitle, txtAddress, txtHidden, txtStatus, txt_empty;
    int TAG_EDIT_ACTIVITY = 809;
    // widget
    ImageButton btn;
    TextView titleTextView;
    int complainId;
    int caseId;
    int refCasesId;
    String caseName;

    CaseDataObject contactInfo;


    RestAdapter webserviceConnector = WebserviceConnector.getInstanceCase();
    RestFulQueary adapterRest = null;

    String token;
    String clientId;
    SharedPreferences sp;
    Boolean isOfficial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_detail);
        setActionBar();

        sp = getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        token = sp.getString(MasterData.SHARED_CONFIG_TOKEN, "null");
        clientId = sp.getString(MasterData.SHARED_CONFIG_CLIENT_ID, "null");

        SharedPreferences spUser = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        isOfficial = spUser.getBoolean(MasterData.SHARED_IS_OFFICIAL, false);

        Bundle data = getIntent().getExtras();
        complainId = data.getInt("id");
        caseId = data.getInt("id_case");

        btnResultDetail = (Button) findViewById(R.id.btn_resultDetail);
        btnChat = (Button) findViewById(R.id.btn_chat);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        viewMenu = findViewById(R.id.menu_slide);
        mlSlideMenuManage = new SlideMenuManage(viewMenu, this);
        imageData = new ArrayList();
        gridView = (ExpandableHeightGridView) findViewById(R.id.grdImagesView);
        gridView.setExpanded(true);
        mGridAdapter = new AdapteGridViewShow(this, R.layout.custom_gridview_image, imageData);
        gridView.setAdapter(mGridAdapter);


        /////////////////////////////////////////////////////////////////////////////
        txtTitle = (TextView) findViewById(R.id.title);
        txtAddress = (TextView) findViewById(R.id.address);
        txtHidden = (TextView) findViewById(R.id.hidden);
        txtStatus = (TextView) findViewById(R.id.status);
        txt_empty = (TextView) findViewById(R.id.txt_empty);
        gridView.setEmptyView(txt_empty);

        btn.setOnClickListener(this);
        btnResultDetail.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        adapterRest = webserviceConnector.create(RestFulQueary.class);
        getData();


        if (isOfficial) {
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }
    }


    void setActionBar() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_back, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);

        titleTextView.setText(getResources().getString(R.string.detail));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    void getData() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.save), getResources().getString(R.string.please_wait), true);
        ringProgressDialog.setCancelable(false);
        GetComplainObject getComplainObject = new GetComplainObject();
        getComplainObject.setAccessToken(token);
        getComplainObject.setClientId(clientId);
        getComplainObject.setPrimaryKeyId(complainId);
        Gson gson = new Gson();
        String data = gson.toJson(getComplainObject);
        System.out.println(data);

        adapterRest.getComplaintData(getComplainObject, new Callback<CaseMainObject>() {
            @Override
            public void success(CaseMainObject caseMainObject, Response response) {
                contactInfo = caseMainObject.getContactInfo();
                refCasesId = caseMainObject.getRefCasesId();
                System.out.println(caseMainObject.getComplaintName());
                caseName = caseMainObject.getComplaintName();
                txtTitle.setText("เรื่อง - " + caseMainObject.getComplaintName());
                txtStatus.setText(getComplainType(caseMainObject.getComplaintTypeString()));
                String hiddentype = (caseMainObject.getContactInfo().getIsAnonymousString()) ?
                        getResources().getString(R.string.hidden) : caseMainObject.getContactInfo().getNameContact();
                txtHidden.setText(hiddentype);
                //////////// Set Address /////////////
                String houseNumber = caseMainObject.getContactInfo().getHouseNumber();
                houseNumber = (houseNumber == null || houseNumber.isEmpty()) ? "" : houseNumber + " ";
                String village = caseMainObject.getContactInfo().getVillage();
                village = (village == null || village.isEmpty()) ? "" : village + " ";
                String soi = caseMainObject.getContactInfo().getSoi();
                soi = (soi == null || soi.isEmpty()) ? "" : soi + " ";
                String road = caseMainObject.getContactInfo().getRoad();
                road = (road == null || road.isEmpty()) ? "" : road + "\n";
                String more = caseMainObject.getContactInfo().getMoreInformation();
                more = (more == null || more.matches("")) ? "" : "(" + more + ")";


                String address = houseNumber + village + soi + road + more;
                if (address.matches("")) {
                    address = "ไม่มีข้อมูล";
                }
                txtAddress.setText(address);
                ////////////////////////Image View///////////////////////////
                if (caseMainObject.getContactInfo().getInfoImageList().size() > 0) {
                    //System.out.println("Have  data");
                    mGridAdapter.resetAdapter(caseMainObject.getContactInfo().getInfoImageList());

                } else {
                    mGridAdapter.resetAdapter(new ArrayList());
                    //System.out.println("no data");
                }

                ringProgressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");
                ringProgressDialog.dismiss();
                finish();
                Toast.makeText(getApplication(),
                        "Please check your internet connection.", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    void deleteData() {
        GetComplainObject getComplainObject = new GetComplainObject();
        getComplainObject.setAccessToken(token);
        getComplainObject.setClientId(clientId);
        getComplainObject.setPrimaryKeyId(complainId);
        adapterRest.deleteComplaintData(getComplainObject, new Callback<UpdateResult>() {
            @Override
            public void success(UpdateResult updateResult, Response response) {
                if (updateResult.getResult()) {
                    finish();
                    Toast.makeText(getApplication(),
                            "This case is deleted success.", Toast.LENGTH_SHORT)
                            .show();
                    BusProvider.getInstance().post("update_case_list");
                } else {
                    Toast.makeText(getApplication(),
                            "Please check your internet connection.", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");
                Toast.makeText(getApplication(),
                        "Please check your internet connection.", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }


    String getComplainType(int type) {
        String str;
        switch (type) {
            case 0:
                str = getResources().getString(R.string.complaintType_0);
                break;
            case 1:
                str = getResources().getString(R.string.complaintType_1);
                break;
            case 2:
                str = getResources().getString(R.string.complaintType_2);
                break;
            case 3:
                str = getResources().getString(R.string.complaintType_3);
                break;
            default:
                str = getResources().getString(R.string.complaintType_0);
                break;
        }
        return str;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_case_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        View locButton = this.findViewById(R.id.action_add);
        mlSlideMenuManage.setmViewClick(locButton);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if (btnResultDetail == v) {
            intent = new Intent(getApplicationContext(), CaseResultActivity.class);
            intent.putExtra("id", caseId);
            startActivity(intent);
            mlSlideMenuManage.stateShowMenu(mlSlideMenuManage.SETTING_MENU_HIDE);
        } else if (btnChat == v) {
            intent = new Intent(getApplicationContext(), CaseChatActivity.class);
            intent.putExtra("id", caseId);
            intent.putExtra("complainid", complainId);
            intent.putExtra("casename", caseName);
            startActivity(intent);
            mlSlideMenuManage.stateShowMenu(mlSlideMenuManage.SETTING_MENU_HIDE);

        } else if (btnEdit == v) {
            intent = new Intent(getApplicationContext(), CaseAddAndEditActivity.class);
            intent.putExtra("data", complainId);
            startActivityForResult(intent, TAG_EDIT_ACTIVITY);

            mlSlideMenuManage.stateShowMenu(mlSlideMenuManage.SETTING_MENU_HIDE);
        } else if (btnDelete == v) {
            mlSlideMenuManage.stateShowMenu(mlSlideMenuManage.SETTING_MENU_HIDE);
            if (refCasesId <= 0) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setTitle(getResources().getString(R.string.confirm_delete));
                alert.setMessage(caseName);

                alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // do nothing
                        deleteData();
                    }
                })
                        .show();

            } else {
                Toast.makeText(getApplication(),
                        "can't delete this case.", Toast.LENGTH_SHORT)
                        .show();
            }


        } else if (v == btn) {
            finish();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAG_EDIT_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                getData();
            }
        }
    }
}
