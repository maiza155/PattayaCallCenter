package com.pattaya.pattayacallcenter.member;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.ExpandableHeightGridView;
import com.pattaya.pattayacallcenter.guest.AdapteGridViewShow;
import com.pattaya.pattayacallcenter.member.data.ForwardObject;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseAssignObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseDataMemberObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseDataObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.GetComplainObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.ImageObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.pattaya.pattayacallcenter.R.drawable.asset_icon_status_flag;
import static com.pattaya.pattayacallcenter.R.drawable.asset_icon_status_gray;
import static com.pattaya.pattayacallcenter.R.drawable.custom_date_bg_green;
import static com.pattaya.pattayacallcenter.R.drawable.custom_date_bg_red;
import static com.pattaya.pattayacallcenter.R.drawable.custom_date_bg_yellow;

public class CaseDetailMemberActivity extends ActionBarActivity {
    private static SharedPreferences spConfig;
    private static SharedPreferences sp;
    private ImageButton btn;
    private TextView titleTextView;
    private LinearLayout layout;
    private TextView txtTitle, txtNo, txtTo, txtProgress, txtHidden, txtPlace, txtemply;
    private ProgressBar bar;
    private AdapteGridViewShow mGridAdapter;
    private ExpandableHeightGridView gridView;
    private List<ImageObject> imageData;
    private RestAdapter webserviceConnector = WebserviceConnector.getInstanceCase();
    private RestFulQueary adapterRest = null;
    private CaseDataObject dataPlace;
    private int caseId;
    private String token;
    private String clientId;
    private int STATE_FORWARD = 0;
    private ArrayList<ForwardObject> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_detail_member);


        setActionBar();
        adapterRest = webserviceConnector.create(RestFulQueary.class);

        txtTitle = (TextView) findViewById(R.id.title);
        txtTo = (TextView) findViewById(R.id.to);
        txtProgress = (TextView) findViewById(R.id.txtprogress);
        txtNo = (TextView) findViewById(R.id.no);
        txtHidden = (TextView) findViewById(R.id.hidden);
        txtPlace = (TextView) findViewById(R.id.place);
        bar = (ProgressBar) findViewById(R.id.progress);
        txtemply = (TextView) findViewById(R.id.txt_empty);
        layout = (LinearLayout) findViewById(R.id.imageLayout);

        imageData = new ArrayList<>();
        gridView = (ExpandableHeightGridView) findViewById(R.id.grdImagesView);
        gridView.setExpanded(true);
        mGridAdapter = new AdapteGridViewShow(this, R.layout.custom_gridview_image, imageData);
        gridView.setAdapter(mGridAdapter);
        gridView.setEmptyView(txtemply);
        init();

    }

    void init() {
        Intent intent = getIntent();
        caseId = intent.getIntExtra("id", 0);
        if (caseId > 0) {
            System.out.println(caseId);
            getData();

        }

        spConfig = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        token = spConfig.getString(MasterData.SHARED_CONFIG_TOKEN, null);
        clientId = spConfig.getString(MasterData.SHARED_CONFIG_CLIENT_ID, null);

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

    void setActionBar() {
        /** Set Title Center Actionbar */
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);

        titleTextView.setText(getResources().getString(R.string.menu_case_member_detail));
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


    void getData() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.load_data), getResources().getString(R.string.please_wait), true);
        ringProgressDialog.setCancelable(false);
        GetComplainObject getComplainObject = new GetComplainObject();
        getComplainObject.setAccessToken(token);
        getComplainObject.setClientId(clientId);
        getComplainObject.setPrimaryKeyId(caseId);

        adapterRest.getCaseData(getComplainObject, new Callback<CaseDataMemberObject>() {
            @Override
            public void success(CaseDataMemberObject caseDataMemberObject, Response response) {
                System.out.println("caseDataMemberObject = [" + caseDataMemberObject + "], response = [" + response + "]");
                arrayList = new ArrayList();
                STATE_FORWARD = caseDataMemberObject.getTypeCaseAssign();
                txtTitle.setText(caseDataMemberObject.getCasesName());
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
                ///////////////////////////////////////////////////////////////////////////////////
                txtProgress.setText("" + caseDataMemberObject.getPercentSuccess() + "%");
                bar.setProgress(caseDataMemberObject.getPercentSuccess());


                ///////////////////////////////////////////////////////////////////////////////////
                layout.removeAllViews();

                if (caseDataMemberObject.getCountDate() != null
                        && !caseDataMemberObject.getCountDate().isEmpty()) {
                    TextView progress = new TextView(CaseDetailMemberActivity.this);
                    progress.setText(caseDataMemberObject.getCountDate());
                    progress.setTextColor(Color.WHITE);
                    progress.setPadding(5, 0, 0, 0);
                    progress.setTextSize(16);

                    if (caseDataMemberObject.getPriority() == null || caseDataMemberObject.getPriority() == 0) {
                        progress.setBackgroundResource(custom_date_bg_green);
                        layout.addView(progress);
                    } else if (caseDataMemberObject.getPriority() == 1) {
                        progress.setBackgroundResource(custom_date_bg_yellow);
                        layout.addView(progress);
                    } else if (caseDataMemberObject.getPriority() == 2) {
                        progress.setBackgroundResource(custom_date_bg_red);
                        layout.addView(progress);
                    }
                }

                if (caseDataMemberObject.getIsFollowUp()) {
                    ImageView image = new ImageView(CaseDetailMemberActivity.this);
                    Drawable drawable = getResources().getDrawable(asset_icon_status_flag);
                    image.setImageDrawable(drawable);
                    layout.addView(image);
                }

                if (caseDataMemberObject.getIsAction()) {
                    ImageView image = new ImageView(CaseDetailMemberActivity.this);
                    Drawable drawable = getResources().getDrawable(asset_icon_status_gray);
                    image.setImageDrawable(drawable);
                    layout.addView(image);
                } else {
                    ImageView image = new ImageView(CaseDetailMemberActivity.this);
                    Drawable drawable = getResources().getDrawable(asset_icon_status_gray);
                    image.setImageDrawable(drawable);
                    layout.addView(image);
                }


                ////////////////////////////////////////////////////////////////////////////////////
                if (caseDataMemberObject.getContactInfo() != null) {
                    dataPlace = caseDataMemberObject.getContactInfo();
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
                    String hiddentype = (dataPlace.getIsAnonymousString()) ?
                            getResources().getString(R.string.hidden) : dataPlace.getNameContact();
                    txtHidden.setText(hiddentype);
                }


                if (dataPlace.getInfoImageList().size() > 0) {
                    System.out.println("Have  data");
                    //mGridAdapter.resetAdapter(caseMainObject.getContactInfo().getInfoImageList());
                    imageData = dataPlace.getInfoImageList();
                    mGridAdapter.resetAdapter(imageData);
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
}
