package com.pattaya.pattayacallcenter.share;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kyleduo.switchbutton.switchbutton.SwitchButton;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;


public class AlertSettingActivity extends ActionBarActivity {
    ImageButton btn;
    TextView titleTextView;
    SwitchButton switchButtonAlert, switchButtonSound;

    SharedPreferences spConfig;

    SharedPreferences.Editor editor;

    boolean isAlert;
    boolean isSoundAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_setting);
        switchButtonAlert = (SwitchButton) findViewById(R.id.switch1);
        switchButtonSound = (SwitchButton) findViewById(R.id.switch2);
        //////////////////////////////////////////////////
        spConfig = getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        isAlert = spConfig.getBoolean(MasterData.SHARED_CONFIG_ALERT, true);
        isSoundAlert = spConfig.getBoolean(MasterData.SHARED_CONFIG_ALERT_SOUND, true);
        ////////////////////////////////////////////////////

        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);


        titleTextView.setText(getResources().getString(R.string.alert));
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        setClickListener();


        switchButtonAlert.setChecked(isAlert);
        switchButtonSound.setChecked(isSoundAlert);


        switchButtonAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println(isChecked);
                if (!isChecked) {
                    switchButtonSound.setChecked(isChecked);
                }
                editor = spConfig.edit();
                editor.putBoolean(MasterData.SHARED_CONFIG_ALERT, isChecked);
                editor.commit();

            }
        });

        switchButtonSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!switchButtonAlert.isChecked()) {
                    switchButtonAlert.setChecked(isChecked);
                }
                editor = spConfig.edit();
                editor.putBoolean(MasterData.SHARED_CONFIG_ALERT_SOUND, isChecked);
                editor.commit();

            }
        });


    }

    private void setClickListener() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
