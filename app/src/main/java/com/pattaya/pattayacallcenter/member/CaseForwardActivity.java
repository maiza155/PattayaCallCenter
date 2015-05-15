package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.pattaya.pattayacallcenter.chat.jsonobject.ChatRoomObject;
import com.pattaya.pattayacallcenter.chat.jsonobject.Members;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.customview.CameraMange;
import com.pattaya.pattayacallcenter.customview.CustomGalleryActivity;
import com.pattaya.pattayacallcenter.customview.ExpandableHeightGridView;
import com.pattaya.pattayacallcenter.guest.CaseDetail.ImageData;
import com.pattaya.pattayacallcenter.guest.CaseDetail.ImageUpdateGridAdapter;
import com.pattaya.pattayacallcenter.member.data.ForwardObject;
import com.pattaya.pattayacallcenter.member.dummy.DataShowAddGroup;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.UpdateResult;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseListJidObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.JidData;
import com.pattaya.pattayacallcenter.webservice.object.casedata.OrgIdObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.SendReassignTaskObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.TaskImageObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.UserIdObject;
import com.pattaya.pattayacallcenter.webservice.object.upload.FileListObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CaseForwardActivity extends ActionBarActivity implements View.OnClickListener, View.OnCreateContextMenuListener {

    private static SharedPreferences spConfig;
    private static SharedPreferences sp;
    ProgressDialog ringProgressDialog;
    private ArrayList<ForwardObject> arrayList;
    private TextView txtTo;
    private List imageData;
    private CameraMange cameraMange;
    private ImageUpdateGridAdapter mGridAdapter;
    private ExpandableHeightGridView gridView;
    private ArrayList<DataShowAddGroup> listData;
    private EditText txtDetail;
    private Button btnImage;
    private Button btnTo;
    private String text = "";
    // widget
    private ImageButton btn;
    private TextView titleTextView;
    private int userId;
    private String token;
    private String clientId;
    private int caseId;
    private int complainId;
    private String displayName;
    private String displayImage;
    private String caseName;
    private SendReassignTaskObject data;
    private RestAdapter webserviceConnectorUpload = WebserviceConnector.getInstanceUpload();
    private RestFulQueary adapterRestUpload = null;
    private RestAdapter webserviceConnector = WebserviceConnector.getInstanceCase();
    private RestFulQueary adapterRest = null;
    private RestAdapter openfireConnectorJson = RestAdapterOpenFire.getInstanceJson();
    private OpenfireQueary openfireQuearyJson = openfireConnectorJson.create(OpenfireQueary.class);
    private int STATE_FORWARD = 0;
    private String jid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_forward_to);

        init();
        setActionBar();

        adapterRestUpload = webserviceConnectorUpload.create(RestFulQueary.class);
        adapterRest = webserviceConnector.create(RestFulQueary.class);
        arrayList = new ArrayList<>();

        txtDetail = (EditText) findViewById(R.id.edit_textdetail);
        txtTo = (TextView) findViewById(R.id.txt_to);
        btnImage = (Button) findViewById(R.id.btn_image);
        btnTo = (Button) findViewById(R.id.btn_to);
        btnImage.setOnClickListener(this);
        btnImage.setOnCreateContextMenuListener(this);
        btn.setOnClickListener(this);
        btnTo.setOnClickListener(this);


        listData = new ArrayList<>();
        imageData = new ArrayList();
        cameraMange = new CameraMange(this);
        gridView = (ExpandableHeightGridView) findViewById(R.id.grid_view);
        gridView.setExpanded(true);
        mGridAdapter = new ImageUpdateGridAdapter(this, R.layout.custom_gridview_image, imageData);
        gridView.setAdapter(mGridAdapter);
    }


    void init() {
        Intent intent = getIntent();
        caseId = intent.getIntExtra("id", 0);
        complainId = intent.getIntExtra("complainid", 0);
        caseName = intent.getStringExtra("casename");
        System.out.println("caseId " + caseId);

        data = new SendReassignTaskObject();

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
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        titleTextView.setText(getResources().getString(R.string.menu_case_member_to));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }


    void setTextTO() {
        if (arrayList.size() > 0) {
            if (STATE_FORWARD == 1) {
                if (arrayList.size() == 1) {
                    txtTo.setText(arrayList.get(0).getName());
                } else if (arrayList.size() == 2) {
                    txtTo.setText(arrayList.get(0).getName() + " เเละ " + arrayList.get(1).getName());
                } else {
                    txtTo.setText(arrayList.get(0).getName() + " เเละอีก 2 หน่วยงาน");
                }

            } else if (STATE_FORWARD == 2) {
                if (arrayList.size() == 1) {
                    txtTo.setText(arrayList.get(0).getName());
                } else if (arrayList.size() == 2) {
                    txtTo.setText(arrayList.get(0).getName() + " เเละ " + arrayList.get(1).getName());
                } else {
                    txtTo.setText(arrayList.get(0).getName() + " เเละอีก 2 คน");
                }

            }
        } else {
            txtTo.setText("ไม่มีข้อมูล");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_case_add_and_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            //finish();
            String[] detail = txtDetail.getText().toString().split(" ");
            System.out.println("  length" + detail.length);
            if (txtDetail.getText().toString().isEmpty() || detail.length <= 0) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(txtDetail);
                Toast.makeText(getApplication(),
                        getResources().getString(R.string.please_enter_fill), Toast.LENGTH_SHORT)
                        .show();

            } else if (arrayList.size() == 0) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(txtTo);
                Toast.makeText(getApplication(),
                        "โปรดระบุผู้รับผิดชอบ", Toast.LENGTH_SHORT)
                        .show();
            } else {
                data.setResultsOperations(txtDetail.getText().toString());
                data.setClientId(clientId);
                data.setCasesId(caseId);
                data.setTypeCaseAssign(STATE_FORWARD);
                data.setAccessToken(token);
                data.setCreateBy(userId);
                data.setUpdateBy(userId);
                data.setUserId(userId);
                if (STATE_FORWARD == 1) {
                    List<OrgIdObject> list = new ArrayList<>();
                    for (ForwardObject e : arrayList) {
                        list.add(new OrgIdObject(e.getId()));
                    }
                    data.setCaseAssignList(list);
                } else if (STATE_FORWARD == 2) {
                    List<UserIdObject> list = new ArrayList<>();
                    for (ForwardObject e : arrayList) {
                        list.add(new UserIdObject(e.getId()));
                    }
                    data.setCaseAssignList(list);

                }
                uploadImage();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void uploadImage() {
        new TaskResizeUpload().execute();

    }

    void saveData() {
        runOnUiThread(new Runnable() {
            public void run() {
                ringProgressDialog = ProgressDialog.show(CaseForwardActivity.this, null, getResources().getString(R.string.please_wait), true);
                ringProgressDialog.setCancelable(true);
            }
        });
        if (data != null) {
            Gson gson = new Gson();
            String json = gson.toJson(data);
            System.out.println(json);
            adapterRest.updateReassignTask(data, new Callback<UpdateResult>() {
                @Override
                public void success(UpdateResult updateResult, Response response) {
                    System.out.println("updateResult = [" + updateResult.getResult() + "], response = [" + response + "]");

                    if (updateResult.getResult()) {
                        String roomName = "case-" + complainId;
                        final ChatRoomObject chatRoomObject = new ChatRoomObject();
                        chatRoomObject.setRoomName(roomName);
                        chatRoomObject.setNaturalName(roomName);
                        chatRoomObject.setDescription(roomName);
                        //get JidList for notify and Recreate chat room
                        adapterRest.getUserListJid(complainId, new Callback<Response>() {
                            @Override
                            public void success(Response result, Response response2) {
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
                                System.out.println(JsonConvertData);
                                CaseListJidObject listObject = new Gson().fromJson(JsonConvertData, CaseListJidObject.class);
                                Members members = new Members();
                                List<String> listJid = new ArrayList();
                                final List<PubsubObject> listNotify = new ArrayList<PubsubObject>();
                                Calendar c = Calendar.getInstance();
                                for (JidData e : listObject.getData()) {
                                    listJid.add(e.getJid());
                                    PubsubObject pub = new PubsubObject();
                                    pub.setUsername(e.getJid().split("@")[0]);
                                    pub.setImage(displayImage);
                                    pub.setAction("อัพเดทเคส");
                                    pub.setDisplayData(c.getTime().toString());
                                    // pub.setPrimarykey(complainId);
                                    pub.setComplainId(complainId);
                                    pub.setCaseId(caseId);
                                    pub.setName(displayName);
                                    pub.setTitle(caseName);
                                    if (!e.getJid().matches(jid)) {
                                        listNotify.add(pub);
                                    }
                                }
                                members.setMember(listJid);
                                chatRoomObject.setMembers(members);

                                //Recreate chat room
                                openfireQuearyJson.createChatRoom(chatRoomObject, new Callback<Response>() {
                                    @Override
                                    public void success(Response response, Response response2) {
                                        //System.out.println("response = [" + response + "], response2 = [" + response2 + "]");
                                        for (PubsubObject e : listNotify) {
                                            XMPPManage.getInstance().new TaskSendNotify(e).execute();
                                        }
                                        ringProgressDialog.dismiss();
                                        BusProvider.getInstance().post("update_case_list");
                                        Intent i = new Intent();
                                        setResult(Activity.RESULT_OK, i);
                                        finish();

                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        ringProgressDialog.dismiss();
                                        Toast.makeText(getApplication(), "Unable connect server. Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                ringProgressDialog.dismiss();
                                Toast.makeText(getApplication(), "Unable connect server \n Please try again", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        ringProgressDialog.dismiss();
                        Toast.makeText(getApplication(), "Please try again.", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println("error = [" + error + "]");
                    ringProgressDialog.dismiss();
                    Toast.makeText(getApplication(), "Cannot create this case please try again", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }
    //

    @Override
    public void onClick(View v) {

        if (v == btnImage) {
            v.showContextMenu();
        } else if (v == btn) {
            finish();
        } else if (v == btnTo) {
            Intent intent = new Intent(getApplication(), CaseForwardToActivity.class);
            intent.putParcelableArrayListExtra("data", arrayList);
            intent.putExtra("state", STATE_FORWARD);
            startActivityForResult(intent, MasterData.FORWARD_TO_DATA);
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
                // successfully captured the image
                // display it in image view
                //previewCapturedImage();
                try {
                    File imgFile = new File(cameraMange.fileUri.getPath());
                    //String mpath = "file:///mnt/sdcard/"+imgFile.getName();
                    //Log.d("TAG", "COUNT DATA >>" + mGridAdapter.getItemCount() + "GET PATH OF CAPTURE IMAGE>>" +mpath);
                    Log.d("TAG", "CAmera Capture path1" + imgFile.getPath());
                    if (imgFile.exists()) {
                        Log.d("TAG", "CAmera Capture path2" + imgFile.getPath());
                        mGridAdapter.addItem(imgFile.getPath());

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //mGridAdapter.addItem();

                //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), imgs.getResourceId(i, -1));
                //imageItems.add(new ImageItem(bitmap, "Image#" + i));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled Image capture
                System.out.println("User cancelled image capture");

            } else {
                // failed to capture image
                System.out.println("Sorry! Failed to capture image");

            }
        }


        /** Check Image From Gallery*/
        if (requestCode == MasterData.PICK_IMAGE_MULTIPLE) {
            if (resultCode == Activity.RESULT_OK) {

                String[] imagesPath = data.getStringExtra("data").split("\\|");
                for (int i = 0; i < imagesPath.length; i++) {
                    mGridAdapter.addItem(imagesPath[i]);
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled Image capture
                System.out.println("User cancelled select image ");

            } else {
                // failed to capture image
                System.out.println("Sorry! Failed to select image");

            }
        }


        if (requestCode == MasterData.FORWARD_TO_DATA) {
            if (resultCode == Activity.RESULT_OK) {
                STATE_FORWARD = data.getIntExtra("state", 0);
                arrayList = (ArrayList<ForwardObject>) data.getSerializableExtra("data");
                setTextTO();

            }
        }


    }

    class TaskResizeUpload extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog ringProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ringProgressDialog = ProgressDialog.show(CaseForwardActivity.this, null, getResources().getString(R.string.please_wait), true);
            ringProgressDialog.setCancelable(true);
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
                        File file = new File(getCacheDir(), "pattaya-to" + randomNum);
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

                                    runOnUiThread(new Thread(new Runnable() {
                                        public void run() {
                                            ringProgressDialog.dismiss();
                                        }
                                    }));

                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                System.out.println("error = [" + error + "]");
                                runOnUiThread(new Thread(new Runnable() {
                                    public void run() {
                                        ringProgressDialog.dismiss();
                                    }
                                }));
                                Toast.makeText(getApplication(), "Cannot upload file please try again", Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else if (e.getTag() == 0) {
                        TaskImageObject imageObject = new TaskImageObject();
                        imageObject.setTaskImage(e.getPath());
                        listImageURL.add(imageObject);
                        if (listImageURL.size() == mGridAdapter.getData().size()) {
                            runOnUiThread(new Thread(new Runnable() {
                                public void run() {
                                    ringProgressDialog.dismiss();
                                }
                            }));
                            data.setTaskImageList(listImageURL);
                            saveData();

                        }
                    }
                }

            } else {
                runOnUiThread(new Thread(new Runnable() {
                    public void run() {
                        ringProgressDialog.dismiss();
                    }
                }));
                data.setTaskImageList(listImageURL);
                saveData();

            }
            return null;
        }
    }
}
