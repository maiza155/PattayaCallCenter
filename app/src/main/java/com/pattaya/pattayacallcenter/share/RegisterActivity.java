package com.pattaya.pattayacallcenter.share;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.pattaya.pattayacallcenter.BaseActivity;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.RegistObject;
import com.pattaya.pattayacallcenter.webservice.object.UpdateResult;

import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RegisterActivity extends BaseActivity implements AlertMessageFragment.OnFragmentInteractionListener {
    Button btnSave;
    ImageButton btn;
    TextView titleTextView;
    EditText email, name, pass, repass;
    RestAdapter webserviceConnector = WebserviceConnector.getInstance();
    final RestFulQueary queary = webserviceConnector.create(RestFulQueary.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = (EditText) findViewById(R.id.txt_email);
        name = (EditText) findViewById(R.id.txt_userName);
        pass = (EditText) findViewById(R.id.txt_password);
        repass = (EditText) findViewById(R.id.txt_repassword);


        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        btnSave = (Button) findViewById(R.id.btn_saveRegister);

        titleTextView.setText(getResources().getString(R.string.register));
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        setClickListener();
    }

    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    void regist() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.register), getResources().getString(R.string.please_wait), true);
        ringProgressDialog.setCancelable(false);
        RegistObject registObject = new RegistObject();

        registObject.setUsername(email.getText().toString());
        registObject.setFristname(name.getText().toString());

        registObject.setUserPassword(pass.getText().toString());
        registObject.setEmail(email.getText().toString());

        queary.sendRegist(registObject, new Callback<UpdateResult>() {
            @Override
            public void success(UpdateResult updateResult, Response response) {

                if (updateResult.getResult()) {
                    titleTextView.setText(getResources().getString(R.string.register_success));
                    LinearLayout ll = (LinearLayout) findViewById(R.id.container);
                    ll.removeAllViews();
                    AlertMessageFragment fragment = new AlertMessageFragment();
                    setFragment(fragment, 0);
                    btnSave.setVisibility(View.GONE);
                } else {
                    alertDialogFailtoServer(getResources().getString(R.string.email_is_exist));
                }
                ringProgressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {

                alertDialogFailtoServer(getResources().getString(R.string.cant_connect_server));
                ringProgressDialog.dismiss();

            }
        });

    }

    void alertDialogFailtoServer(String msg) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(getResources().getString(R.string.register));
        alertDialog.setMessage(msg);
        alertDialog.setButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                //  finish();
            }
        });
        alertDialog.show();

    }


    void setClickListener() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().matches("") || !EMAIL_ADDRESS_PATTERN.matcher(email.getText().toString()).matches()) {
                    YoYo.with(Techniques.Shake).duration(1000).playOn(email);
                    Toast.makeText(getApplication(),
                            getResources().getString(R.string.email_incorrect), Toast.LENGTH_SHORT)
                            .show();

                } else if (name.getText().toString().matches("")) {
                    YoYo.with(Techniques.Shake).duration(1000).playOn(name);
                    Toast.makeText(getApplication(),
                            getResources().getString(R.string.please_enter_fill), Toast.LENGTH_SHORT)
                            .show();
                } else if (pass.getText().toString().matches("") && pass.length() != 6) {
                    YoYo.with(Techniques.Shake).duration(1000).playOn(pass);
                    Toast.makeText(getApplication(), "รหัสผ่านต้องมีอย่างน้อย 6 อักขระ", Toast.LENGTH_SHORT)
                            .show();
                } else if (!repass.getText().toString().matches(pass.getText().toString())) {
                    YoYo.with(Techniques.Shake).duration(1000).playOn(repass);
                    Toast.makeText(getApplication(),
                            getResources().getString(R.string.new_pass_not_match), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    regist();

                }
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
