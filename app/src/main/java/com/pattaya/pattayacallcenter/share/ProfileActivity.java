package com.pattaya.pattayacallcenter.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.jsonobject.MapProperty;
import com.pattaya.pattayacallcenter.chat.jsonobject.Property;
import com.pattaya.pattayacallcenter.chat.jsonobject.UserProperty;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.customview.CameraMange;
import com.pattaya.pattayacallcenter.customview.CustomGalleryActivity;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.AccessUserObject;
import com.pattaya.pattayacallcenter.webservice.object.GetUserObject;
import com.pattaya.pattayacallcenter.webservice.object.UpdateResult;
import com.pattaya.pattayacallcenter.webservice.object.upload.FileListObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class ProfileActivity extends ActionBarActivity implements View.OnClickListener, View.OnCreateContextMenuListener {
    final RestAdapter restAdapterOpenFireJson = RestAdapterOpenFire.getInstanceJson();
    final OpenfireQueary openfireQueary = restAdapterOpenFireJson.create(OpenfireQueary.class);
    ImageButton btn;
    Button btnSave;
    TextView titleTextView;
    EditText txtUserName, txtSurName, txtMoblie, txtIdCard;
    EditText txtAddress, txtProvince, txtDistrict, txtParish, txtPostCode;
    RoundedImageView circularImageView;
    TextView txtEmail;
    SharedPreferences spConfig;
    SharedPreferences sp;
    RestAdapter webserviceConnector = WebserviceConnector.getInstance();
    RestAdapter webserviceConnectorUpload = WebserviceConnector.getInstanceUpload();
    RestFulQueary adapterRest = null;
    RestFulQueary adapterRestUpload = null;


    String pic = "No";
    String urlImage = null;
    File fileImage = null;
    SharedPreferences.Editor editor;
    Button btnChangePic;
    CameraMange cameraMange;
    AccessUserObject accessUserObject;


    int userId;
    String jid;
    String token;
    Context context = this;
    String clientId = "android";
    String username;
    String diaplayName;

    String fristName;
    String lastName;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        cameraMange = new CameraMange(this);
        circularImageView = (RoundedImageView) findViewById(R.id.img_pic);
        txtProvince = (EditText) findViewById(R.id.txt_provinceName);
        txtDistrict = (EditText) findViewById(R.id.txt_districtName);
        txtParish = (EditText) findViewById(R.id.txt_parishName);
        txtUserName = (EditText) findViewById(R.id.txt_userName);
        txtSurName = (EditText) findViewById(R.id.txt_lastName);
        txtMoblie = (EditText) findViewById(R.id.txt_mobile);
        txtIdCard = (EditText) findViewById(R.id.txt_idCard);
        txtAddress = (EditText) findViewById(R.id.txt_address);
        txtPostCode = (EditText) findViewById(R.id.txt_postNumber);
        txtEmail = (TextView) findViewById(R.id.txt_email);
        btnChangePic = (Button) findViewById(R.id.btn_changeProfile);


        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        btnSave = (Button) findViewById(R.id.btn_saveRegister);

        titleTextView.setText(getResources().getString(R.string.profile_information));
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        btnSave.setOnClickListener(this);
        btn.setOnClickListener(this);
        circularImageView.setOnClickListener(this);
        circularImageView.setOnCreateContextMenuListener(this);
        btnChangePic.setOnClickListener(this);
        btnChangePic.setOnCreateContextMenuListener(this);
        init();

    }


    void init() {
        spConfig = getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        sp = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        token = spConfig.getString(MasterData.SHARED_CONFIG_TOKEN, null);
        clientId = spConfig.getString(MasterData.SHARED_CONFIG_CLIENT_ID, null);
        pic = sp.getString(MasterData.SHARED_USER_IMAGE, "IMAGE FAIL..........");
        userId = sp.getInt(MasterData.SHARED_USER_USER_ID, -10);
        username = sp.getString(MasterData.SHARED_USER_USERNAME, "Unknown");
        jid = sp.getString(MasterData.SHARED_USER_JID, null);
        fristName = sp.getString(MasterData.SHARED_USER_FRIST_NAME, null);
        lastName = sp.getString(MasterData.SHARED_USER_LAST_NAME, null);
        editor = sp.edit();


        txtEmail.setText(username);
        //init Restful Service
        adapterRest = webserviceConnector.create(RestFulQueary.class);
        adapterRestUpload = webserviceConnectorUpload.create(RestFulQueary.class);
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, null, "loading...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        ringProgressDialog.setIndeterminate(true);
        GetUserObject getUserObject = new GetUserObject(userId, token, clientId);
        adapterRest.getUser(getUserObject, new Callback<AccessUserObject>() {
            @Override
            public void success(AccessUserObject accessUserObject, Response response) {
                Log.d("Profile User Access", " " + accessUserObject.toString());

                if (accessUserObject.getFirstname() == null || accessUserObject.getFirstname().isEmpty()) {
                    txtUserName.setText(fristName);
                } else {
                    txtUserName.setText(accessUserObject.getFirstname());
                }
                if (accessUserObject.getLastname() == null || accessUserObject.getLastname().isEmpty()) {
                    txtSurName.setText(lastName);
                } else {
                    txtSurName.setText(accessUserObject.getLastname());
                }


                txtIdCard.setText(accessUserObject.getIdCard());
                txtPostCode.setText(accessUserObject.getPostCode());
                txtMoblie.setText(accessUserObject.getMobile());
                txtAddress.setText(accessUserObject.getAddress());
                //  txtEmail.setText(accessUserObject.getUserImage();
                txtParish.setText(accessUserObject.getAmphur());
                txtProvince.setText(accessUserObject.getProvince());
                txtDistrict.setText(accessUserObject.getDistrict());

                if (accessUserObject.getUserImage() != null) {
                    pic = accessUserObject.getUserImage();
                }


                System.out.println(pic);
                diaplayName = accessUserObject.getDisplayName();

                setImageUrl(pic);
                ringProgressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                ringProgressDialog.dismiss();
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle(getResources().getString(R.string.update_data));
                alertDialog.setMessage(getResources().getString(R.string.cant_connect_server));
                alertDialog.setButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
                alertDialog.show();
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (v == btn) {
            finish();
        } else if (v == btnSave) {

            final ProgressDialog ringProgressDialog = ProgressDialog.show(this, null, getResources().getString(R.string.please_wait), true);
            ringProgressDialog.setCancelable(true);
            ringProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            ringProgressDialog.setIndeterminate(true);
            accessUserObject = new AccessUserObject();
            accessUserObject.setUserId(String.valueOf(userId));
            accessUserObject.setFirstname(txtUserName.getText().toString());
            accessUserObject.setLastname(txtSurName.getText().toString());
            accessUserObject.setIdCard(txtIdCard.getText().toString());
            accessUserObject.setMobile(txtMoblie.getText().toString());
            accessUserObject.setAddress(txtAddress.getText().toString());
            accessUserObject.setPostCode(txtPostCode.getText().toString());
            accessUserObject.setProvince(txtProvince.getText().toString());
            accessUserObject.setDistrict(txtDistrict.getText().toString());
            accessUserObject.setAmphur(txtParish.getText().toString());
            accessUserObject.setAccessToken(token);
            accessUserObject.setClientId(clientId);
            accessUserObject.setEmail(txtEmail.getText().toString());
            accessUserObject.setUserName(txtEmail.getText().toString());

            if (urlImage != null) {
                //update file successfull
                ringProgressDialog.dismiss();
                accessUserObject.setUserImage(urlImage);
                updateDataAll();
            } else {
                if (fileImage != null) {
                    //update image but cannot upload file
                    ringProgressDialog.dismiss();
                    new TaskResizeUpload().execute();

                } else {
                    //not update data Image
                    ringProgressDialog.dismiss();
                    accessUserObject.setUserImage(pic);
                    updateDataAll();

                }
            }


        } else if (v == btnChangePic) {
            v.showContextMenu();
        } else if (v == circularImageView) {
            v.showContextMenu();
        }

    }


    void updateDataAll() {
        final Boolean[] boolUpdate = {false, false};
        Gson gson = new Gson();
        String json = gson.toJson(accessUserObject, AccessUserObject.class);
        System.out.println(json);

        runOnUiThread(new Runnable() {
            public void run() {
                progressDialog = ProgressDialog.show(ProfileActivity.this, null, getResources().getString(R.string.please_wait), true);
                progressDialog.setCancelable(true);
            }
        });
        adapterRest.postUser(accessUserObject, new Callback<UpdateResult>() {
            @Override
            public void success(UpdateResult updateResult, Response response) {
                System.out.println("updateResult = [" + updateResult.getResult() + "], response = [" + response + "]");
                Boolean bool = updateResult.getResult();

                if (bool) {
                    Toast.makeText(getApplication(),
                            getResources().getString(R.string.update_data_success), Toast.LENGTH_SHORT)
                            .show();
                    editor.putString(MasterData.SHARED_USER_IMAGE, urlImage);
                    editor.putString(MasterData.SHARED_USER_FRIST_NAME, txtUserName.getText().toString());
                    editor.putString(MasterData.SHARED_USER_LAST_NAME, txtSurName.getText().toString());
                    editor.commit();
                }
                progressDialog.dismiss();
                finish();

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplication(), "Unable connect server \n Please try again", Toast.LENGTH_SHORT).show();
                System.out.println("error update = [" + error + "]");
                progressDialog.dismiss();
                // alertDialogFailtoServer();
            }
        });
        if (jid != null) {
            String username = jid.split("@")[0];
            UserProperty userProperty = new UserProperty();
            userProperty.setUsername(username);
            userProperty.setName(diaplayName);

            List<MapProperty> mapProperties = new ArrayList<>();
            MapProperty mapProperty = new MapProperty();
            mapProperty.setKey("userImage");
            mapProperty.setValue(accessUserObject.getUserImage());
            mapProperties.add(mapProperty);

            Property property = new Property(mapProperties);

            userProperty.setProperties(property);

            openfireQueary.updateProperty(username, userProperty, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    System.out.println("s = [" + s + "], response = [" + response + "]");

                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println("error = [" + error + "]");

                    // Toast.makeText(getApplication(), "Unable connect server \n Please try again", Toast.LENGTH_SHORT).show();
                }
            });
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
                    fileImage = new File(cameraMange.fileUri.getPath());

                    if (fileImage.exists()) {
                        setImageFile(fileImage);
                        //getImagePathUpload(fileImage);


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
                fileImage = new File(imagesPath[0]);
                if (fileImage.exists()) {
                    setImageFile(fileImage);
                    //getImagePathUpload(fileImage);


                }

            }
        }


    }

    void getImagePathUpload(File file) {
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        System.out.println("file = [" + mimeType + "]");
        adapterRestUpload.uploadImage(new TypedFile(mimeType, file), new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                //Try to get response body
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

                urlImage = fileListObject.getData().get(0).getUrl();
                setImageUrl(urlImage);
                editor = sp.edit();
                editor.putString(MasterData.SHARED_USER_IMAGE, urlImage);


            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");

            }
        });
    }


    void setImageFile(File f) {
        Glide.with(context).load(f)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .override(100, 100)
                .into(circularImageView);
    }

    void setImageUrl(String url) {
        String mUrl = (url == null || url.isEmpty()) ? "No Image" : url;
        System.out.println("File : " + url);
        Glide.with(context).load(mUrl)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .override(200, 200)
                .into(circularImageView);


    }


    class TaskResizeUpload extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog ringProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ringProgressDialog = ProgressDialog.show(ProfileActivity.this, null, getResources().getString(R.string.please_wait), true);
            ringProgressDialog.setCancelable(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String uuid = UUID.randomUUID().toString();
            System.out.println("uuid = " + uuid);
            int randomNum = 500 + (int) (Math.random() * 2000000000);
            File file = new File(getCacheDir(), "pattaya" + randomNum + uuid);
            try {
                file.createNewFile();
            } catch (IOException error) {
                error.printStackTrace();
            }
            Bitmap bitmap = null;
            try {
                // resize
                bitmap = Glide.with(Application.getContext())
                        .load(fileImage)
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

            if (bitmap != null) {
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
                Log.e("File upload", "" + file.length());
                Log.e("File upload", "" + file.getAbsolutePath());
                final long totalSize = file.length();
                System.out.println("FILE LENGTH" + ">>>>>>>>" + totalSize);


                adapterRestUpload.uploadImage(new TypedFile("image/jpeg", fileImage), new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {
                        //Try to get response body
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
                        urlImage = fileListObject.getData().get(0).getUrl();
                        //setImageUrl(urlImage);
                        accessUserObject.setUserImage(urlImage);
                        ringProgressDialog.dismiss();
                        updateDataAll();

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ringProgressDialog.dismiss();
                        System.out.println("error upload = [" + error + "]");
                        Toast.makeText(getApplication(), "Unable connect server \n Please try again", Toast.LENGTH_SHORT).show();


                    }
                });
            } else {
                ringProgressDialog.dismiss();
                Toast.makeText(getApplication(), "This image cannot upload.", Toast.LENGTH_SHORT).show();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.e("Task Upload", "finish");
        }
    }

}
