package com.pattaya.pattayacallcenter.guest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseDataObject;

public class PlaceActivity extends ActionBarActivity implements View.OnClickListener {

    EditText txtNnumber;
    EditText spSoi;

    EditText spStreet;
    EditText spMoo;
    EditText txtDetail;
    CaseDataObject dataPlace = new CaseDataObject();
    Button btnMap;
    // widget
    ImageButton btn;
    TextView titleTextView;
    int TAG_MAP = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        Intent intent = getIntent();
        CaseDataObject dataPlace = intent.getParcelableExtra("place");


        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_back, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        titleTextView.setText(getResources().getString(R.string.profile_place));
        titleTextView.setPadding(40, 0, 0, 0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        // dataPlace = new DataPlace();
        txtNnumber = (EditText) findViewById(R.id.number);
        spSoi = (EditText) findViewById(R.id.soi);
        spStreet = (EditText) findViewById(R.id.street);
        spMoo = (EditText) findViewById(R.id.moo);
        txtDetail = (EditText) findViewById(R.id.detail);
        btnMap = (Button) findViewById(R.id.btn_map);

        if (dataPlace != null) {
            this.dataPlace = dataPlace;
            txtNnumber.setText(dataPlace.getHouseNumber());
            spSoi.setText(dataPlace.getSoi());
            spStreet.setText(dataPlace.getRoad());
            spMoo.setText(dataPlace.getVillage());
            txtDetail.setText(dataPlace.getMoreInformation());

        }


        btnMap.setOnClickListener(this);
        btn.setOnClickListener(this);
    }

    void getData() {
        dataPlace.setHouseNumber(txtNnumber.getText().toString());
        dataPlace.setMoreInformation(txtDetail.getText().toString());
        dataPlace.setSoi((spSoi.getText().toString()));
        dataPlace.setVillage((spMoo.getText().toString()));
        dataPlace.setRoad((spStreet.getText().toString()));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            getData();
            Intent i = new Intent();
            i.putExtra("place", dataPlace);
            setResult(Activity.RESULT_OK, i);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == btnMap) {
            Intent intent = new Intent(getApplicationContext(), CaseMapActivity.class);
            startActivityForResult(intent, TAG_MAP);
        } else if (v == btn) {
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAG_MAP) {
            if (resultCode == Activity.RESULT_OK) {
                String location = data.getStringExtra("detail");
                if (!location.matches("")) {
                    txtDetail.setText(location);
                }

            }
        }
    }
}
