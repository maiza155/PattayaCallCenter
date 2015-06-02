package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.CountingTypedFile;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.ProgressListener;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.chat.PubsubObject;
import com.pattaya.pattayacallcenter.chat.XMPPManage;
import com.pattaya.pattayacallcenter.customview.CameraMange;
import com.pattaya.pattayacallcenter.customview.CustomGalleryActivity;
import com.pattaya.pattayacallcenter.customview.ExpandableHeightGridView;
import com.pattaya.pattayacallcenter.guest.CaseDetail.ImageData;
import com.pattaya.pattayacallcenter.guest.CaseDetail.ImageUpdateGridAdapter;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.UpdateResult;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseListJidObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CloseCaseObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.JidData;
import com.pattaya.pattayacallcenter.webservice.object.casedata.TaskImageObject;
import com.pattaya.pattayacallcenter.webservice.object.upload.FileListObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CloseCaseActivity extends ActionBarActivity implements View.OnClickListener, View.OnCreateContextMenuListener {

    static SharedPreferences spConfig;
    static SharedPreferences sp;
    static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    List imageData;
    CameraMange cameraMange;
    ImageUpdateGridAdapter mGridAdapter;
    ExpandableHeightGridView gridView;
    View btnImage;
    CloseCaseObject data;
    Spinner spinner;
    EditText txtDetail;
    EditText time;
    // widget
    View btn;
    TextView titleTextView;


    TextView btnImg;
    int userId;
    String token;
    String clientId;
    DatePickerDialog fromDatePickerDialog;
    int caseId;
    int complainId;
    RestAdapter webserviceConnectorUpload = WebserviceConnector.getInstanceUpload();
    RestFulQueary adapterRestUpload = null;
    RestAdapter webserviceConnector = WebserviceConnector.getInstanceCase();
    RestFulQueary adapterRest = null;
    ProgressDialog ringProgressDialog;
    String jid;
    private int year, month, day, hour, minute;
    private Calendar calendar;
    private String displayName;
    private String displayImage;
    private String caseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_case);
        setActionBar();
        init();
        spinner = (Spinner) findViewById(R.id.sp);
        txtDetail = (EditText) findViewById(R.id.edit_textdetail);
        time = (EditText) findViewById(R.id.time);
        data = new CloseCaseObject();
        btnImg = (TextView) findViewById(R.id.btn_image);

        adapterRest = webserviceConnector.create(RestFulQueary.class);
        adapterRestUpload = webserviceConnectorUpload.create(RestFulQueary.class);


        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        time.setOnClickListener(this);
        initImageManage();

        time.setText(dateFormatter.format(calendar.getTime()));
        btnImg.setOnClickListener(this);
        btnImg.setOnCreateContextMenuListener(this);

    }

    void init() {
        Intent intent = getIntent();
        caseId = intent.getIntExtra("id", 0);
        complainId = intent.getIntExtra("complainid", 0);
        caseName = intent.getStringExtra("casename");
        sp = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        userId = sp.getInt(MasterData.SHARED_USER_USER_ID, -10);
        displayImage = sp.getString(MasterData.SHARED_USER_IMAGE, null);
        displayName = sp.getString(MasterData.SHARED_USER_DISPLAY_NAME, null);
        jid = sp.getString(MasterData.SHARED_USER_JID, null);
        spConfig = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        token = spConfig.getString(MasterData.SHARED_CONFIG_TOKEN, null);
        clientId = spConfig.getString(MasterData.SHARED_CONFIG_CLIENT_ID, null);
    }

    void setActionBar() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = v.findViewById(R.id.add_left_menu);
        titleTextView.setText(getResources().getString(R.string.menu_case_member_close));
        titleTextView.setPadding(40, 0, 0, 0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        btn.setOnClickListener(this);
    }

    void initImageManage() {
        btnImage = findViewById(R.id.button);
        btnImage.setOnClickListener(this);
        btnImage.setOnCreateContextMenuListener(this);

        imageData = new ArrayList();
        cameraMange = new CameraMange(this);
        gridView = (ExpandableHeightGridView) findViewById(R.id.grid_view);
        gridView.setExpanded(true);
        mGridAdapter = new ImageUpdateGridAdapter(this, R.layout.custom_gridview_image, imageData);
        gridView.setAdapter(mGridAdapter);


        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                time.setText(dateFormatter.format(newDate.getTime()));
            }

        }, year, month, day);
    }


    void upload() {
        new TaskResizeUpload().execute();
    }


    void saveData() {
        runOnUiThread(new Runnable() {
            public void run() {
                ringProgressDialog = ProgressDialog.show(CloseCaseActivity.this, getResources().getString(R.string.update_data), getResources().getString(R.string.please_wait), true);
            }
        });

        Gson gson = new Gson();
        String json = gson.toJson(data);
        System.out.println(json);
        adapterRest.updateTask(data, new Callback<UpdateResult>() {
            @Override
            public void success(UpdateResult updateResult, Response response) {
                System.out.println("updateResult = [" + updateResult.getResult() + "], response = [" + response + "]");

                if (updateResult.getResult()) {
                    //get JidList for notify and Recreate chat room
                    adapterRest.getUserListJid(complainId, new Callback<Response>() {
                        @Override
                        public void success(final Response result, Response response2) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    DatabaseChatHelper.init().clearCaseTable();
                                    BufferedReader reader;
                                    StringBuilder sb = new StringBuilder();
                                    try {
                                        reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                                        String line;

                                        try {
                                            while ((line = reader.readLine()) != null) {
                                                sb.append(line);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    String JsonConvertData = "{data:" + sb.toString() + "}";

                                    CaseListJidObject listObject = new Gson().fromJson(JsonConvertData, CaseListJidObject.class);

                                    Calendar c = Calendar.getInstance();
                                    for (JidData e : listObject.getData()) {
                                        PubsubObject pub = new PubsubObject();
                                        pub.setUsername(e.getJid().split("@")[0]);
                                        pub.setImage(displayImage);
                                        pub.setAction("ปิดเคส");
                                        pub.setDisplayData(c.getTime().toString());
                                        pub.setPrimarykey(complainId);
                                        pub.setName(displayName);
                                        pub.setTitle(caseName);
                                        if (!e.getJid().matches(jid)) {
                                            XMPPManage.getInstance().new TaskSendNotify(pub).execute();
                                        }


                                    }
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            ringProgressDialog.dismiss();
                                        }
                                    });


                                    BusProvider.getInstance().post("update_case_list");
                                    Intent i = new Intent();
                                    setResult(Activity.RESULT_OK, i);
                                    finish();
                                }
                            }).start();


                        }

                        @Override
                        public void failure(RetrofitError error) {
                            ringProgressDialog.dismiss();
                            Toast.makeText(getApplication(), "Unable connect server \n Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    ringProgressDialog.dismiss();
                    Toast.makeText(getApplication(), "Please try again", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");
                ringProgressDialog.dismiss();
                Toast.makeText(getApplication(), "Cannot upload file please try again", Toast.LENGTH_SHORT).show();
            }
        });

    }


    void setData() {
        data.setAccessToken(token);
        data.setUserId(userId);
        data.setUpdateBy(userId);
        data.setCreateBy(userId);
        data.setClientId(clientId);
        data.setCasesId(caseId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_case_add_and_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            String[] detail = txtDetail.getText().toString().split(" ");
            System.out.println("  length" + detail.length);
            if (txtDetail.getText().toString().isEmpty() || detail.length <= 0) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(txtDetail);
                Toast.makeText(getApplication(),
                        getResources().getString(R.string.please_enter_fill), Toast.LENGTH_SHORT)
                        .show();
            } else if (time.getText().toString().isEmpty()) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(time);
                Toast.makeText(getApplication(),
                        getResources().getString(R.string.please_enter_fill), Toast.LENGTH_SHORT)
                        .show();
            } else {
                data.setResultTypeString((spinner.getSelectedItemPosition() + 1));
                data.setCompletedDateString(time.getText().toString() + " 00:00");
                data.setResultsOperations(txtDetail.getText().toString());
                setData();
                upload();
            }


            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (v == btnImage) {
            v.showContextMenu();
        } else if (v == btn) {
            finish();
        } else if (v == time) {
            fromDatePickerDialog.show();
        } else if (v == btnImg) {
            v.showContextMenu();
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(getResources().getString(R.string.image_select));
        menu.add(0, v.getId(), 0, getResources().getString(R.string.image_select_from_gallery)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getApplication(), CustomGalleryActivity.class);
                startActivityForResult(intent, MasterData.PICK_IMAGE_MULTIPLE);
                return true;
            }
        });
        //groupId, itemId, order, title
        menu.add(0, v.getId(), 0, getResources().getString(R.string.image_capture)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                cameraMange.captureImage();
                return true;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == cameraMange.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    File imgFile = new File(cameraMange.fileUri.getPath());
                    if (imgFile.exists()) {
                        mGridAdapter.addItem(imgFile.getPath());

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        /** Check Image From Gallery*/
        if (requestCode == MasterData.PICK_IMAGE_MULTIPLE) {
            if (resultCode == Activity.RESULT_OK) {

                String[] imagesPath = data.getStringExtra("data").split("\\|");
                for (int i = 0; i < imagesPath.length; i++) {
                    mGridAdapter.addItem(imagesPath[i]);
                }

            }
        }


    }


    class TaskResizeUpload extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog ringProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ringProgressDialog = ProgressDialog.show(CloseCaseActivity.this, getResources().getString(R.string.uploading), getResources().getString(R.string.please_wait), true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final List<TaskImageObject> listImageURL = new ArrayList<>();
            if (mGridAdapter.getData().size() > 0) {
                int imageCount = 0;
                for (ImageData e : mGridAdapter.getData()) {
                    if (e.getTag() == 1) {
                        imageCount++;

                        int randomNum = 500 + (int) ((Math.random() * 1204006080) / Math.random());
                        File file = new File(getCacheDir(), "pattaya" + randomNum);
                        try {
                            file.createNewFile();
                        } catch (IOException error) {
                            error.printStackTrace();
                        }
                        Bitmap bitmap = null;
                        try {
                            // resize
                            bitmap = Glide.with(Application.getContext())
                                    .load(e.getPath())
                                    .asBitmap()
                                    .fitCenter()
                                    .into(500, 500) // Width and height
                                    .get();
                        } catch (InterruptedException error) {
                            error.printStackTrace();
                        } catch (ExecutionException error) {
                            error.printStackTrace();
                        }

                        System.out.println(bitmap);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, MasterData.PERCEN_OF_IMAGE_FILE, bos);
                        byte[] bitmapdata = bos.toByteArray();
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(file);
                        } catch (FileNotFoundException error) {
                            error.printStackTrace();
                        }
                        try {
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                        } catch (IOException error) {
                            error.printStackTrace();
                        }

                        final long totalSize = file.length();
                        final int finalImageCount = imageCount;
                        ProgressListener listener = new ProgressListener() {
                            String fileID = "Please wait... \nimage" + finalImageCount + " upload complete";

                            @Override
                            public void transferred(long num) {
                                //publishProgress((int) ((num / (float) totalSize) * 100));
                                final int[] count = {Math.round(((num / (float) totalSize) * 100))};
                                runOnUiThread(new Thread(new Runnable() {
                                    public void run() {

                                        if (count[0] == 100) {
                                            ringProgressDialog.setMessage(fileID);

                                        }
                                    }
                                }));


                            }
                        };

                        adapterRestUpload.uploadImageCase(new CountingTypedFile("image/jpeg", file, listener), new Callback<Response>() {
                            @Override
                            public void success(Response result, Response response) {

                                BufferedReader reader = null;
                                StringBuilder sb = new StringBuilder();
                                try {

                                    reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                                    String line;

                                    try {
                                        while ((line = reader.readLine()) != null) {
                                            sb.append(line);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String JsonConvertData = "{data:" + sb.toString() + "}";

                                FileListObject fileListObject = new Gson().fromJson(JsonConvertData, FileListObject.class);
                                System.out.println(fileListObject.getData().get(0).getUrl());
                                TaskImageObject imageObject = new TaskImageObject();
                                imageObject.setTaskImage(fileListObject.getData().get(0).getUrl());
                                listImageURL.add(imageObject);
                                if (listImageURL.size() == mGridAdapter.getData().size()) {
                                    data.setTaskImageList(listImageURL);
                                    saveData();
                                    ringProgressDialog.dismiss();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                System.out.println("error = [" + error + "]");
                                ringProgressDialog.dismiss();
                                Toast.makeText(getApplication(), "Cannot upload file please try again", Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else if (e.getTag() == 0) {
                        TaskImageObject imageObject = new TaskImageObject();
                        imageObject.setTaskImage(e.getPath());
                        listImageURL.add(imageObject);
                        if (listImageURL.size() == mGridAdapter.getData().size()) {
                            ringProgressDialog.dismiss();
                            data.setTaskImageList(listImageURL);
                            saveData();
                        }
                    }
                }

            } else {
                ringProgressDialog.dismiss();
                data.setTaskImageList(listImageURL);
                saveData();

            }

            return null;
        }
    }
}
