package com.pattaya.pattayacallcenter.share;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.pattaya.pattayacallcenter.webservice.object.UpdateResult;

import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ForgetPasswordActivity extends BaseActivity implements AlertMessageFragment.OnFragmentInteractionListener {
    Button btnSave;
    ImageButton btn;
    TextView titleTextView;
    EditText email;
    RestAdapter webserviceConnector = WebserviceConnector.getInstance();
    final RestFulQueary queary = webserviceConnector.create(RestFulQueary.class);
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        email = (EditText) findViewById(R.id.txt_userEmail);

        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        btnSave = (Button) findViewById(R.id.btn_seekPassword);
        titleTextView.setText(R.string.forget_pass);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        setClickListener();
    }

    private void setClickListener() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //titleTextView.setText("ลงทะเบียนสำเร็จ");
                if (!EMAIL_ADDRESS_PATTERN.matcher(email.getText().toString()).matches()) {
                    YoYo.with(Techniques.Shake).duration(1000).playOn(email);
                    Toast.makeText(getApplication(),
                            getResources().getString(R.string.email_incorrect), Toast.LENGTH_SHORT)
                            .show();

                } else {
                    final ProgressDialog ringProgressDialog = ProgressDialog.show(context, getResources().getString(R.string.update_data), getResources().getString(R.string.please_wait), true);
                    ringProgressDialog.setCancelable(false);

                    queary.forgetPass(email.getText().toString(), new Callback<UpdateResult>() {
                        @Override
                        public void success(UpdateResult updateResult, Response response) {
                            if (updateResult.getResult()) {
                                LinearLayout ll = (LinearLayout) findViewById(R.id.container);
                                ll.removeAllViews();
                                AlertMessageFragment fragment = new AlertMessageFragment();
                                fragment.setTextMsg(getResources().getString(R.string.forget_pass_message));
                                setFragment(fragment, 0);
                            } else {
                                alertDialogFailtoServer(getResources().getString(R.string.email_incorrect));
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

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    void alertDialogFailtoServer(String msg) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(getResources().getString(R.string.forget_pass));
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


    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
