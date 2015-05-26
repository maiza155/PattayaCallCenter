package com.pattaya.pattayacallcenter.member;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.XMPPManage;
import com.pattaya.pattayacallcenter.chat.XMPPManageOfficial;
import com.pattaya.pattayacallcenter.chat.XMPPService;
import com.pattaya.pattayacallcenter.chat.XMPPServiceOfficial;
import com.pattaya.pattayacallcenter.share.AlertSettingActivity;
import com.pattaya.pattayacallcenter.share.ChangePasswordActivity;
import com.pattaya.pattayacallcenter.share.LoginActivity;
import com.pattaya.pattayacallcenter.share.ProfileActivity;


public class SettingFragment extends Fragment implements View.OnClickListener {


    static SettingFragment fragment = null;
    TextView titleTextView;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editorUser;
    Button btnOwn;
    Button btnAlert;
    Button btnOrganize;
    Button btnPass;
    Button btnClose;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance() {
        if (fragment == null) {
            fragment = new SettingFragment();
        }


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SharedPreferences spUser = getActivity().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        sp = getActivity().getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        editor = sp.edit();
        editorUser = spUser.edit();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        btnOwn = (Button) root.findViewById(R.id.btn_own);
        btnAlert = (Button) root.findViewById(R.id.btn_alert);
        btnOrganize = (Button) root.findViewById(R.id.btn_organize);
        btnPass = (Button) root.findViewById(R.id.btn_pass);
        btnClose = (Button) root.findViewById(R.id.btn_exit);
        btnClose.setOnClickListener(this);
        btnOrganize.setOnClickListener(this);
        btnAlert.setOnClickListener(this);
        btnPass.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnOwn.setOnClickListener(this);


        return root;
    }

    void setActionBar(Context actionBar) {
        /** Set Title Center Actionbar */
        LayoutInflater inflater = LayoutInflater.from(actionBar);
        View v = inflater.inflate(R.layout.custom_actionbar_none, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        // btn = (ImageButton) v.findViewById(R.id.add_left_menu);

        titleTextView.setText(getResources().getString(R.string.setting));
        ((MemberMainActivity) actionBar).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((MemberMainActivity) actionBar).getSupportActionBar().setCustomView(v);
        ((MemberMainActivity) actionBar).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MemberMainActivity) actionBar).getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        setActionBar(getActivity());


    }


    @Override
    public void onClick(View v) {
        if (v == btnClose) {
            XMPPManage.getInstance().disConnect();
            XMPPManageOfficial.getInstance().disConnect();
            editor.putString(MasterData.SHARED_CONFIG_TOKEN, null);
            editor.commit();
            try {
                editorUser.putBoolean(MasterData.SHARED_IS_OFFICIAL, false);
                editorUser.commit();
                v.getContext().stopService(new Intent(v.getContext(), XMPPService.class));
                v.getContext().stopService(new Intent(v.getContext(), XMPPServiceOfficial.class));
                LoginManager.getInstance().logOut();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();


        } else if (v == btnAlert) {
            Intent intent = new Intent(getActivity(), AlertSettingActivity.class);
            startActivity(intent);

        } else if (v == btnOrganize) {
            Intent intent = new Intent(getActivity(), OrganizeMemberActivity.class);
            startActivity(intent);

        } else if (v == btnPass) {

            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);

        } else if (v == btnOwn) {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        }

    }
}
