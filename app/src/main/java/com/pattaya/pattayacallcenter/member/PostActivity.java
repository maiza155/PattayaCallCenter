package com.pattaya.pattayacallcenter.member;

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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.pattaya.pattayacallcenter.customview.CameraMange;
import com.pattaya.pattayacallcenter.customview.CustomGalleryActivity;
import com.pattaya.pattayacallcenter.customview.ExpandableHeightGridView;
import com.pattaya.pattayacallcenter.guest.CaseDetail.ImageData;
import com.pattaya.pattayacallcenter.guest.CaseDetail.ImageUpdateGridAdapter;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.PostObject;
import com.pattaya.pattayacallcenter.webservice.object.SavePostObject;
import com.pattaya.pattayacallcenter.webservice.object.UpdateResult;
import com.pattaya.pattayacallcenter.webservice.object.casedata.ImageObject;
import com.pattaya.pattayacallcenter.webservice.object.upload.FileListObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PostActivity extends ActionBarActivity implements View.OnClickListener, View.OnCreateContextMenuListener {

    private final RestAdapter restAdapterUpload = WebserviceConnector.getInstanceUpload();
    private final RestAdapter restAdapterPost = WebserviceConnector.getInstanceCartdUI();
    private final RestFulQueary restFulQuearyPost = restAdapterPost.create(RestFulQueary.class);
    private final RestFulQueary restFulQuearyUpload = restAdapterUpload.create(RestFulQueary.class);
    ProgressDialog ringProgressDialog;
    PostObject postObject;
    private List<String> imageData;
    private List imageUploadURL;
    private CameraMange cameraMange;
    private ImageUpdateGridAdapter mGridAdapter;
    private ExpandableHeightGridView gridView;
    private View btnImage;
    private EditText txt_detail;
    private TextView imageHeader;
    private ProgressListener listener;
    // widget
    private View btn;
    private TextView titleTextView;
    private SharedPreferences spUser;
    private int userId;
    private int postId = 0;
    private SavePostObject savePostObject;
    private List<ImageObject> listOldImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Bundle data = getIntent().getExtras();

        txt_detail = (EditText) findViewById(R.id.detail);
        imageHeader = (TextView) findViewById(R.id.image_header);
        setActionBar();


        btn.setOnClickListener(this);
        imageHeader.setOnClickListener(this);
        imageHeader.setOnCreateContextMenuListener(this);
        initImageManage();
        initRest();

        if (data != null) {
            postObject = data.getParcelable("postdata");
            System.out.println("postId " + postObject.getPostId());
            txt_detail.setText(postObject.getDetail());
            postId = postObject.getPostId();
            if (postObject.getPostImageList().size() > 0) {
                for (String e : postObject.getPostImageList()) {
                    mGridAdapter.addItemUpdate(e);
                }


            }

        }

    }


    void setActionBar() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = v.findViewById(R.id.add_left_menu);
        titleTextView.setText(getResources().getString(R.string.post));
        titleTextView.setPadding(40, 0, 0, 0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
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
    }


    void initRest() {
        spUser = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        userId = spUser.getInt(MasterData.SHARED_USER_USER_ID, 0);
        System.out.println(userId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_ac, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_save) {

            if (txt_detail.getText().toString().matches("")) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(txt_detail);
                Toast.makeText(getApplication(),
                        getResources().getString(R.string.please_enter_fill), Toast.LENGTH_SHORT)
                        .show();

            } else {
                new TaskReSizeImage().execute();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void savePost() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(PostActivity.this, null, getResources().getString(R.string.please_wait), true);
        ringProgressDialog.setCancelable(true);
        restFulQuearyPost.savePost(savePostObject, new Callback<UpdateResult>() {
            @Override
            public void success(UpdateResult updateResult, Response response) {
                Toast.makeText(getApplication(),
                        "success", Toast.LENGTH_SHORT)
                        .show();
                BusProvider.getInstance().post("post");
                Activity activity = PostActivity.this;
                if (activity != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (postId > 0) {
                                Log.e("TAG", "Update Caassst");
                                ArrayList<String> listOfStrings = new ArrayList<String>();
                                if (savePostObject.getPostImageList() != null) {
                                    listOfStrings.addAll(savePostObject.getPostImageList());
                                }

                                Intent intent = new Intent();
                                intent.putExtra("detail", savePostObject.getDetail());
                                intent.putExtra("postId", postId);
                                intent.putStringArrayListExtra("image", listOfStrings);
                                setResult(Activity.RESULT_OK, intent);
                            }
                            ringProgressDialog.dismiss();
                            finish();
                        }
                    });


                }


            }

            @Override
            public void failure(RetrofitError error) {
                Activity activity = PostActivity.this;
                if (activity != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertDialogFailtoServer();
                            ringProgressDialog.dismiss();
                        }
                    });

                }


            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v == btnImage) {

            v.showContextMenu();
        } else if (v == btn) {
            finish();
        } else if (v == imageHeader) {
            v.showContextMenu();
        }

    }

    void alertDialogFailtoServer() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getResources().getString(R.string.update_data));
        alertDialog.setMessage(getResources().getString(R.string.cant_connect_server));
        alertDialog.setButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                //finish();
            }
        });
        alertDialog.show();
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


    }

    class TaskReSizeImage extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog ringProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ringProgressDialog = ProgressDialog.show(PostActivity.this, null, getResources().getString(R.string.please_wait), true);
            ringProgressDialog.setCancelable(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            savePostObject = new SavePostObject();
            savePostObject.setDetail(txt_detail.getText().toString());
            savePostObject.setPostById(userId);
            savePostObject.setPostType("0");
            if (postId > 0) {
                savePostObject.setPostId(postId);
            }
            final List<String> listImageURL = new ArrayList<>();
            if (mGridAdapter.getData().size() > 0) {
                int imageCount = 0;
                for (ImageData e : mGridAdapter.getData()) {
                    if (e.getTag() == 1) {
                        imageCount++;
                        String uuid = UUID.randomUUID().toString();
                        System.out.println("uuid = " + uuid);
                        System.out.println(e);
                        int randomNum = 500 + (int) ((Math.random() * 1204006080) / Math.random());
                        File file = new File(getCacheDir(), "pattaya-post" + randomNum + uuid + ".jpg");
                        try {
                            file.createNewFile();
                        } catch (IOException error) {
                            error.printStackTrace();
                        }
                        Bitmap bitmap = null;
                        try {
                            bitmap = Glide.with(Application.getContext())
                                    .load(e.getPath())
                                    .asBitmap()
                                    .fitCenter()
                                    .into(700, 700) // Width and height
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


                        //String mimeType = URLConnection.guessContentTypeFromName(file.getName());
                        System.out.println(file.getName().toString());
                        final long totalSize = file.length();
                        final int finalImageCount = imageCount;
                        listener = new ProgressListener() {
                            String fileID = "Please wait... \nimage" + finalImageCount + " uploading";

                            @Override
                            public void transferred(long num) {
                                final int[] count = {Math.round(((num / (float) totalSize) * 100))};
                                runOnUiThread(new Thread(new Runnable() {
                                    public void run() {
                                        if (count[0] == 100) {
                                            if (ringProgressDialog != null)
                                                ringProgressDialog.setMessage(fileID);
                                        }
                                    }
                                }));


                            }
                        };

                        restFulQuearyUpload.uploadImage(new CountingTypedFile("image/jpeg", file, listener), new Callback<Response>() {
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

                                listImageURL.add(fileListObject.getData().get(0).getUrl());
                                if (listImageURL.size() == mGridAdapter.getData().size()) {
                                    Activity activity = PostActivity.this;
                                    if (activity != null) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                savePostObject.setPostImageList(listImageURL);
                                                if (ringProgressDialog != null)
                                                    ringProgressDialog.dismiss();
                                                savePost();
                                            }
                                        });
                                        System.out.println("UPload Complete!!!");

                                    }

                                }

                            }

                            @Override
                            public void failure(RetrofitError error) {
                                System.out.println("error = [" + error + "]");
                                Activity activity = PostActivity.this;
                                if (activity != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialogFailtoServer();
                                            if (ringProgressDialog != null)
                                                ringProgressDialog.dismiss();
                                        }
                                    });

                                }


                            }
                        });


                    } else if (e.getTag() == 0) {
                        listImageURL.add(e.getPath());
                        if (listImageURL.size() == mGridAdapter.getData().size()) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Activity activity = PostActivity.this;
                                    if (activity != null) {
                                        savePostObject.setPostImageList(listImageURL);
                                        if (ringProgressDialog != null)
                                            ringProgressDialog.dismiss();
                                        savePost();
                                    }


                                }
                            });


                            System.out.println("UPload Complete!!!");
                        }
                    }
                }


            } else {
                Activity activity = PostActivity.this;
                if (activity != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ringProgressDialog.dismiss();
                            savePost();
                        }
                    });

                }

            }

            return null;
        }
    }


}
