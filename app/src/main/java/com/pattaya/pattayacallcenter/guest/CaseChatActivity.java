package com.pattaya.pattayacallcenter.guest;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.pattaya.pattayacallcenter.AdapterChat;
import com.pattaya.pattayacallcenter.AdapterOfficial;
import com.pattaya.pattayacallcenter.AdapterStricker;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.Data.Messages;
import com.pattaya.pattayacallcenter.Data.Users;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.chat.NotifyChat;
import com.pattaya.pattayacallcenter.chat.StrickLoaderService;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom.ChatRoom;
import com.pattaya.pattayacallcenter.customview.CameraMange;
import com.pattaya.pattayacallcenter.customview.CustomGalleryActivity;
import com.pattaya.pattayacallcenter.customview.SlideMenuManage;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CaseChatActivity extends ActionBarActivity implements View.OnClickListener, ObservableScrollViewCallbacks {
    final RestAdapter restAdapterOpenFire = RestAdapterOpenFire.getInstance();
    final OpenfireQueary openfireQueary = restAdapterOpenFire.create(OpenfireQueary.class);
    private final int TAG_INTENT_PLACE = 909;
    Button btnCommit;
    EditText txtMsg;
    Button btnImage;
    Button btnUpdateStricker;
    Button btnCamera;
    Button btnPlace;
    ImageButton btnAdd;
    ImageButton btnSticker;
    SlideMenuManage mSlideMenuManage;
    SlideMenuManage mSlideMenuManageSticker;
    SlideMenuManage mSlideMenuManageImage;
    CameraMange cameraMange;
    View mSlideMenuImage;
    AdapterChat adapterChat;
    AdapterOfficial adapterOfficial;
    ObservableListView listChat;
    TextView caseTitle;
    // widget
    ImageButton btn;
    TextView titleTextView;
    TextView txtempty;
    Users otherUser;
    GridView gridView;
    ArrayList<Integer> listStiker;
    AdapterStricker adapterStricker;
    ProgressBar progressBar;
    int complainId;
    Boolean roomIsCreated = false;
    String jid;
    int idCase;
    String caseName;
    ArrayList dataChat;
    Boolean isOfficial;
    RestAdapter openfireConnectorJson = RestAdapterOpenFire.getInstanceJson();
    OpenfireQueary openfireQuearyJson = openfireConnectorJson.create(OpenfireQueary.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_chat);

        BusProvider.getInstance().register(this);
        Bundle data = getIntent().getExtras();

        idCase = data.getInt("id");
        complainId = data.getInt("complainid");
        caseName = data.getString("casename");

        caseTitle = (TextView) findViewById(R.id.case_name);
        listChat = (ObservableListView) findViewById(R.id.chat);
        //listChat.setScrollViewCallbacks(this);
        caseName = (caseName == null) ? "no name" : caseName;
        caseTitle.setText("เรื่อง - " + caseName);

        mSlideMenuImage = findViewById(R.id.menu_slide);
        txtMsg = (EditText) findViewById(R.id.txt_msg);
        btnCamera = (Button) findViewById(R.id.btn_camera);
        btnImage = (Button) findViewById(R.id.btn_image);
        btnPlace = (Button) findViewById(R.id.btn_place);
        btnCommit = (Button) findViewById(R.id.btn_commit);
        btnAdd = (ImageButton) findViewById(R.id.btn_add);
        btnSticker = (ImageButton) findViewById(R.id.btn_sticker);


        mSlideMenuManage = new SlideMenuManage(mSlideMenuImage, this);
        mSlideMenuManage.setStateAnimate(mSlideMenuManage.STATE_BOTTOM);


        cameraMange = new CameraMange(this);

        //btnAdd.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        btnPlace.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnSticker.setOnClickListener(this);

        /** / Sticker Adapter////////////// */
        txtempty = (TextView) findViewById(R.id.empty_sticker);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        View stickerView = findViewById(R.id.menu_slide_sticker);
        gridView = (GridView) findViewById(R.id.grid_sticker);
        listStiker = new ArrayList();
        adapterStricker = new AdapterStricker(this, R.layout.custom_image_grid, listStiker);
        gridView.setAdapter(adapterStricker);

        mSlideMenuManageSticker = new SlideMenuManage(stickerView, this);
        mSlideMenuManageSticker.setStateAnimate(mSlideMenuManageSticker.STATE_BOTTOM);
        adapterStricker.setSlideMenuManage(mSlideMenuManageSticker);


        gridView.setAdapter(adapterStricker);
        btnUpdateStricker = (Button) findViewById(R.id.btnupdate);
        btnUpdateStricker.setOnClickListener(this);
        adapterStricker.querySticker();

        mSlideMenuManageImage = new SlideMenuManage(mSlideMenuImage, this);
        mSlideMenuManageImage.setStateAnimate(mSlideMenuManageImage.STATE_BOTTOM);

        /** /////////////////////////////////// */
        dataChat = new ArrayList();
        SharedPreferences spUser = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        isOfficial = spUser.getBoolean(MasterData.SHARED_IS_OFFICIAL, false);
        if (isOfficial) {
            jid = "pattayaofficial58@pattaya-data";

        } else {
            jid = spUser.getString(MasterData.SHARED_USER_JID, "null");

        }



        setRoom();



        adapterStricker.SetOnItemClickListener(new AdapterStricker.OnItemClickListener() {
            @Override
            public void onItemClick(String image) {
                if (isOfficial) {
                    if (adapterOfficial != null) {
                        adapterOfficial.enterMsg(image);
                    }
                } else {
                    if (adapterChat != null) {
                        adapterChat.enterMsg(image);
                    }
                }


                //setListViewInBtm();

            }
        });

        setActionBar();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    void initAdapter() {
        if (isOfficial) {
            adapterOfficial = new AdapterOfficial(this, dataChat, otherUser, jid, listChat);
            listChat.setAdapter(adapterOfficial);
            adapterOfficial.queryChatLogs(true);
        } else {
            adapterChat = new AdapterChat(this, dataChat, otherUser, jid, listChat);
            listChat.setAdapter(adapterChat);
            adapterChat.queryChatLogs(true);
        }

    }


    @Subscribe
    public void onBusReciver(String event) {

        if (roomIsCreated && event.equalsIgnoreCase(otherUser.getJid())) {

            System.out.println("recive my log");
            if (isOfficial) {
                adapterOfficial.queryChatLogs(true);
            } else {
                adapterChat.queryChatLogs(true);
            }

            // Toast.makeText(getApplication(), event, Toast.LENGTH_SHORT).show();
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
        if (roomIsCreated) {
            if (messages.getRoom().equalsIgnoreCase(otherUser.getJid())) {
                //  Toast.makeText(getApplication(), messages.getRoom() + " New Messages ", Toast.LENGTH_SHORT).show();
                DatabaseChatHelper.init().clearCountLastMessage(messages.getRoom());
                if (!messages.getSender().matches(jid)) {
                    NotifyChat.cancelNotification(NotifyChat.NOTIFY_CHAT_ID);
                    if (isOfficial) {
                        adapterOfficial.queryChatLogsNoReset(messages, false);
                    } else {
                        adapterChat.queryChatLogsNoReset(messages, false);
                    }

                }

            }
        }

    }

    @Override
    public void onClick(View v) {


        if (v == btnCamera) {
            cameraMange.captureImage();
            mSlideMenuManage.stateShowMenu(mSlideMenuManage.SETTING_MENU_HIDE);

        } else if (v == btnImage) {
            Intent intent = new Intent(this, CustomGalleryActivity.class);
            startActivityForResult(intent, MasterData.PICK_IMAGE_MULTIPLE);
            mSlideMenuManage.stateShowMenu(mSlideMenuManage.SETTING_MENU_HIDE);
        } else if (v == btnAdd) {
            mSlideMenuManageImage.eventShow();
            mSlideMenuManageSticker.stateShowMenu(mSlideMenuManage.SETTING_MENU_HIDE);

        } else if (v == btnSticker) {
            mSlideMenuManageImage.stateShowMenu(mSlideMenuManage.SETTING_MENU_HIDE);
            mSlideMenuManageSticker.eventShow();
            if (!isMyServiceRunning(StrickLoaderService.class)) {
                btnUpdateStricker.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        } else if (v == btnPlace) {
            Intent intent = new Intent(this, CaseMapActivity.class);
            startActivityForResult(intent, TAG_INTENT_PLACE);
        } else if (v == btnUpdateStricker) {
            startService(new Intent(this, StrickLoaderService.class));
            txtempty.setVisibility(View.GONE);
            btnUpdateStricker.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else if (v == btn) {
            finish();
        } else if (v == btnCommit) {
            if (roomIsCreated) {
                if (!txtMsg.getText().toString().matches("")) {
                    txtempty.setVisibility(View.GONE);
                    if (isOfficial) {
                        adapterOfficial.enterMsg(txtMsg.getText().toString());
                    } else {
                        adapterChat.enterMsg(txtMsg.getText().toString());
                    }

                    // setListViewInBtm();
                }
                txtMsg.setText(null);
            }


        }

        hideKeyboard();

    }


    void setRoom() {
        final String roomName = "case-" + complainId;
        openfireQueary.getChatRoomDetail("case-" + complainId, new Callback<ChatRoom>() {
            @Override
            public void success(ChatRoom chatRoom, Response response) {
                System.out.println("chatRoom = [" + chatRoom.getRoomName() + "], response = [" + response + "]");
                roomIsCreated = true;
                otherUser = new Users();
                otherUser.setJid(chatRoom.getRoomName() + "@" + "conference.pattaya-data");
                otherUser.setName(chatRoom.getRoomName());
                otherUser.setType(Users.TYPE_GROUP);
                initAdapter();
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");
                Toast.makeText(getApplication(),
                        "Please check your internet connection", Toast.LENGTH_SHORT)
                        .show();

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
                        String[] imagesPath = new String[1];
                        imagesPath[0] = imgFile.getPath();
                        if (isOfficial) {
                            adapterOfficial.addImageList(imagesPath);
                        } else {
                            adapterChat.addImageList(imagesPath);
                        }


                        // setListViewInBtm();
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
                if (isOfficial) {
                    adapterOfficial.addImageList(imagesPath);
                } else {
                    adapterChat.addImageList(imagesPath);
                }

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
                    if (isOfficial) {
                        adapterOfficial.enterMsg("My location is \n" + detail);
                    } else {
                        adapterChat.enterMsg("My location is \n" + detail);
                    }

                    // setListViewInBtm();
                }

            }
        }


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


    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    public void onScrollChanged(int i, boolean b, boolean b2) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        /*
        if (scrollState == ScrollState.UP) {
            if (caseTitle.isShown()) {
                caseTitle.setVisibility(View.GONE);
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!caseTitle.isShown()) {
                caseTitle.setVisibility(View.VISIBLE);
            }
        }*/

    }
}
