package com.pattaya.pattayacallcenter.member;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.member.Adapter.AdapterPager;

import java.util.concurrent.atomic.AtomicInteger;

public class MemberMainActivity extends ActionBarActivity {


    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    static final String TAG = "GCMDemo";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String PROPERTY_APP_VERSION = "appVersion";
    public ViewPager viewPager;
    PagerSlidingTabStrip tabs;
    AdapterPager adapterPager;
    // widget
    ImageButton btn;
    TextView titleTextView;
    LayoutInflater inflater;
    String SENDER_ID = "853967631150";
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String regid;
    SharedPreferences sp;
    Boolean isOfficial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_main);
        BusProvider.getInstance().register(this);
        sp = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        sp.edit().putBoolean(MasterData.SHARED_IS_MEMBER, true).commit();
        isOfficial = sp.getBoolean(MasterData.SHARED_IS_OFFICIAL, false);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.titles);
        adapterPager = new AdapterPager(getSupportFragmentManager(), this);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapterPager);
        tabs.setViewPager(viewPager);

    }



}
