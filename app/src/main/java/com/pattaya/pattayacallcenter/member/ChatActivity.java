package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pattaya.pattayacallcenter.AdapterChat;
import com.pattaya.pattayacallcenter.AdapterStricker;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.Data.Messages;
import com.pattaya.pattayacallcenter.Data.Users;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.chat.NotifyChat;
import com.pattaya.pattayacallcenter.chat.StrickLoaderService;
import com.pattaya.pattayacallcenter.customview.CameraMange;
import com.pattaya.pattayacallcenter.customview.CustomGalleryActivity;
import com.pattaya.pattayacallcenter.customview.SlideMenuManage;
import com.pattaya.pattayacallcenter.guest.CaseMapActivity;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;

public class ChatActivity extends ActionBarActivity implements View.OnClickListener {


    // widget
    ImageButton btn;
    TextView titleTextView;
    //sticker
    Button btnUpdateStricker;
    TextView txtempty;

    //slider buttom
    GridView gridView;
    AdapterStricker adapterStricker;
    Button btnImage;
    Button btnCamera;
    Button btnPlace;
    Button btnCommit;
    ImageButton btnAdd;
    ImageButton btnSticker;
    EditText txtMsg;
    ListView listChat;

    SlideMenuManage mSlideMenuManageSticker;
    SlideMenuManage mSlideMenuManageImage;
    CameraMange cameraMange;
    View mSlideMenuImage;
    private final int PICK_IMAGE_MULTIPLE = 300;
    ArrayList dataChat;
    ArrayList dataUser;
    ArrayList listStiker;

    ////// XMPP Manage//////
    DatabaseChatHelper databaseChatHelper;

    String mUser = "";
    AdapterChat adapterChat;
    Users otherUser;
    String jid;
    ProgressBar progressBar;

    int TAG_INTENT_PLACE = 909;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        setActionBar();
        init();

        /** //////////////////////////////////////////////////////////////////////////// */
        mSlideMenuImage = findViewById(R.id.menu_slide);
        txtMsg = (EditText) findViewById(R.id.txt_msg);
        btnCamera = (Button) findViewById(R.id.btn_camera);
        btnImage = (Button) findViewById(R.id.btn_image);
        btnPlace = (Button) findViewById(R.id.btn_place);
        btnCommit = (Button) findViewById(R.id.btn_commit);


        btnAdd = (ImageButton) findViewById(R.id.btn_add);
        btnSticker = (ImageButton) findViewById(R.id.btn_sticker);

        mSlideMenuManageImage = new SlideMenuManage(mSlideMenuImage, this);
        mSlideMenuManageImage.setStateAnimate(mSlideMenuManageImage.STATE_BOTTOM);

        cameraMange = new CameraMange(this);
        btnCamera.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        btnPlace.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnSticker.setOnClickListener(this);
        btnCommit.setOnClickListener(this);

        /** / Sticker Adapter////////////// */
        txtempty = (TextView) findViewById(R.id.empty_sticker);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        View stickerView = findViewById(R.id.menu_slide_sticker);
        gridView = (GridView) findViewById(R.id.grid_sticker);
        listStiker = new ArrayList<>();


        mSlideMenuManageSticker = new SlideMenuManage(stickerView, this);
        mSlideMenuManageSticker.setStateAnimate(mSlideMenuManageSticker.STATE_BOTTOM);
        adapterStricker = new AdapterStricker(this, R.layout.custom_image_grid, listStiker);
        adapterStricker.setSlideMenuManage(mSlideMenuManageSticker);
        gridView.setAdapter(adapterStricker);
        gridView.setEmptyView(txtempty);

        /** /////////////////////////////////// */
        dataChat = new ArrayList();
        dataUser = new ArrayList();
        listChat = (ListView) findViewById(R.id.chat);

        if (otherUser != null) {
            titleTextView.setText(otherUser.getName());
            adapterChat = new AdapterChat(this, dataChat, otherUser, mUser, listChat);
            listChat.setAdapter(adapterChat);
            DatabaseChatHelper.init().clearCountLastMessage(otherUser.getJid());
            adapterChat.queryChatLogs(true);
        }
        adapterStricker.querySticker();


        /** /////////////////////////////////////////////*/
        btnUpdateStricker = (Button) findViewById(R.id.btnupdate);
        btnUpdateStricker.setOnClickListener(this);

        adapterStricker.SetOnItemClickListener(new AdapterStricker.OnItemClickListener() {
            @Override
            public void onItemClick(String image) {
                adapterChat.enterMsg(image);
                //setListViewInBtm();

            }
        });


        txtMsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (adapterChat != null) {
                    //adapterChat.setListViewInBtm();
                }


            }
        });


    }


    void setActionBar() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);

        titleTextView.setText(getResources().getString(R.string.chat_room));
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

    void init() {
        // init Data
        NotifyChat.cancelNotification(NotifyChat.NOTIFY_CHAT_ID);
        databaseChatHelper = DatabaseChatHelper.init();
        Bundle data = getIntent().getExtras();
        otherUser = data.getParcelable("user");

        jid = data.getString("jid");
        if (jid != null) {
            otherUser = databaseChatHelper.getOneUsers(jid);
            System.out.println("nonononnono  " + otherUser.getName());

            if (otherUser == null) {
                System.out.println("null>>>>" + jid);
            }
        }

        SharedPreferences spUser = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        mUser = spUser.getString(MasterData.SHARED_USER_JID, "null");

    }


    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onBusReciver(String event) {
        if (event.equalsIgnoreCase(otherUser.getJid())) {
            adapterChat.queryChatLogs(true);
            //   Toast.makeText(getApplication(), event, Toast.LENGTH_SHORT).show();
//            setListViewInBtm();
        } else if (event.matches("sticker_fin")) {
            System.out.println("Finish sticker ");
            //adapterStricker.querySticker();
            btnUpdateStricker.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else if (event.matches("sticker_update")) {
            System.out.println("update sticker ");
            adapterStricker.querySticker();
        } else if (event.matches("sticker_fin_no_data")) {
            if (adapterStricker.getCount() == 0) {
                txtempty.setVisibility(View.VISIBLE);
            }
            btnUpdateStricker.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

    }

    @Subscribe
    public void onBusReciver(Messages messages) {
        if (messages.getRoom().equalsIgnoreCase(otherUser.getJid())) {
            //  Toast.makeText(getApplication(), messages.getRoom() + " New Messages ", Toast.LENGTH_SHORT).show();
            DatabaseChatHelper.init().clearCountLastMessage(messages.getRoom());
            if (!messages.getSender().matches(mUser)) {
                NotifyChat.cancelNotification(NotifyChat.NOTIFY_CHAT_ID);
                adapterChat.queryChatLogsNoReset(messages, false);
                //setListViewInBtm();
            }

        }

    }


    @Override
    public void onClick(View v) {
        if (v == btnCamera) {
            cameraMange.captureImage();
            mSlideMenuManageImage.stateShowMenu(mSlideMenuManageImage.SETTING_MENU_HIDE);

        } else if (v == btnImage) {
            Intent intent = new Intent(this, CustomGalleryActivity.class);
            startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
            mSlideMenuManageImage.stateShowMenu(mSlideMenuManageImage.SETTING_MENU_HIDE);


        } else if (v == btnAdd) {
            mSlideMenuManageImage.eventShow();
            mSlideMenuManageSticker.stateShowMenu(mSlideMenuManageSticker.SETTING_MENU_HIDE);


        } else if (v == btnSticker) {
            mSlideMenuManageImage.stateShowMenu(mSlideMenuManageImage.SETTING_MENU_HIDE);
            mSlideMenuManageSticker.eventShow();
            if (!isMyServiceRunning(StrickLoaderService.class)) {
                txtempty.setVisibility(View.GONE);
                btnUpdateStricker.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

        } else if (v == btnPlace) {
            mSlideMenuManageImage.stateShowMenu(mSlideMenuManageImage.SETTING_MENU_HIDE);
            Intent intent = new Intent(this, CaseMapActivity.class);
            startActivityForResult(intent, TAG_INTENT_PLACE);

        } else if (v == btnCommit) {
            if (!txtMsg.getText().toString().matches("")) {
                adapterChat.enterMsg(txtMsg.getText().toString());
                //setListViewInBtm();
            }
            txtMsg.setText(null);

        } else if (v == btnUpdateStricker) {
            startService(new Intent(this, StrickLoaderService.class));
            txtempty.setVisibility(View.GONE);
            btnUpdateStricker.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);


    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
                        String[] imagesPath = new String[1];
                        imagesPath[0] = imgFile.getPath();
                        adapterChat.addImageList(imagesPath);
                        //setListViewInBtm();
                        // mGridAdapter.addItem(imgFile.getPath(), "!");

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
                adapterChat.addImageList(imagesPath);
                //setListViewInBtm();


            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled Image capture
                System.out.println("User cancelled select image ");

            } else {
                // failed to capture image
                System.out.println("Sorry! Failed to select image");

            }
        }


        if (requestCode == TAG_INTENT_PLACE) {
            if (resultCode == Activity.RESULT_OK) {
                String detail = data.getStringExtra("detail");
                if (!detail.matches("")) {
                    adapterChat.enterMsg("My location is \n" + detail);
                    //setListViewInBtm();
                }

            }
        }


    }


}
