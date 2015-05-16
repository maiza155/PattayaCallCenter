package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.pattaya.pattayacallcenter.R;

public class CaseAdvanceSearchActivity extends ActionBarActivity {
    TextView titleTextView;
    ImageButton btn;
    Button btnCommit;

    Spinner spinner1, spinner2;
    EditText txtSearch;
    Boolean action = null;
    int completed = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_advance_search);
        setActionBar();
        txtSearch = (EditText) findViewById(R.id.txt_search);
        spinner2 = (Spinner) findViewById(R.id.spn_due);
        spinner1 = (Spinner) findViewById(R.id.spn_action);
        btnCommit = (Button) findViewById(R.id.btn_search);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    if (position == 2) {
                        action = true;
                    } else {
                        action = false;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    completed = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("action", action);
                i.putExtra("completed", completed);
                i.putExtra("text", txtSearch.getText().toString());
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });


    }
    void setActionBar() {
        /** Set Title Center Actionbar */
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        btn.setPadding(0,0,0,0);
        titleTextView.setText(getResources().getString(R.string.search));
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


}
