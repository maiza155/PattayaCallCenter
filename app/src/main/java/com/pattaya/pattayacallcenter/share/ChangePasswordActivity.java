package com.pattaya.pattayacallcenter.share;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.ChangePassObject;
import com.pattaya.pattayacallcenter.webservice.object.UpdateResult;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ChangePasswordActivity extends ActionBarActivity implements View.OnClickListener {


    ImageButton btn;

    EditText txtOldPass;
    EditText txtNewPass;
    EditText txtReNewPass;
    Button btnSave;
    SharedPreferences spConfig;
    SharedPreferences sp;

    String username;
    String token;
    String clientId;
    RestAdapter webserviceConnector = WebserviceConnector.getInstance();
    RestFulQueary adapterRest = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        txtNewPass = (EditText) findViewById(R.id.txt_newPassword);
        txtReNewPass = (EditText) findViewById(R.id.txt_renewPassword);
        txtOldPass = (EditText) findViewById(R.id.txt_oldPassword);
        btnSave = (Button) findViewById(R.id.btn_save);


        btnSave.setOnClickListener(this);

        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        TextView titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        titleTextView.setText(getResources().getString(R.string.change_pass));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        btn.setOnClickListener(this);

        spConfig = getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        token = spConfig.getString(MasterData.SHARED_CONFIG_TOKEN, null);
        clientId = spConfig.getString(MasterData.SHARED_CONFIG_CLIENT_ID, null);
        sp = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        username = sp.getString(MasterData.SHARED_USER_USERNAME, null);
    }


    @Override
    public void onClick(View v) {
        if (v == btn) {
            finish();
        } else if (v == btnSave) {
            if (txtOldPass.getText().toString().matches("")) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(txtOldPass);
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.please_enter_fill), Toast.LENGTH_SHORT).show();
            } else if (txtNewPass.getText().toString().matches("") && txtNewPass.getText().toString().length() != 6) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(txtNewPass);
                Toast.makeText(this, "รหัสผ่านต้องมีอย่างน้อย 6 อักขระ", Toast.LENGTH_SHORT).show();
            } else if (txtReNewPass.getText().toString().matches("")) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(txtReNewPass);
                Toast.makeText(getApplication(),
                        getResources().getString(R.string.please_enter_fill), Toast.LENGTH_SHORT).show();
            } else if (!txtReNewPass.getText().toString().matches(txtNewPass.getText().toString())) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(txtReNewPass);
                Toast.makeText(getApplication(),
                        getResources().getString(R.string.new_pass_not_match), Toast.LENGTH_SHORT).show();
            } else {
                restAdapter();

            }

        }
    }

    private void restAdapter() {
        ChangePassObject changePassObject = new ChangePassObject();
        changePassObject.setUsername(username);
        changePassObject.setAccessToken(token);
        changePassObject.setClientId(clientId);
        changePassObject.setNewPassword(txtNewPass.getText().toString());
        changePassObject.setOldPassword(txtOldPass.getText().toString());

        adapterRest = webserviceConnector.create(RestFulQueary.class);
        adapterRest.resetPass(changePassObject, new Callback<UpdateResult>() {
            @Override
            public void success(UpdateResult updateResult, Response response) {

                if (updateResult.getResult()) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.update_data_success), Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    alertDialogFailtoServer(getResources().getString(R.string.pass_incorrect));

                }
            }

            @Override
            public void failure(RetrofitError error) {
                alertDialogFailtoServer(getResources().getString(R.string.cant_connect_server));
            }
        });


    }

    void alertDialogFailtoServer(String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getResources().getString(R.string.change_pass));
        alertDialog.setMessage(message);
        alertDialog.setButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();

            }
        });
        alertDialog.show();
    }

}
