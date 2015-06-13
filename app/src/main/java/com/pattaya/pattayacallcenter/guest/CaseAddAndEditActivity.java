package com.pattaya.pattayacallcenter.guest;


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
import android.widget.CheckBox;
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
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.AccessUserObject;
import com.pattaya.pattayacallcenter.webservice.object.GetUserObject;
import com.pattaya.pattayacallcenter.webservice.object.UpdateResult;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseDataObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseListJidObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseMainObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.GetComplainObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.ImageObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.JidData;
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
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class CaseAddAndEditActivity extends ActionBarActivity implements View.OnClickListener, View.OnCreateContextMenuListener {

    TextView txtPlace;
    CaseDataObject dataCaseDetail;
    List<String> imageData;

    List<ImageObject> listImageURL;
    CameraMange cameraMange;
    ImageUpdateGridAdapter mGridAdapter;
    ExpandableHeightGridView gridView;
    CaseMainObject caseMainObject;
    Button btnPlace;
    Button btnImage;


    List<ImageObject> listOldImageUrl;
    EditText txtName, txtUserName, txtPhone;
    CheckBox checkBox;

    // widget
    ImageButton btn;
    TextView titleTextView;
    int complainId = 0;
    int caseId = 0;
    RestAdapter webserviceConnector = WebserviceConnector.getInstanceCase();
    RestFulQueary adapterRest = null;
    RestAdapter webserviceConnectorUpload = WebserviceConnector.getInstanceUpload();
    RestFulQueary adapterRestUpload = null;
    RestAdapter webserviceConnectorUser = WebserviceConnector.getInstance();
    SharedPreferences sp, spConfig;
    String token, clientId;
    int userId;
    String userName;
    int contactInfoId;
    String jid;

    ProgressListener listener;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    ProgressDialog ringProgressDialog;
    private String displayName;
    private String displayImage;
    private RestFulQueary adapterRestUser = null;
    private RestAdapter openfireConnectorJson = RestAdapterOpenFire.getInstanceJson();
    private OpenfireQueary openfireQuearyJson = openfireConnectorJson.create(OpenfireQueary.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_add_and_edit);
        adapterRestUpload = webserviceConnectorUpload.create(RestFulQueary.class);
        adapterRest = webserviceConnector.create(RestFulQueary.class);
        adapterRestUser = webserviceConnectorUser.create(RestFulQueary.class);

        txtName = (EditText) findViewById(R.id.edit_textdetail);
        txtUserName = (EditText) findViewById(R.id.edit_text_name);
        txtPhone = (EditText) findViewById(R.id.edit_text_phone);
        checkBox = (CheckBox) findViewById(R.id.chk_select);

        setActionBar();

        init();

        caseMainObject = new CaseMainObject();
        dataCaseDetail = new CaseDataObject();

        listImageURL = new ArrayList<>();

        txtPlace = (TextView) findViewById(R.id.txt_place);
        btnImage = (Button) findViewById(R.id.btn_image);
        btnPlace = (Button) findViewById(R.id.btn_place);
        btnImage.setOnClickListener(this);
        btnPlace.setOnClickListener(this);
        btnImage.setOnCreateContextMenuListener(this);
        btn.setOnClickListener(this);

        listOldImageUrl = new ArrayList<>();
        imageData = new ArrayList();
        cameraMange = new CameraMange(this);
        gridView = (ExpandableHeightGridView) findViewById(R.id.grid_view);
        gridView.setExpanded(true);
        mGridAdapter = new ImageUpdateGridAdapter(this, R.layout.custom_gridview_image, imageData);
        gridView.setAdapter(mGridAdapter);

        if (displayName == null) {
            txtUserName.setText(userName);
        } else {
            txtUserName.setText(displayName);
        }


    }


    void setActionBar() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        titleTextView.setText(getResources().getString(R.string.case_complain));
        titleTextView.setPadding(40, 0, 0, 0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    void init() {
        Intent intent = getIntent();
        int data = intent.getIntExtra("data", 0);
        if (data > 0) {
            complainId = data;
            getData();
            System.out.println("Edit Data");
        }
        sp = getSharedPreferences("PREF_USER", Context.MODE_PRIVATE);
        displayImage = sp.getString(MasterData.SHARED_USER_IMAGE, null);
        displayName = sp.getString(MasterData.SHARED_USER_DISPLAY_NAME, null);
        userName = sp.getString(MasterData.SHARED_USER_USERNAME, null);
        spConfig = getSharedPreferences("APP_CONFIG", Context.MODE_PRIVATE);
        userId = sp.getInt("USER_ID", -10);
        token = spConfig.getString("TOKEN", null);
        clientId = spConfig.getString("CLIENT_ID", null);

        jid = sp.getString(MasterData.SHARED_USER_JID, null);
    }

    void setData() {
        Calendar c = Calendar.getInstance();

        caseMainObject.setAccessToken(token);
        caseMainObject.setClientId(clientId);
        caseMainObject.setComplaintName(txtName.getText().toString());
        caseMainObject.setCreatedBy(userId);
        if (complainId == 0) {
            caseMainObject.setComplaintDateString(sdf.format(c.getTime()));
        } else {
            caseMainObject.setContactInfoId(contactInfoId);
            caseMainObject.setComplaintId(complainId);
        }

        dataCaseDetail.setIsAnonymousString(checkBox.isChecked());
        dataCaseDetail.setNameContact(txtUserName.getText().toString());
        dataCaseDetail.setTelephone(txtPhone.getText().toString());
        caseMainObject.setContactInfo(dataCaseDetail);
    }

    void uploadImage() {
        new TaskResizeUpload().execute();
    }

    void saveData() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ringProgressDialog = ProgressDialog.show(CaseAddAndEditActivity.this, null, getResources().getString(R.string.please_wait), true);
                        ringProgressDialog.setCancelable(true);
                    }
                });

            }

            @Override
            protected Boolean doInBackground(Void... params) {
                setData();
                Gson gson = new Gson();
                String json = gson.toJson(caseMainObject);
                System.out.println(json);

                GetUserObject getUserObject = new GetUserObject(userId, token, clientId);
                adapterRestUser.getUser(getUserObject, new Callback<AccessUserObject>() {
                    @Override
                    public void success(AccessUserObject accessUserObject, Response response) {
                        CaseDataObject caseDataObject = caseMainObject.getContactInfo();
                        caseDataObject.setAddress(accessUserObject.getAddress());
                        caseDataObject.setDistrict(accessUserObject.getDistrict());
                        caseDataObject.setAmphur(accessUserObject.getAmphur());
                        caseDataObject.setProvince(accessUserObject.getProvince());
                        caseDataObject.setPostCode(accessUserObject.getPostCode());
                        caseDataObject.setEmail(accessUserObject.getEmail());
                        if (caseDataObject.getTelephone() == null || caseDataObject.getTelephone().isEmpty()) {
                            caseDataObject.setTelephone(accessUserObject.getMobile());
                        }
                        if (caseDataObject.getNameContact() == null || caseDataObject.getNameContact().isEmpty()) {
                            caseDataObject.setNameContact(accessUserObject.getDisplayName());
                        }

                        caseMainObject.setContactInfo(caseDataObject);
                        adapterRest.saveCase(caseMainObject, new Callback<UpdateResult>() {
                                    @Override
                                    public void success(final UpdateResult updateResult, Response response) {
                                        System.out.println("updateResult = [" + updateResult.getResult() + "], response = [" + response + "]");
                                        Log.e("TAG", "Save Data Success");
                                        if (updateResult.getResult()) {
                                            String roomName = "complaint-" + updateResult.getPrimaryKeyId();
                                            final ChatRoomObject chatRoomObject = new ChatRoomObject();
                                            chatRoomObject.setRoomName(roomName);
                                            chatRoomObject.setNaturalName(roomName);
                                            chatRoomObject.setDescription(roomName);

                                            /////get JidList for notify and Recreate chat room
                                            adapterRest.getUserListJid(updateResult.getPrimaryKeyId(), new Callback<Response>() {
                                                @Override
                                                public void success(Response result, Response response2) {
                                                    Log.e("TAG", "getList Success");
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
                                                    List<JidData> data = listObject.getData();
                                                    data.add(new JidData(jid));

                                                    for (JidData e : data) {
                                                        if (!e.getJid().isEmpty() && e.getJid() != null) {
                                                            listJid.add(e.getJid());
                                                            PubsubObject pub = new PubsubObject();
                                                            pub.setUsername(e.getJid().split("@")[0]);
                                                            pub.setImage(displayImage);
                                                            Log.e("TAG", "getList Success  JIID " + e.getJid() + "  " + jid);
                                                            pub.setDisplayDate(c.getTime().toString());
                                                            // pub.setPrimarykey(complainId);
                                                            if (complainId == 0) {
                                                                pub.setAction("ส่งเรื่องร้องเรียน");
                                                            } else {
                                                                pub.setAction("อัพเดทเคส");
                                                            }
                                                            pub.setComplainId(updateResult.getPrimaryKeyId());
                                                            pub.setCaseId(caseId);
                                                            pub.setName(displayName);
                                                            pub.setTitle(caseMainObject.getComplaintName());
                                                            if (!e.getJid().matches(jid)) {
                                                                listNotify.add(pub);
                                                            }

                                                        }

                                                    }
                                                    members.setMember(listJid);
                                                    chatRoomObject.setMembers(members);

                                                    //Recreate chat room`
                                                    openfireQuearyJson.createChatRoom(chatRoomObject, new Callback<Response>() {
                                                        @Override
                                                        public void success(Response response, Response response2) {
                                                            Log.e("TAG", "Create Room Success  ");
                                                            //System.out.println("response = [" + response + "], response2 = [" + response2 + "]");
                                                            XMPPManage.getInstance().setJoinRoom(chatRoomObject.getRoomName() + "@conference.pattaya-data");
                                                            for (PubsubObject e : listNotify) {
                                                                XMPPManage.getInstance().new TaskSendNotify(e).execute();
                                                            }
                                                            DatabaseChatHelper.init().clearCaseTable();

                                                            BusProvider.getInstance().post("update_case_list");
                                                            Toast.makeText(getApplication(), "success", Toast.LENGTH_SHORT).show();
                                                            Intent i = new Intent();
                                                            setResult(Activity.RESULT_OK, i);

                                                            ringProgressDialog.dismiss();
                                                            finish();


                                                        }

                                                        @Override
                                                        public void failure(RetrofitError error) {
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                    ringProgressDialog.dismiss();
                                                                }
                                                            });
                                                            Toast.makeText(getApplication(), "Unable connect server. Please try again", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });


                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            ringProgressDialog.dismiss();
                                                        }
                                                    });
                                                    Toast.makeText(getApplication(), "Unable connect server. Please try again", Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                        } else {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    ringProgressDialog.dismiss();
                                                }
                                            });
                                            Toast.makeText(getApplication(), "Cannot save this case please try again", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        System.out.println("error = [" + error + "]");
                                        Toast.makeText(getApplication(), "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                ringProgressDialog.dismiss();
                                            }
                                        });
                                    }
                                }

                        );


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println("error get user data = [" + error + "]");
                        ringProgressDialog.dismiss();
                        Toast.makeText(getApplication(), "Unable connect server. Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }
        }.execute();


    }

    void getData() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, null, getResources().getString(R.string.please_wait), true);
        ringProgressDialog.setCancelable(true);
        GetComplainObject getComplainObject = new GetComplainObject();
        getComplainObject.setAccessToken(token);
        getComplainObject.setClientId(clientId);
        getComplainObject.setPrimaryKeyId(complainId);
        adapterRest.getComplaintData(getComplainObject, new Callback<CaseMainObject>() {
            @Override
            public void success(CaseMainObject caseMainObject, Response response) {
                dataCaseDetail = caseMainObject.getContactInfo();

                System.out.println(caseMainObject.getComplaintName());
                contactInfoId = caseMainObject.getContactInfoId();
                txtName.setText(caseMainObject.getComplaintName());
                txtUserName.setText(caseMainObject.getContactInfo().getNameContact());
                txtPhone.setText(caseMainObject.getContactInfo().getTelephone());
                System.out.println(caseMainObject.getContactInfo().getIsAnonymousString());
                checkBox.setChecked(caseMainObject.getContactInfo().getIsAnonymousString());
                //////////// Set Address /////////////
                String houseNumber = caseMainObject.getContactInfo().getHouseNumber();
                houseNumber = (houseNumber == null || houseNumber.isEmpty()) ? "" : houseNumber + " ";

                String village = caseMainObject.getContactInfo().getVillage();
                village = (village == null || village.isEmpty()) ? "" : village + " ";

                String soi = caseMainObject.getContactInfo().getSoi();
                soi = (soi == null || soi.isEmpty()) ? "" : soi + " ";

                String road = caseMainObject.getContactInfo().getRoad();
                road = (road == null || road.isEmpty()) ? "" : road + "\n";

                String more = caseMainObject.getContactInfo().getMoreInformation();
                more = (more == null || more.matches("")) ? "" : "(" + more + ")";


                String address = houseNumber + village + soi + road + more;
                if (address.matches("")) {
                    address = "ไม่มีข้อมูล";
                }
                txtPlace.setText(address);
                ////////////////////////Image View///////////////////////////
                if (caseMainObject.getContactInfo().getInfoImageList().size() > 0) {

                    //mGridAdapter.resetAdapter(caseMainObject.getContactInfo().getInfoImageList());
                    listOldImageUrl = caseMainObject.getContactInfo().getInfoImageList();
                    for (ImageObject e : listOldImageUrl) {

                        mGridAdapter.addItemUpdate(e.getInfoImage());
                    }
                } else {

                }
                ringProgressDialog.dismiss();


            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");
                ringProgressDialog.dismiss();
                Toast.makeText(getApplication(),
                        "Please check your internet connection.", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        });
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
            if (txtName.getText().toString().isEmpty()) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(txtName);
                Toast.makeText(getApplication(),
                        getResources().getString(R.string.please_enter_fill), Toast.LENGTH_SHORT)
                        .show();

            } else {
                uploadImage();
            }


            // finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == btnImage) {
            v.showContextMenu();
        } else if (v == btnPlace) {
            Intent intent = new Intent(getApplication(), PlaceActivity.class);
            intent.putExtra("place", dataCaseDetail);
            startActivityForResult(intent, MasterData.PLACE_DATA);
        } else if (v == btn) {
            finish();
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(getResources().getString(R.string.image_select));
        menu.add(0, v.getId(), 0, getResources().getString(R.string.image_select_from_gallery)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mGridAdapter.getCount() < 6) {
                    Intent intent = new Intent(getApplication(), CustomGalleryActivity.class);
                    startActivityForResult(intent, MasterData.PICK_IMAGE_MULTIPLE);
                } else {
                    Toast.makeText(getApplication(), "You can upload about 6 photos per case.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        //groupId, itemId, order, title
        menu.add(0, v.getId(), 0, getResources().getString(R.string.image_capture)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mGridAdapter.getCount() < 6) {
                    cameraMange.captureImage();
                } else {
                    Toast.makeText(getApplication(), "You can upload about 6 photos per case.", Toast.LENGTH_SHORT).show();
                }

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

                        Log.d("TAG", "CAmera Capture path2" + imgFile.getPath());
                        if (mGridAdapter.getCount() < 6) {
                            mGridAdapter.addItem(imgFile.getPath());
                        } else {
                            Toast.makeText(getApplication(), "You can upload about 6 photos per case.", Toast.LENGTH_SHORT).show();

                        }


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
                    if (mGridAdapter.getCount() < 6) {
                        mGridAdapter.addItem(imagesPath[i]);
                    } else {
                        Toast.makeText(getApplication(), "You can upload about 6 photos per case.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                }

            }
        }

        if (requestCode == MasterData.PLACE_DATA) {
            if (resultCode == Activity.RESULT_OK) {
                //dataCaseDetail.setDataPlace((DataPlace) data.getSerializableExtra("data"));

                dataCaseDetail = data.getParcelableExtra("place");
                txtPlace.setText(dataCaseDetail.toString());
            }
        }


    }

    class TaskResizeUpload extends AsyncTask<Void, Void, Boolean> {

        ProgressDialog ringProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ringProgressDialog = ProgressDialog.show(CaseAddAndEditActivity.this, null, getResources().getString(R.string.please_wait), true);
            ringProgressDialog.setCancelable(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (mGridAdapter.getData().size() > 0) {

                listImageURL = new ArrayList<>();
                int imageCount = 0;
                for (ImageData e : mGridAdapter.getData()) {
                    if (e.getTag() == 1) {
                        imageCount++;
                        String uuid = UUID.randomUUID().toString();
                        int randomNum = 500 + (int) ((Math.random() * 1204006080) / Math.random());
                        File file = new File(getCacheDir(), "pattaya-complain" + randomNum + uuid + ".jpg");
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
                        listener = new ProgressListener() {
                            String fileID = "Please wait... \nimage" + finalImageCount + " upload complete";

                            @Override
                            public void transferred(long num) {
                                //publishProgress((int) ((num / (float) totalSize) * 100));
                                final int[] count = {Math.round(((num / (float) totalSize) * 100))};
                                runOnUiThread(new Thread(new Runnable() {
                                    public void run() {
                                        //btn.setText("#"+i);
                                        //System.out.println(fileID +"  FILE LENGTH" + ">>>>>>>>" + count[0]);
                                        if (count[0] == 100) {
                                            ringProgressDialog.setMessage(fileID);
                                            System.out.println(fileID + "       Complete !!!");
                                        }
                                    }
                                }));
                                //ringProgressDialog.setMessage("Uploading image"+ finalImageCount +" ("+count+")");

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
                                ImageObject imageObject = new ImageObject();
                                imageObject.setInfoImage(fileListObject.getData().get(0).getUrl());
                                listImageURL.add(imageObject);
                                if (listImageURL.size() == mGridAdapter.getData().size()) {
                                    Activity activity = CaseAddAndEditActivity.this;
                                    if (activity != null) {
                                        dataCaseDetail.setInfoImageList(listImageURL);
                                        saveData();
                                        ringProgressDialog.dismiss();
                                    }

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
                        ImageObject imageObject = new ImageObject();
                        imageObject.setInfoImage(e.getPath());
                        listImageURL.add(imageObject);
                        if (listImageURL.size() == mGridAdapter.getData().size()) {
                            Activity activity = CaseAddAndEditActivity.this;
                            if (activity != null) {
                                ringProgressDialog.dismiss();
                                dataCaseDetail.setInfoImageList(listImageURL);
                                saveData();
                            }


                        }
                    }
                }

            } else {
                Activity activity = CaseAddAndEditActivity.this;
                if (activity != null) {
                    dataCaseDetail.setInfoImageList(listImageURL);
                    ringProgressDialog.dismiss();
                    saveData();
                }


            }

            return null;
        }
    }

}
