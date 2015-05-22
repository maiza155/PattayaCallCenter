package com.pattaya.pattayacallcenter.guest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.login.LoginManager;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.XMPPManage;
import com.pattaya.pattayacallcenter.chat.XMPPService;
import com.pattaya.pattayacallcenter.chat.jsonobject.MapProperty;
import com.pattaya.pattayacallcenter.chat.jsonobject.Property;
import com.pattaya.pattayacallcenter.chat.jsonobject.UserProperty;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.share.LoginActivity;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.UpdateResult;
import com.pattaya.pattayacallcenter.webservice.object.organize.AcceptInviteObject;
import com.pattaya.pattayacallcenter.webservice.object.organize.GetInviteOrgObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OrganizeActivity extends ActionBarActivity implements View.OnClickListener {
    final RestAdapter restAdapterOpenFireJson = RestAdapterOpenFire.getInstanceJson();
    final OpenfireQueary openfireQueary = restAdapterOpenFireJson.create(OpenfireQueary.class);
    Button btnCount;
    Button btnAccept;
    Button btnNoAccept;
    ImageButton btn;
    TextView titleTextView;
    EditText txt_name;
    EditText txt_lastname;
    EditText txt_iccard;
    FrameLayout frame;
    SharedPreferences sp;
    SharedPreferences spConfig;
    SharedPreferences.Editor editor;
    int userId;
    RestAdapter webserviceConnector = WebserviceConnector.getInstance();
    final RestFulQueary queary = webserviceConnector.create(RestFulQueary.class);
    int orgId;
    String jid;
    String pic;
    String fristName;
    String lastName;
    String token;
    String clientId = "android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organize);
        spConfig = getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        sp = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        userId = sp.getInt(MasterData.SHARED_USER_USER_ID, -10);
        fristName = sp.getString(MasterData.SHARED_USER_FRIST_NAME, null);
        lastName = sp.getString(MasterData.SHARED_USER_LAST_NAME, null);
        jid = sp.getString(MasterData.SHARED_USER_JID, null);
        pic = sp.getString(MasterData.SHARED_USER_IMAGE, "IMAGE FAIL..........");


        token = spConfig.getString(MasterData.SHARED_CONFIG_TOKEN, null);
        clientId = spConfig.getString(MasterData.SHARED_CONFIG_CLIENT_ID, null);
        System.out.println(userId);

        txt_iccard = (EditText) findViewById(R.id.edit_text_id_card);
        txt_name = (EditText) findViewById(R.id.edit_text_name);
        txt_lastname = (EditText) findViewById(R.id.edit_text_surname);
        frame = (FrameLayout) findViewById(R.id.frame);

        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        btnCount = (Button) findViewById(R.id.btn_count);


        btnAccept = (Button) findViewById(R.id.btn_accept);
        btnNoAccept = (Button) findViewById(R.id.btn_noaccept);
        titleTextView.setText(getResources().getString(R.string.org));
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        btn.setOnClickListener(this);
        btnAccept.setOnClickListener(this);
        btnNoAccept.setOnClickListener(this);
        btnCount.setOnClickListener(this);

        if (fristName != null) {
            txt_name.setText(fristName);
        }
        if (lastName != null) {
            txt_lastname.setText(lastName);
        }


        getInvite();
    }

    void acceptInvite(final Boolean bool) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(OrganizeActivity.this, null, getResources().getString(R.string.please_wait), true);
        ringProgressDialog.setCancelable(false);
        final AcceptInviteObject acceptInviteObject = new AcceptInviteObject();
        acceptInviteObject.setFirstname(txt_name.getText().toString());
        acceptInviteObject.setLastname(txt_lastname.getText().toString());
        acceptInviteObject.setIdCard(txt_iccard.getText().toString());
        acceptInviteObject.setOrgId(orgId);
        acceptInviteObject.setAccept(bool);
        acceptInviteObject.setUserId(userId);

        String username = jid.split("@")[0];
        UserProperty userProperty = new UserProperty();
        userProperty.setUsername(username);
        userProperty.setName(txt_name.getText().toString() + " " + txt_lastname.getText().toString());

        List<MapProperty> mapProperties = new ArrayList<>();
        MapProperty mapProperty = new MapProperty();
        mapProperty.setKey("userImage");
        mapProperty.setValue(pic);
        mapProperties.add(mapProperty);

        Property property = new Property(mapProperties);

        userProperty.setProperties(property);
        openfireQueary.updateProperty(username, userProperty, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                queary.acceptInvite(acceptInviteObject, new Callback<UpdateResult>() {
                    @Override
                    public void success(UpdateResult updateResult, Response response) {
                        System.out.println("updateResult = [" + updateResult.getResult() + "], response = [" + response + "]");

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                if (bool) {
                                    XMPPManage.getInstance().disConnect();
                                    editor = sp.edit();
                                    editor.putInt("USER_ID", -10);
                                    editor.commit();
                                    editor = spConfig.edit();
                                    editor.putString("TOKEN", null);
                                    editor.commit();


                                    OrganizeActivity.this.stopService(new Intent(OrganizeActivity.this, XMPPService.class));

                                    LoginManager.getInstance().logOut();


                                    Intent i = new Intent();
                                    setResult(Activity.RESULT_OK, i);


                                    Intent intent = new Intent(OrganizeActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    ringProgressDialog.dismiss();
                                    finish();
                                } else {
                                    Intent i = new Intent();
                                    i.putExtra("state", false);
                                    setResult(Activity.RESULT_OK, i);
                                    ringProgressDialog.dismiss();
                                    finish();

                                }


                            }
                        }, 3000);

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println("error = [" + error + "]");
                        ringProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.cant_connect_server), Toast.LENGTH_SHORT)
                                .show();
                        //alertDialogFailtoServer(getResources().getString(R.string.cant_connect_server));

                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                ringProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.cant_connect_server), Toast.LENGTH_SHORT)
                        .show();
            }
        });


    }


    void getInvite() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.load_data), getResources().getString(R.string.please_wait), true);
        ringProgressDialog.setCancelable(false);
        System.out.println("userId   " + userId);
        queary.getInviteOrg(userId, new Callback<GetInviteOrgObject>() {
            @Override
            public void success(GetInviteOrgObject getInviteOrgObject, Response response) {
                ringProgressDialog.dismiss();
                System.out.println("getInviteOrgObject = [" + getInviteOrgObject.getOrgId() + "], response = [" + response + "]");
                if (getInviteOrgObject.getOrgId() == 0) {
                    //alertDialogFailtoServer("No invite");
                    frame.setVisibility(View.VISIBLE);
                }
                orgId = getInviteOrgObject.getOrgId();
                btnCount.setText(getInviteOrgObject.getOrgName());
            }

            @Override
            public void failure(RetrofitError error) {
                ringProgressDialog.dismiss();
                System.out.println("error = [" + error + "]");
                alertDialogFailtoServer(getResources().getString(R.string.cant_connect_server));
            }
        });
    }

    void alertDialogFailtoServer(String msg) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(getResources().getString(R.string.invite_org));
        alertDialog.setMessage(msg);
        alertDialog.setButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                finish();
            }
        });
        alertDialog.show();

    }


    @Override
    public void onClick(View v) {
        if (v == btnAccept) {
            if (txt_name.getText().toString().matches("")) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(txt_name);
                Toast.makeText(getApplication(),
                        getResources().getString(R.string.please_enter_fill), Toast.LENGTH_SHORT)
                        .show();

            } else if (txt_lastname.getText().toString().matches("")) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(txt_lastname);
                Toast.makeText(getApplication(),
                        getResources().getString(R.string.please_enter_fill), Toast.LENGTH_SHORT)
                        .show();

            } else if (txt_iccard.getText().toString().matches("") || txt_iccard.getText().length() != 13) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(txt_iccard);
                Toast.makeText(getApplication(),
                        getResources().getString(R.string.idcard_incorrect), Toast.LENGTH_SHORT)
                        .show();
            } else {
                UserProperty userProperty = new UserProperty();
                userProperty.setName(txt_name.getText().toString() + " " + txt_lastname.getText().toString());
                if (jid != null) {
                    String username = jid.split("@")[0];
                    userProperty.setUsername(username);
                    openfireQueary.updateProperty(username, userProperty, new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {
                            acceptInvite(true);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getApplication(),
                                    getResources().getString(R.string.cant_connect_server), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });


                } else {
                    acceptInvite(true);
                }


            }


        } else if (v == btnNoAccept) {
            acceptInvite(false);


        } else if (v == btnCount) {

        } else if (v == btn) {
            finish();

        }

    }
}
