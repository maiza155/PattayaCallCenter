package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.member.Adapter.AdapterListForward;
import com.pattaya.pattayacallcenter.member.data.ForwardObject;

import java.util.ArrayList;

public class CaseForwardToActivity extends ActionBarActivity implements View.OnClickListener {

    private Button btnUser;
    private Button btnOrganize;
    private ListView listView;
    private ArrayList<ForwardObject> arrayList;


    private AdapterListForward adapter;
    private int STATE;
    private int TAG_USERDATA = 200;
    private int TAG_ORGANIZEDATA = 201;
    private String text = "";
    // widget
    private ImageButton btn;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_add_and_edit_to);
        arrayList = new ArrayList<>();
        init();
        setActionBar();

        // init widget
        btnUser = (Button) findViewById(R.id.btn_user);
        btnOrganize = (Button) findViewById(R.id.btn_organize);
        listView = (ListView) findViewById(R.id.list_view);

        /** //////////////////////////////////////////////////////// */

        btn.setOnClickListener(this);
        btnUser.setOnClickListener(this);
        btnOrganize.setOnClickListener(this);

        // set listView Adapter
        adapter = new AdapterListForward(this, arrayList);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.empty));

    }

    void init() {
        arrayList = getIntent().getParcelableArrayListExtra("data");
        int state = getIntent().getIntExtra("state", 0);
        text = getIntent().getStringExtra("text");
        STATE = (state == 2) ? TAG_USERDATA : (state == 1) ? TAG_ORGANIZEDATA : 0;
    }

    void setActionBar() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);

        titleTextView.setText(getResources().getString(R.string.member_case_forward_respond));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public void onClick(View v) {
        if (v == btnOrganize) {
            Intent intent = new Intent(getApplication(), CaseForwardToOrganize.class);
            if (STATE == TAG_ORGANIZEDATA) {
                intent.putParcelableArrayListExtra("data", arrayList);
            }
            startActivityForResult(intent, TAG_ORGANIZEDATA);

        } else if (v == btnUser) {
            Intent intent = new Intent(getApplication(), CaseForwardToUser.class);
            if (STATE == TAG_USERDATA) {
                intent.putParcelableArrayListExtra("data", arrayList);
            }
            startActivityForResult(intent, TAG_USERDATA);

        } else if (v == btn) {
            Intent i = new Intent();
            int state = (STATE == TAG_ORGANIZEDATA) ? 1 : (STATE == TAG_USERDATA) ? 2 : 0;
            i.putExtra("state", state);
            i.putParcelableArrayListExtra("data", arrayList);
            setResult(Activity.RESULT_OK, i);
            finish();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TAG_USERDATA) {
            if (resultCode == Activity.RESULT_OK) {
                arrayList = data.getParcelableArrayListExtra("data");
                adapter.resetAdapter(arrayList);
                STATE = TAG_USERDATA;
            }
        }

        if (requestCode == TAG_ORGANIZEDATA) {
            if (resultCode == Activity.RESULT_OK) {
                arrayList = data.getParcelableArrayListExtra("data");
                adapter.resetAdapter(arrayList);
                STATE = TAG_ORGANIZEDATA;
            }
        }


    }
}
