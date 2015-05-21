package com.pattaya.pattayacallcenter.member;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.Data.Users;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.chat.XMPPManage;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom.ChatRoom;
import com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom.Member;
import com.pattaya.pattayacallcenter.chat.xmlobject.User;
import com.pattaya.pattayacallcenter.member.Adapter.AdapterListViewAddGroupData;
import com.pattaya.pattayacallcenter.member.data.InviteFriendObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddGroupActivity extends ActionBarActivity implements View.OnClickListener {

    final String TAG = "AddGroupActivity";
    //Openfire
    final RestAdapter restAdapterOpenFire = RestAdapterOpenFire.getInstance();
    final OpenfireQueary openfireQueary = restAdapterOpenFire.create(OpenfireQueary.class);
    final RestAdapter restAdapterOpenFireJson = RestAdapterOpenFire.getInstanceJson();
    final OpenfireQueary openfireQuearyJson = restAdapterOpenFireJson.create(OpenfireQueary.class);
    List<String> userList = new ArrayList<>();
    ArrayList memberlist = new ArrayList();
    ArrayList outcastlist = new ArrayList();
    Intent intent;
    // widget
    ImageButton btn;
    TextView titleTextView;
    Users groupdata;
    SharedPreferences sp;
    String jid;
    ProgressDialog progressDialog;
    int TAG_ADD_FRIEND_ACTIVITY = 807;
    int TAG_ADD_EDIT_ACTIVITY = 809;
    private AdapterListViewAddGroupData adapterListViewAddGroupData; //Adapter List ที่เรากำหนดขึ้นเอง
    private ArrayList<InviteFriendObject> listDataAddGroup = new ArrayList(); //list ในการเก็บข้อมูลของ DataShow
    private ListView listViewDataAddGroup;
    private Button btn_leave;
    private Button btn_chat;
    private Button btn_invite;
    private TextView tv_empty;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_group);
        Bundle data = getIntent().getExtras();
        groupdata = data.getParcelable("user");


        sp = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        jid = sp.getString(MasterData.SHARED_USER_JID, null);
        //orgId = "3";


        tv_empty = (TextView) findViewById(R.id.empty);
        btn_chat = (Button) findViewById(R.id.btn_chat);
        btn_invite = (Button) findViewById(R.id.btn_invite);
        btn_leave = (Button) findViewById(R.id.btn_leave);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ClickButton();
        SetActionBar();
        listViewDataAddGroup = (ListView) findViewById(R.id.listview_addgroup);
        adapterListViewAddGroupData = new AdapterListViewAddGroupData(getBaseContext(), listDataAddGroup);
        listViewDataAddGroup.setAdapter(adapterListViewAddGroupData);
        listViewDataAddGroup.setEmptyView(tv_empty);
        tv_empty.setOnClickListener(this);
        getUserInRooom();
        setClickDelete();

    }


    void setClickDelete() {
        adapterListViewAddGroupData.SetOnItemClickListener(new AdapterListViewAddGroupData.OnClickListener() {
            @Override
            public void itemClick(String jid, final int position) {
                openfireQuearyJson.deleteUserChatRoom(groupdata.getJid().split("@")[0], "members", jid, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Toast.makeText(getApplication(),
                                "success", Toast.LENGTH_SHORT)
                                .show();
                        adapterListViewAddGroupData.removeAt(position);
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
        });
    }


    private void SetActionBar() {

        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        titleTextView.setPadding(40, 0, 0, 0);
        titleTextView.setText(groupdata.getName());
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        //setClickLisener();

        btn.setOnClickListener(this);


    }

    public void ClickButton() {
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("jid", groupdata.getJid());
                startActivity(intent);
            }
        });
        btn_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddGroupActivity.this)
                        .setTitle("ยืนยันการออกจากกลุ่ม")
                        .setMessage("ยืนยันการออกจากกลุ่ม")
                        .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                leaveChatroom();
                            }
                        })
                        .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing

                            }
                        })
                        .show();

            }
        });
        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddFriendActivity.class);
                intent.putExtra("group", true);
                intent.putExtra("member", memberlist);
                intent.putExtra("outcast", outcastlist);
                startActivityForResult(intent, TAG_ADD_FRIEND_ACTIVITY);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_group, menu);
        return super.onCreateOptionsMenu(menu);

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
        } else if (id == R.id.action_editGroup) {
            intent = new Intent(getApplicationContext(), CreateGroupActivity.class);
            intent.putExtra("update", true);
            String[] data = {groupdata.getName(), groupdata.getJid(), groupdata.getPic()};
            intent.putExtra("data", data);

            startActivityForResult(intent, TAG_ADD_EDIT_ACTIVITY);
            return true;
        } else if (id == R.id.action_deleteGroup) {
            new AlertDialog.Builder(AddGroupActivity.this)
                    .setTitle("ยืนยันการลบกลุ่ม")
                    .setMessage("ยืนยันการลบกลุ่ม")
                    .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            deleteChatroom();
                        }
                    })
                    .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing

                        }
                    })
                    .show();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void getUserInRooom() {
        tv_empty.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        memberlist = new ArrayList();
        outcastlist = new ArrayList();
        openfireQueary.getChatRoomDetail(groupdata.getJid().split("@")[0], new Callback<ChatRoom>() {
            @Override
            public void success(final ChatRoom chatRoom, Response response) {
                Log.d(TAG, jid);
                final boolean[] fin = {false, false};

                if (chatRoom.getOutcasts().getListMember() != null) {
                    for (final Member e : chatRoom.getOutcasts().getListMember()) {
                        outcastlist.add(e.getText());
                        System.out.println("outcast>>>" + e.getText());
                    }
                }

                for (final Member e : chatRoom.getMembers().getListMember()) {
                    memberlist.add(e.getText());
                    if (!e.getText().matches(jid)) {
                        System.out.println(e.getText());
                        userList.add(e.getText());
                        final String mUserJid = e.getText();
                        openfireQueary.getUser(e.getText().split("@")[0], new Callback<User>() {
                            @Override
                            public void success(User user, Response response) {

                                listDataAddGroup.add(new InviteFriendObject(mUserJid, user.getName(), user.getProperty().get("userImage")));
                                progressBar.setVisibility(View.GONE);
                                adapterListViewAddGroupData.resetAdapter(listDataAddGroup);


                            }

                            @Override
                            public void failure(RetrofitError error) {
                                System.out.println("error = [" + error + "]");

                                if (listDataAddGroup.size() == 0) {
                                    tv_empty.setVisibility(View.VISIBLE);
                                }

                                progressBar.setVisibility(View.GONE);


                            }
                        });

                    } else {
                        if (listDataAddGroup.size() == 0) {
                            tv_empty.setVisibility(View.VISIBLE);
                            tv_empty.setText("No Result");
                        }

                        progressBar.setVisibility(View.GONE);

                    }

                }


            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error = [" + error + "]");
                if (listDataAddGroup.size() == 0) {
                    tv_empty.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);

            }
        });


    }

    void leaveChatroom() {
        openfireQuearyJson.deleteUserChatRoom(groupdata.getJid().split("@")[0], "members", jid, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                System.out.println("response = [" + response + "], response2 = [" + response2 + "]");
                BusProvider.getInstance().post("add_roster_complete");
                Toast.makeText(getApplication(),
                        "success", Toast.LENGTH_SHORT)
                        .show();
                DatabaseChatHelper.init().deleteLastMessage(groupdata.getJid());
                BusProvider.getInstance().post("update_last_message");
                finish();


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

    void deleteChatroom() {
        openfireQuearyJson.deleteChatRoom(groupdata.getJid().split("@")[0], new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                BusProvider.getInstance().post("add_roster_complete");
                Toast.makeText(getApplication(),
                        "success", Toast.LENGTH_SHORT)
                        .show();
                DatabaseChatHelper.init().deleteLastMessage(groupdata.getJid());
                BusProvider.getInstance().post("update_last_message");
                finish();

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplication(),
                        "Please check your internet connection", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v == btn) {
            finish();
        } else if (v == tv_empty) {
            getUserInRooom();

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAG_ADD_FRIEND_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                final ArrayList<InviteFriendObject> mData = data.getParcelableArrayListExtra("data");
                final ArrayList<String> arrJid = new ArrayList<>();
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = ProgressDialog.show(AddGroupActivity.this, null, getResources().getString(R.string.please_wait), true);
                        progressDialog.setCancelable(true);

                    }

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        for (InviteFriendObject e : mData) {
                            if (!userList.contains(e.getJid())) {
                                arrJid.add(e.getJid());
                                openfireQuearyJson.updateChatRoom(groupdata.getJid().split("@")[0], "outcasts", e.getJid(), new Callback<Response>() {
                                    @Override
                                    public void success(Response response, Response response2) {
                                        System.out.println("response = [" + response + "], response2 = [" + response2 + "]");
                                        Toast.makeText(getApplication(),
                                                "send invite success", Toast.LENGTH_SHORT)
                                                .show();

                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        System.out.println("error = [" + error + "]");
                                    }
                                });
                            }
                        }

                        XMPPManage.getInstance().sendInviteJoinRoom(
                                groupdata.getJid()
                                , arrJid);
                        return null;
                    }


                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        progressDialog.dismiss();


                    }
                }.execute();




            }

        }


        if (requestCode == TAG_ADD_EDIT_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra("name");
                String image = data.getStringExtra("image");
                groupdata.setPic(image);
                titleTextView.setText(name);

            }

        }
    }


}
