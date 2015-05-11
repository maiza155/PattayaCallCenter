package com.pattaya.pattayacallcenter.member;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.UpdateResult;
import com.pattaya.pattayacallcenter.webservice.object.UpdateTaskObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CaseWorkDateActivity extends ActionBarActivity implements View.OnClickListener {

    private EditText startDate;
    private EditText startTime;
    private EditText endDate;
    private EditText endTime;

    private DatePickerDialog fromDatePickerDialog, toDatePickerDialog;
    private TimePickerDialog mStartTimePicker, mEndTimePicker;

    // widget
    private ImageButton btn;
    private Button btnSend;
    private TextView titleTextView;
    private SimpleDateFormat dateFormatter;
    private int year, month, day, hour, minute;
    private Calendar calendar;


    private RestAdapter webserviceConnector = WebserviceConnector.getInstanceCase();
    private RestFulQueary adapterRest = null;


    private static SharedPreferences spConfig;
    private static SharedPreferences sp;
    private int userId;
    private String token;
    private String clientId;
    private int caseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_work_date);

        init();

        setActionBar();

        adapterRest = webserviceConnector.create(RestFulQueary.class);

        startDate = (EditText) findViewById(R.id.start_date);
        startTime = (EditText) findViewById(R.id.start_time);
        endDate = (EditText) findViewById(R.id.end_date);
        endTime = (EditText) findViewById(R.id.end_time);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);


        btnSend = (Button) findViewById(R.id.btn_send);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        startDate.setInputType(InputType.TYPE_NULL);
        startTime.setInputType(InputType.TYPE_NULL);
        endDate.setInputType(InputType.TYPE_NULL);
        endTime.setInputType(InputType.TYPE_NULL);


        btnSend.setOnClickListener(this);
        btn.setOnClickListener(this);
        setDate();
        setClickListener();
    }

    void setActionBar() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);

        titleTextView.setText(getResources().getString(R.string.menu_case_member_date));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    void init() {
        Intent intent = getIntent();
        caseId = intent.getIntExtra("id", 0);
        if (caseId > 0) {
            System.out.println(caseId);
        }


        sp = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        userId = sp.getInt(MasterData.SHARED_USER_USER_ID, -10);
        spConfig = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        token = spConfig.getString(MasterData.SHARED_CONFIG_TOKEN, null);
        clientId = spConfig.getString(MasterData.SHARED_CONFIG_CLIENT_ID, null);
    }

    private void setClickListener() {
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);

    }


    void setDate() {
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                startDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, year, month, day);


        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                endDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, year, month, day);

        mStartTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                startTime.setText("" + selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);

        mEndTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                endTime.setText("" + selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);


    }

    void saveTaskDate() {
        UpdateTaskObject updateTaskObject = new UpdateTaskObject();
        updateTaskObject.setCasesId(caseId);
        updateTaskObject.setUserId(userId);
        updateTaskObject.setAccessToken(token);
        updateTaskObject.setClientId(clientId);
        String start = startDate.getText().toString() + " " + startTime.getText().toString();
        String end = endDate.getText().toString() + " " + endTime.getText().toString();
        updateTaskObject.setStartDateString(start);
        updateTaskObject.setDuleDateString(end);


        adapterRest.updateTaskDate(updateTaskObject, new Callback<UpdateResult>() {
            @Override
            public void success(UpdateResult updateResult, Response response) {
                System.out.println("updateResult = [" + updateResult.getResult() + "], response = [" + response + "]");
                if (updateResult.getResult()) {
                    finish();
                } else {
                    Toast.makeText(getApplication(),
                            "Please try again.", Toast.LENGTH_SHORT)
                            .show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");
                Toast.makeText(getApplication(),
                        "Please check your internet connection.", Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_case_work_date, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Boolean checkDate() {

        if (!startDate.getText().toString().isEmpty()
                && !startTime.getText().toString().isEmpty()
                && !endDate.getText().toString().isEmpty()
                && !endTime.getText().toString().isEmpty()) {
            try {
                Date start = dateFormatter.parse(startDate.getText().toString());
                Date end = dateFormatter.parse(endDate.getText().toString());
                if (start.before(end)) {
                    return true;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        return false;
    }


    @Override
    public void onClick(View v) {
        if (v == btn) {
            finish();
        } else if (v == btnSend) {
            if (checkDate()) {
                saveTaskDate();
            } else {
                Toast.makeText(getApplication(),
                        "Time is not correct.", Toast.LENGTH_SHORT)
                        .show();
            }


        } else if (v == startDate) {
            fromDatePickerDialog.show();
        } else if (v == endDate) {
            toDatePickerDialog.show();
        } else if (v == startTime) {
            mStartTimePicker.show();
        } else if (v == endTime) {
            mEndTimePicker.show();
        }


    }
}
