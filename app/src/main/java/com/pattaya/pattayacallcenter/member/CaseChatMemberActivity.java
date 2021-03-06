package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.Data.Messages;
import com.pattaya.pattayacallcenter.Data.Users;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.chat.NotifyChat;
import com.pattaya.pattayacallcenter.chat.StrickLoaderService;
import com.pattaya.pattayacallcenter.chat.jsonobject.ChatRoomObject;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom.ChatRoom;
import com.pattaya.pattayacallcenter.customview.CameraMange;
import com.pattaya.pattayacallcenter.customview.CustomGalleryActivity;
import com.pattaya.pattayacallcenter.customview.SlideMenuManage;
import com.pattaya.pattayacallcenter.guest.CaseMapActivity;
import com.pattaya.pattayacallcenter.member.Adapter.AdapterMenuCaseChat;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CaseChatMemberActivity extends ActionBarActivity implements View.OnClickListener, ObservableScrollViewCallbacks {


    private final int EDIT_ACTIVITY = 390;
    private final int TAG_INTENT_PLACE = 909;
    private final int TAG_INTENT_CLOSE = 910;
    private final int TAG_INTENT_FORWARD = 911;
    private final RestAdapter restAdapterOpenFire = RestAdapterOpenFire.getInstance();
    private final OpenfireQueary openfireQueary = restAdapterOpenFire.create(OpenfireQueary.class);
    ProgressDialog ringProgressDialog;
    Boolean isOfficial;
    AdapterOfficial adapterOfficial;
    ProgressBar progressBarChat;
    private FrameLayout mMenuContainer;
    private Users otherUser;
    private AdapterChat adapterChat;
    private ObservableListView listChat;
    // widget
    private ImageButton btn;
    private TextView titleTextView;
    private TextView caseTitle;
    //slider buttom
    private GridView gridView;
    private GridView mGridMenu;
    private AdapterMenuCaseChat adapterMenuCaseChat;
    private Button btnCommit;
    private EditText txtMsg;
    private Button btnImage;
    private Button btnUpdateStricker;
    private Button btnCamera;
    private Button btnPlace;
    private ImageButton btnAdd;
    private ImageButton btnSticker;
    private SlideMenuManage mSlideMenuManage;
    private SlideMenuManage mSlideMenuManageSticker;
    private SlideMenuManage mSlideMenuManageImage;
    private CameraMange cameraMange;
    private View mSlideMenuImage;
    private ArrayList<Integer> listStiker;
    private AdapterStricker adapterStricker;
    private ProgressBar progressBar;
    private List<Integer> menu;
    private int idCase;
    private String caseName;
    private ArrayList dataChat;
    private Boolean roomIsCreated = false;
    private String jid;
    private int complainId;
    private TextView txtempty;
    private RestAdapter openfireConnectorJson = RestAdapterOpenFire.getInstanceJson();
    private OpenfireQueary openfireQuearyJson = openfireConnectorJson.create(OpenfireQueary.class);
    private RestAdapter webserviceConnector = WebserviceConnector.getInstanceCase();
    private RestFulQueary adapterRest = webserviceConnector.create(RestFulQueary.class);
    private String token;
    private String clientId;
    private int userId;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finish();
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_chat_member);
        BusProvider.getInstance().register(this);


