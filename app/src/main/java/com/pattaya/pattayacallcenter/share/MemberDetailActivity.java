package com.pattaya.pattayacallcenter.share;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.Data.Users;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.XMPPManage;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.member.ChatActivity;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.AccessUserObject;
import com.pattaya.pattayacallcenter.webservice.object.GetUserObject;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MemberDetailActivity extends ActionBarActivity {
    // widget
    ImageButton btn;
    TextView titleTextView;
    boolean isFavor = false;
    Button chat;
    Button favorite;
    Intent intent;
    Users otherUser;
    RestAdapter webserviceConnector = WebserviceConnector.getInstance();
    RestFulQueary adapterRest = null;
    SharedPreferences spConfig, sp;
    String jid;
    String token;
    Context context = this;
    String clientId = "android";
    int userId;

    TextView txtName, txtOrg, txtEmail;
    RoundedImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        ////////////////////////////////////////////////////////////////////
        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        txtOrg = (TextView) findViewById(R.id.org);
        image = (RoundedImageView) findViewById(R.id.image);


        //////////////////////////////////////////////////////////////////
        spConfig = getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        sp = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);

        token = spConfig.getString(MasterData.SHARED_CONFIG_TOKEN, null);
        clientId = spConfig.getString(MasterData.SHARED_CONFIG_CLIENT_ID, null);
        userId = sp.getInt(MasterData.SHARED_USER_USER_ID, -10);

        adapterRest = webserviceConnector.create(RestFulQueary.class);
        Bundle data = getIntent().getExtras();
        otherUser = data.getParcelable("user");

        isFavor = (otherUser.getFavorite()) ? true : false;

        jid = otherUser.getJid();

        chat = (Button) findViewById(R.id.btn_chat);
        favorite = (Button) findViewById(R.id.btn_favorite);
        setActionBar();
        ClickBtn();
        setFavorite();
        getUserData();
    }

    void getUserData() {
        GetUserObject getUserObject = new GetUserObject(jid, token, clientId);
        adapterRest.getUser(getUserObject, new Callback<AccessUserObject>() {
            @Override
            public void success(AccessUserObject accessUserObject, Response response) {
                System.out.println(accessUserObject.getFirstname());
                txtName.setText(accessUserObject.getDisplayName());
                txtEmail.setText(accessUserObject.getEmail());
                txtOrg.setText(accessUserObject.getOrgName());
                String image = (accessUserObject.getUserImage() == null || accessUserObject.getUserImage().isEmpty()) ? "No Image" : accessUserObject.getUserImage();
                setImageUrl(image);
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");
                Toast.makeText(getApplication(),
                        getResources().getString(R.string.cant_connect_server), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }


    void setImageUrl(String url) {
        Glide.with(context).load(url)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .override(100, 100)
                .into(image);


    }

    void setActionBar() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        btn.setPadding(0, 0, 0, 0);
        titleTextView.setText(getResources().getString(R.string.detail));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        setClickLisener();
    }

    private void setClickLisener() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ClickBtn() {
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("user", otherUser);
                startActivity(intent);
            }
        });
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog ringProgressDialog = new ProgressDialog(v.getContext());
                ringProgressDialog.setMessage("updating");
                ringProgressDialog.setCancelable(true);
                ringProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                ringProgressDialog.setIndeterminate(true);
                ringProgressDialog.show();
                if (isFavor) {
                    Boolean bool = XMPPManage.getInstance().removeFavourite(jid);
                    System.out.println("No-Favorite");
                    ringProgressDialog.dismiss();
                    if (bool) {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.update_data_success), Toast.LENGTH_SHORT)
                                .show();
                        BusProvider.getInstance().post("add_roster_complete");
                        isFavor = false;
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.cant_connect_server), Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Boolean bool = XMPPManage.getInstance().updateFavourite(jid);
                    System.out.println("Favorite");
                    ringProgressDialog.dismiss();
                    if (bool) {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.update_data_success), Toast.LENGTH_SHORT)
                                .show();
                        BusProvider.getInstance().post("add_roster_complete");
                        isFavor = true;
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.cant_connect_server), Toast.LENGTH_SHORT)
                                .show();
                    }

                }

                setFavorite();
            }
        });
    }


    void setFavorite() {
        if (isFavor) {
            favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_on, 0, 0, 0);
            favorite.setText(getResources().getString(R.string.friend_favor));
        } else {
            favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_off, 0, 0, 0);
            favorite.setText(getResources().getString(R.string.friend_normal));
        }

    }


}
