package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.jsonobject.ChatRoomObject;
import com.pattaya.pattayacallcenter.chat.jsonobject.Members;
import com.pattaya.pattayacallcenter.chat.jsonobject.Outcasts;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom.ChatRoom;
import com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom.Member;
import com.pattaya.pattayacallcenter.customview.CameraMange;
import com.pattaya.pattayacallcenter.customview.CustomGalleryActivity;
import com.pattaya.pattayacallcenter.customview.NonScrollListView;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.member.Adapter.AdapterListCreateGroup;
import com.pattaya.pattayacallcenter.member.data.InviteFriendObject;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.upload.FileListObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class CreateGroupActivity extends ActionBarActivity implements View.OnClickListener, View.OnCreateContextMenuListener {

    private AdapterListCreateGroup adapterListCreateGroup; //Adapter List ที่เรากำหนดขึ้นเอง
    private ArrayList listDataCreateGroup = new ArrayList<>(); //list ในการเก็บข้อมูลของ DataShow
    private NonScrollListView listViewDataCreateGroup;
    private Button btn_changePhoto;
    private Button btn_createGroup;
    private Button btn_invite;
    private Intent intent;
    // widget
    private ImageButton btn;
    private TextView titleTextView;
    private TextView txtName;
    private int TAG_ADD_FRIEND_ACTIVITY = 807;
    private SharedPreferences sp;
    private String jid;

    private RoundedImageView image;

    private String urlImage = null;
    private File fileImage = null;
    private CameraMange cameraMange;


    private final RestAdapter restAdapterOpenFire = RestAdapterOpenFire.getInstance();
    private final OpenfireQueary openfireQueary = restAdapterOpenFire.create(OpenfireQueary.class);

    private RestAdapter openfireConnectorJson = RestAdapterOpenFire.getInstanceJson();
    private OpenfireQueary openfireQuearyJson = openfireConnectorJson.create(OpenfireQueary.class);

    private RestAdapter webserviceConnectorUpload = WebserviceConnector.getInstanceUpload();
    private RestFulQueary adapterRestUpload = webserviceConnectorUpload.create(RestFulQueary.class);

    private Boolean isUpdate;

    private String room, naturalName, description;
    private ArrayList memberlist = new ArrayList();
    private ArrayList outcastlist = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        isUpdate = getIntent().getBooleanExtra("update", false);
        sp = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        jid = sp.getString(MasterData.SHARED_USER_JID, null);

        SetActionBar();
        btn_changePhoto = (Button) findViewById(R.id.btn_changeProfile);
        btn_createGroup = (Button) findViewById(R.id.btn_createGroup);
        btn_invite = (Button) findViewById(R.id.btn_invite);
        txtName = (TextView) findViewById(R.id.name);
        image = (RoundedImageView) findViewById(R.id.image);
        cameraMange = new CameraMange(this);

        Onclick();
        if (isUpdate) {
            btn_createGroup.setText("Update");
            String[] data = getIntent().getStringArrayExtra("data");
            naturalName = data[0];
            room = data[1].split("@")[0];
            description = data[2];
            txtName.setText(naturalName);
            Glide.with(this)
                    .load(description)
                    .override(200, 200)
                    .fitCenter()
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                    .error(R.drawable.com_facebook_profile_picture_blank_square)
                    .into(image);
            getUserInRooom();

        }

        listViewDataCreateGroup = (NonScrollListView) findViewById(R.id.list_itemMember);
        adapterListCreateGroup = new AdapterListCreateGroup(getBaseContext(), listDataCreateGroup);
        listViewDataCreateGroup.setAdapter(adapterListCreateGroup);
        listViewDataCreateGroup.setEmptyView(findViewById(R.id.txt_empty));

    }

    private void Onclick() {
        btn_changePhoto.setOnClickListener(this);
        btn_createGroup.setOnClickListener(this);
        btn_invite.setOnClickListener(this);
        btn_changePhoto.setOnCreateContextMenuListener(this);
    }

    private void SetActionBar() {

        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);

        btn.setPadding(0, 0, 0, 0);
        titleTextView.setText(getResources().getString(R.string.create_group));
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        btn.setOnClickListener(this);


    }


    void getUserInRooom() {
        memberlist = new ArrayList();
        outcastlist = new ArrayList();
        openfireQueary.getChatRoomDetail(room, new Callback<ChatRoom>() {
            @Override
            public void success(final ChatRoom chatRoom, Response response) {

                if (chatRoom.getOutcasts().getListMember() != null) {
                    for (final Member e : chatRoom.getOutcasts().getListMember()) {
                        outcastlist.add(e.getText());
                        System.out.println("outcast>>>" + e.getText());
                    }
                }

                if (chatRoom.getMembers().getListMember() != null) {
                    for (final Member e : chatRoom.getMembers().getListMember()) {
                        memberlist.add(e.getText());
                        System.out.println("member>>>" + e.getText());
                    }
                }


            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");
                finish();
                Toast.makeText(getApplication(),
                        getResources().getString(R.string.cant_connect_server), Toast.LENGTH_SHORT)
                        .show();

            }
        });


    }

    ChatRoomObject setData() {
        ChatRoomObject chatRoomObject = null;
        if (txtName.getText().toString().matches("")) {
            Toast.makeText(getApplication(),
                    getResources().getString(R.string.please_enter_fill), Toast.LENGTH_SHORT)
                    .show();
        } else {
            chatRoomObject = new ChatRoomObject();

            chatRoomObject.setNaturalName(txtName.getText().toString());

            chatRoomObject.setDescription(txtName.getText().toString());
            Outcasts outcasts = new Outcasts();
            if (adapterListCreateGroup.getCount() > 0) {
                List<String> inviteUser = new ArrayList<>();
                if (isUpdate) {
                    inviteUser.addAll(outcastlist);
                }

                for (InviteFriendObject e : adapterListCreateGroup.getListData()) {
                    inviteUser.add(e.getJid());
                }
                outcasts.setOutcast(inviteUser);
                chatRoomObject.setOutcasts(outcasts);

            } else {
                if (isUpdate) {
                    outcasts.setOutcast(outcastlist);
                    chatRoomObject.setOutcasts(outcasts);
                }

            }

            Members members = new Members();
            if (!isUpdate) {
                List<String> user = new ArrayList<>();
                user.add(jid);
                members.setMember(user);
            } else {
                members.setMember(memberlist);

            }
            chatRoomObject.setMembers(members);


            //
            Random r = new Random();
            char c = (char) (r.nextInt(26) + 'a');
            char c2 = (char) (r.nextInt(26) + 'a');
            int Low = 2000;
            int High = 1000000;
            int R = r.nextInt(High - Low) + Low;
            chatRoomObject.setRoomName("pattaya-" + c + c2 + R);


        }
        return chatRoomObject;
    }


    void createChatroom(String img) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.update_data), getResources().getString(R.string.please_wait), true);
        ChatRoomObject chatRoomObject = setData();
        if (chatRoomObject != null) {
            chatRoomObject.setDescription(img);
            openfireQuearyJson.createChatRoom(chatRoomObject, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    System.out.println("response = [" + response + "], response2 = [" + response2 + "]");

                    Toast.makeText(getApplication(),
                            "success", Toast.LENGTH_SHORT)
                            .show();
                    BusProvider.getInstance().post("add_roster_complete");
                    ringProgressDialog.dismiss();
                    finish();
                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println("error = [" + error + "]");
                    ringProgressDialog.dismiss();
                    Toast.makeText(getApplication(),
                            "Please check your internet connection", Toast.LENGTH_SHORT)
                            .show();

                }
            });
        } else {
            ringProgressDialog.dismiss();
        }

    }


    void updateChatroom(String img) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.update_data), getResources().getString(R.string.please_wait), true);
        final ChatRoomObject chatRoomObject = setData();
        if (chatRoomObject != null) {
            chatRoomObject.setDescription((img.matches("")) ? description : img);

            chatRoomObject.setRoomName(room);
            openfireQuearyJson.updateChatRoomData(room, chatRoomObject, new Callback<Response>() {

                @Override
                public void success(Response response, Response response2) {
                    System.out.println("response = [" + response + "], response2 = [" + response2 + "]");
                    BusProvider.getInstance().post("update_last_message");
                    BusProvider.getInstance().post("add_roster_complete");
                    Intent intent1 = new Intent();
                    intent1.putExtra("name", chatRoomObject.getNaturalName());
                    setResult(Activity.RESULT_OK, intent1);
                    Toast.makeText(getApplication(),
                            "success", Toast.LENGTH_SHORT)
                            .show();
                    ringProgressDialog.dismiss();
                    finish();
                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println("error = [" + error + "]");
                    ringProgressDialog.dismiss();
                    Toast.makeText(getApplication(),
                            "Please check your internet connection", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        if (v == btn) {
            finish();

        } else if (btn_changePhoto == v) {
            Log.d("TAG", "Click image");
            v.showContextMenu();
        } else if (btn_createGroup == v) {

            //ถ้า file  ถูก upload เเล้ว
            if (urlImage != null) {
                if (isUpdate) {
                    updateChatroom(urlImage);
                } else {
                    createChatroom(urlImage);
                }

            } else {
                if (fileImage != null) {
                    String mimeType = URLConnection.guessContentTypeFromName(fileImage.getName());
                    adapterRestUpload.uploadImage(new TypedFile(mimeType, fileImage), new Callback<Response>() {
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

                            urlImage = fileListObject.getData().get(0).getUrl();
                            setImageUrlToView(urlImage);

                            if (isUpdate) {
                                updateChatroom(urlImage);
                            } else {
                                createChatroom(urlImage);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            System.out.println("error = [" + error + "]");
                            Toast.makeText(getApplication(),
                                    "Please check your internet connection", Toast.LENGTH_SHORT)
                                    .show();

                        }
                    });

                } else {
                    if (isUpdate) {
                        updateChatroom("");
                    } else {
                        createChatroom("");
                    }
                }
            }


            //finish();
        } else if (btn_invite == v) {
            Intent intent = new Intent(getApplicationContext(), AddFriendActivity.class);
            intent.putExtra("group", true);
            intent.putExtra("member", memberlist);
            intent.putExtra("outcast", outcastlist);
            startActivityForResult(intent, TAG_ADD_FRIEND_ACTIVITY);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAG_ADD_FRIEND_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                System.out.println("daataataat");
                ArrayList<InviteFriendObject> mData = data.getParcelableArrayListExtra("data");
                adapterListCreateGroup.resetAdapter(mData);

            }

        }


        if (requestCode == cameraMange.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                try {
                    fileImage = new File(cameraMange.fileUri.getPath());
                    Log.d("TAG", "CAmera Capture path1" + fileImage.getPath());
                    if (fileImage.exists()) {
                        Log.d("TAG", "CAmera Capture path2" + fileImage.getPath());
                        setImageFileToView(fileImage);
                        getImagePathUpload(fileImage);


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled Image capture
                Log.e("image selected", "cancel");
            } else {
                // failed to capture image
                Log.e("image selected", "fail");
            }
        }


        /** Check Image From Gallery*/
        if (requestCode == MasterData.PICK_IMAGE_MULTIPLE) {
            if (resultCode == Activity.RESULT_OK) {
                String[] imagesPath = data.getStringExtra("data").split("\\|");
                fileImage = new File(imagesPath[0]);
                if (fileImage.exists()) {
                    setImageFileToView(fileImage);
                    getImagePathUpload(fileImage);


                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled Image capture
                Log.e("image selected", "cancel");
            } else {
                // failed to capture image
                Log.e("image selected", "fail");
            }
        }


    }


    void getImagePathUpload(File file) {
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        System.out.println("file = [" + mimeType + "]");
        adapterRestUpload.uploadImage(new TypedFile(mimeType, file), new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {

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

                FileListObject fileListObject = new Gson().fromJson(JsonConvertData, FileListObject.class);

                urlImage = fileListObject.getData().get(0).getUrl();
                setImageUrlToView(urlImage);


            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");

            }
        });
    }

    void setImageFileToView(File f) {
        Glide.with(this).load(f)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .override(100, 100)
                .into(image);
    }

    void setImageUrlToView(String url) {
        String mUrl = (url.isEmpty()) ? "No Image" : url;
        Glide.with(this).load(mUrl)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .override(100, 100)
                .into(image);


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


}