//        menu = new ArrayList<>();
//        mMenuContainer = (FrameLayout) findViewById(R.id.menuview_container);
//        mGridMenu = (GridView) findViewById(R.id.menugrid);
//        adapterMenuCaseChat = new AdapterMenuCaseChat(this, menu);
//        mGridMenu.setAdapter(adapterMenuCaseChat);


        init();
        setActionBsr();
        ////////////////////////////////////////////////////////////////////////////////////////////
        caseTitle = (TextView) findViewById(R.id.case_name);
        listChat = (ObservableListView) findViewById(R.id.chat);
        //listChat.setScrollViewCallbacks(this);
        caseName = (caseName == null) ? "no name" : caseName;
        caseTitle.setText(caseName);
        ////////////////////////////////////////////////////////////////////////////////////////////


        /** ///////////////////////////////////////////////////////////////////////// */

        //STATE_USER_LOGIN = STATE_MENU_2;
        // initMenuCase(STATE_USER_LOGIN);
        //mSlideMenuManage = new SlideMenuManage(mMenuContainer, this);
        //setClickCaseMenu();


        /** //////////////////////////////////////////////////////////////////////////// */
        mSlideMenuImage = findViewById(R.id.menu_slide);
        txtMsg = (EditText) findViewById(R.id.txt_msg);
        btnCamera = (Button) findViewById(R.id.btn_camera);
        btnImage = (Button) findViewById(R.id.btn_image);
        btnPlace = (Button) findViewById(R.id.btn_place);
        btnCommit = (Button) findViewById(R.id.btn_commit);
        btnAdd = (ImageButton) findViewById(R.id.btn_add);
        btnSticker = (ImageButton) findViewById(R.id.btn_sticker);
        progressBarChat = (ProgressBar) findViewById(R.id.progress);

        mSlideMenuManageImage = new SlideMenuManage(mSlideMenuImage, this);
        mSlideMenuManageImage.setStateAnimate(mSlideMenuManageImage.STATE_BOTTOM);

        cameraMange = new CameraMange(this);

        //btnAdd.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        btnPlace.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnSticker.setOnClickListener(this);

        btn.setOnClickListener(this);

        dataChat = new ArrayList();
        setRoom();
        initSticker();


    }


    void initSticker() {
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
        gridView.setEmptyView(txtempty);

        gridView.setAdapter(adapterStricker);
        btnUpdateStricker = (Button) findViewById(R.id.btnupdate);
        btnUpdateStricker.setOnClickListener(this);
        adapterStricker.querySticker();


        /** /////////////////////////////////// */

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

    }


    void init() {
        SharedPreferences spUser = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        isOfficial = spUser.getBoolean(MasterData.SHARED_IS_OFFICIAL, false);
        if (isOfficial) {
            jid = "pattayaofficial58@pattaya-data";

        } else {
            jid = spUser.getString(MasterData.SHARED_USER_JID, "null");

        }
        userId = spUser.getInt(MasterData.SHARED_USER_USER_ID, 0);

        SharedPreferences spConfig = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        token = spConfig.getString(MasterData.SHARED_CONFIG_TOKEN, null);
        clientId = spConfig.getString(MasterData.SHARED_CONFIG_CLIENT_ID, null);

        Bundle data = getIntent().getExtras();
        if (data != null) {
            //otherUser = data.getParcelable("user");
            caseName = data.getString("casename");
            idCase = data.getInt("id");
            complainId = data.getInt("complainid");

            System.out.println("caseId >>" + idCase);
            System.out.println("name >>" + caseName);
            System.out.println("complainId >>" + complainId);

        }
        SharedPreferences settings = Application.getContext().getSharedPreferences(MasterData.SHARED_CASE_COUNT, Context.MODE_PRIVATE);
        String caseStr = "id_" + complainId;
        settings.edit().putInt(caseStr, 0).commit();


    }

    void setActionBsr() {
        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);

        titleTextView.setText(getResources().getString(R.string.case_chat));
        titleTextView.setPadding(40, 0, 0, 0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }



    @Subscribe
    public void onBusReciver(String event) {

        if (roomIsCreated && event.equalsIgnoreCase(otherUser.getJid())) {
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
        } else if (event.matches("fin_updateChat")) {
            progressBarChat.setVisibility(View.GONE);

        }

    }


    @Subscribe
    public void onBusReciver(Messages messages) {
        if (roomIsCreated) {
            if (messages.getRoom().equalsIgnoreCase(otherUser.getJid())) {
                SharedPreferences settings = Application.getContext().getSharedPreferences(MasterData.SHARED_CASE_COUNT, Context.MODE_PRIVATE);
                String caseStr = "id_" + complainId;
                settings.edit().putInt(caseStr, 0).commit();
                //  Toast.makeText(getApplication(), messages.getRoom() + " New Messages ", Toast.LENGTH_SHORT).show();
                DatabaseChatHelper.init().clearCountLastMessage(messages.getRoom());
                if (!messages.getSender().matches(jid)) {
                    System.out.println("Hello");
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
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    void setRoom() {
        final String roomName;
        if (isOfficial) {
            roomName = "complain-" + complainId;
        } else {
            roomName = "case-" + complainId;
        }
        openfireQueary.getChatRoomDetail(roomName, new Callback<ChatRoom>() {
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
                if (error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 404) {
                        final ChatRoomObject chatRoomObject = new ChatRoomObject();
                        chatRoomObject.setRoomName(roomName);
                        chatRoomObject.setNaturalName(roomName);
                        chatRoomObject.setDescription(roomName);

                        if (chatRoomObject != null) {
                            //chatRoomObject.setDescription(img);
                            openfireQuearyJson.createChatRoom(chatRoomObject, new Callback<Response>() {
                                @Override
                                public void success(Response response, Response response2) {
                                    //System.out.println("response = [" + response + "], response2 = [" + response2 + "]");

                                    roomIsCreated = true;
                                    otherUser = new Users();
                                    otherUser.setJid(chatRoomObject.getRoomName() + "@" + "conference.pattaya-data");
                                    otherUser.setName(chatRoomObject.getRoomName());
                                    otherUser.setType(Users.TYPE_GROUP);
                                    initAdapter();

                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    // System.out.println("error = [" + error + "]");
                                }
                            });
                        }


                    }

                } else {
                    Toast.makeText(getApplication(),
                            "Please check your internet connection", Toast.LENGTH_SHORT)
                            .show();
                }


            }
        });

    }


    void initAdapter() {
        System.out.println("init() isofficial " + isOfficial);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.menu_case_chat_member, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_add:
                mSlideMenuManage.eventShow();
                mSlideMenuManageSticker.stateShowMenu(mSlideMenuManage.SETTING_MENU_HIDE);
                mSlideMenuManageImage.stateShowMenu(mSlideMenuManage.SETTING_MENU_HIDE);
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onClick(View v) {

        if (v == btnCamera) {
            cameraMange.captureImage();
            // mSlideMenuManage.stateShowMenu( mSlideMenuManageImage .SETTING_MENU_HIDE);
            hideKeyboard();


        } else if (v == btnImage) {
            Intent intent = new Intent(this, CustomGalleryActivity.class);
            startActivityForResult(intent, MasterData.PICK_IMAGE_MULTIPLE);
            hideKeyboard();
            // mSlideMenuManage.stateShowMenu( mSlideMenuManageImage .SETTING_MENU_HIDE);
        } else if (v == btnAdd) {
            mSlideMenuManageImage.eventShow();
            mSlideMenuManageSticker.stateShowMenu(mSlideMenuManageImage.SETTING_MENU_HIDE);
            hideKeyboard();

        } else if (v == btnSticker) {
            mSlideMenuManageImage.stateShowMenu(mSlideMenuManageImage.SETTING_MENU_HIDE);
            mSlideMenuManageSticker.eventShow();
            if (!isMyServiceRunning(StrickLoaderService.class)) {
                txtempty.setVisibility(View.GONE);
                btnUpdateStricker.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                hideKeyboard();
            }
        } else if (v == btnPlace) {
            Intent intent = new Intent(this, CaseMapActivity.class);
            startActivityForResult(intent, TAG_INTENT_PLACE);
            hideKeyboard();
        } else if (v == btnUpdateStricker) {

            startService(new Intent(this, StrickLoaderService.class));
            txtempty.setVisibility(View.GONE);
            btnUpdateStricker.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            hideKeyboard();
        } else if (v == btn) {
            finish();
        } else if (v == btnCommit) {
            if (roomIsCreated) {
                if (!txtMsg.getText().toString().matches("")) {
                    if (isOfficial) {
                        adapterOfficial.enterMsg(txtMsg.getText().toString());
                    } else {
                        adapterChat.enterMsg(txtMsg.getText().toString());
                    }
                    // setListViewInBtm();
                }
                // hideKeyboard();
                txtMsg.setText(null);
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

        if (requestCode == TAG_INTENT_CLOSE) {
            if (resultCode == Activity.RESULT_OK) {
                finish();

            }
        }

        if (requestCode == TAG_INTENT_FORWARD) {
            if (resultCode == Activity.RESULT_OK) {
                finish();

            }
        }

        if (requestCode == EDIT_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                //finish();

            }
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

        if (scrollState == ScrollState.UP) {
            if (caseTitle.isShown()) {
                caseTitle.setVisibility(View.GONE);
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!caseTitle.isShown()) {
                caseTitle.setVisibility(View.VISIBLE);
            }
        }

    }
}

