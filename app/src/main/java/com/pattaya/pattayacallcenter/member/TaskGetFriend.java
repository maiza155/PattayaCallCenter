package com.pattaya.pattayacallcenter.member;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.Data.Users;
import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom.ChatRoom;
import com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom.ChatRooms;
import com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom.Member;
import com.pattaya.pattayacallcenter.chat.xmlobject.Roster.Roster;
import com.pattaya.pattayacallcenter.chat.xmlobject.Roster.RosterItem;
import com.pattaya.pattayacallcenter.chat.xmlobject.User;
import com.pattaya.pattayacallcenter.member.data.BusGetFriendObject;
import com.pattaya.pattayacallcenter.member.data.InviteFriendObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by SWF on 4/28/2015.
 */
public class TaskGetFriend extends AsyncTask<Void, Void, Boolean> {
    final RestAdapter restAdapterOpenFire = RestAdapterOpenFire.getInstance();
    final OpenfireQueary openfireQueary = restAdapterOpenFire.create(OpenfireQueary.class);
    Boolean checkGroup = true;
    Boolean checkUser = true;
    int countInviteGroup = 0;
    int countInviteFriend = 0;
    String jid;
    Context context;

    List<InviteFriendObject> childListGroup;
    BusGetFriendObject busGetFriendObject = new BusGetFriendObject();

    public TaskGetFriend(Context context) {
        this.context = context;
        SharedPreferences sp = context.getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        jid = sp.getString(MasterData.SHARED_USER_JID, null);
        childListGroup = new ArrayList<>();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        getRosterFromServer();
        return null;
    }


    void getGroupFromServer() {
        checkGroup = false;
        openfireQueary.getChatRoom(new Callback<ChatRooms>() {
            @Override
            public void success(ChatRooms chatRooms, Response response) {
                final ArrayList<Users> arrUser = new ArrayList<>();
                countInviteGroup = 0;
                if (chatRooms.getChatRoom() != null) {
                    for (ChatRoom e : chatRooms.getChatRoom()) {
                        final String room = e.getRoomName() + "@conference.pattaya-data";
                        /*
                        for (Owners owners : e.getOwners()) {
                            if (!owners.getOwner().matches(jid)) {
                                final Users mUser = new Users(room, e.getNaturalName(), null, Users.TYPE_GROUP);
                                mUser.setPic(e.getDescription());
                                arrUser.add(mUser);
                                DatabaseChatHelper.init().addUsers(mUser);
                            }
                        }*/
                        if (e.getOutcasts().getListMember() != null) {
                            for (Member member : e.getOutcasts().getListMember()) {
                                if (member.getText().matches(jid)) {
                                    countInviteGroup++;
                                    Log.e("countInviteGroup", "" + countInviteGroup);
                                    childListGroup.add(new InviteFriendObject(room, e.getNaturalName(), e.getDescription()));


                                }
                            }
                        }

                        if (e.getMembers().getListMember() != null) {
                            for (Member member : e.getMembers().getListMember()) {
                                if (member.getText().matches(jid)) {
                                    final Users mUser = new Users(room, e.getNaturalName(), null, Users.TYPE_GROUP);
                                    mUser.setPic(e.getDescription());
                                    arrUser.add(mUser);
                                    DatabaseChatHelper.init().addUsers(mUser);
                                }

                            }

                        }


                    }
                    checkGroup = true;
                    if (checkGroup && checkUser) {
                        //new queryTask().execute();

                        busGetFriendObject.setListGroupData(childListGroup);
                        busGetFriendObject.setCount(countInviteFriend + countInviteGroup);
                        BusProvider.getInstance().post(busGetFriendObject);
                        BusProvider.getInstance().post("update_last_message");
                    }


                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }


    void getRosterFromServer() {
        checkUser = false;
        openfireQueary.getRoster(jid.split("@")[0], new Callback<Roster>() {
            @Override
            public void success(final Roster roster, retrofit.client.Response response) {
                final ArrayList<String> tempArr = new ArrayList<>();
                DatabaseChatHelper.init().clearUserTable();
                getGroupFromServer();

                if (roster.getRosterItem() != null) {
                    countInviteFriend = 0;
                    for (final RosterItem e : roster.getRosterItem()) {

                        // มี Group ใน Rooster
                        if (e.getGroups().getGroup() != null) {
                            //Friend
                            if (e.getGroups().getGroup().size() == 1) {

                                final Users mUser = new Users(e.getJid(), e.getNickname(), null, Users.TYPE_FRIEND);
                                openfireQueary.getUser(e.getJid().split("@")[0], new Callback<User>() {
                                    @Override
                                    public void success(User user, retrofit.client.Response response) {
                                        mUser.setName(user.getName());
                                        mUser.setPic(user.getProperty().get("userImage"));
                                        DatabaseChatHelper.init().addUsers(mUser);
                                        tempArr.add(mUser.getJid());

                                        System.out.println(countInviteFriend);
                                        if (tempArr.size() == roster.getRosterItem().size()) {
                                            checkUser = true;
                                            if (checkUser && checkGroup) {
                                                //new queryTask().execute();
                                                busGetFriendObject.setCount(countInviteFriend + countInviteGroup);
                                                BusProvider.getInstance().post(busGetFriendObject);
                                                BusProvider.getInstance().post("update_last_message");
                                            }
                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {

                                    }
                                });


                            }
                            // Friend & Favorite
                            else if (e.getGroups().getGroup().size() == 2) {

                                final Users mUser = new Users(e.getJid(), e.getNickname(), null, Users.TYPE_FRIEND);
                                mUser.setFavorite(true);
                                openfireQueary.getUser(e.getJid().split("@")[0], new Callback<User>() {
                                    @Override
                                    public void success(User user, retrofit.client.Response response) {
                                        mUser.setName(user.getName());
                                        mUser.setPic(user.getProperty().get("userImage"));
                                        DatabaseChatHelper.init().addUsers(mUser);
                                        tempArr.add(mUser.getJid());
                                        System.out.println("F" + countInviteFriend);

                                        if (tempArr.size() == roster.getRosterItem().size()) {
                                            checkUser = true;
                                            if (checkUser && checkGroup) {
                                                // new queryTask().execute();
                                                busGetFriendObject.setCount(countInviteFriend + countInviteGroup);
                                                BusProvider.getInstance().post(busGetFriendObject);
                                                BusProvider.getInstance().post("update_last_message");
                                            }
                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        //System.out.println("error = [" + error + "]");
                                    }
                                });


                            }

                        }// ไม่มี group
                        else {
                            //System.out.println("///////No Group " + e.getJid());

                            final Users mUser = new Users(e.getJid(), e.getNickname(), null, Users.TYPE_NOT_FRIEND);
                            openfireQueary.getUser(e.getJid().split("@")[0], new Callback<User>() {
                                @Override
                                public void success(User user, retrofit.client.Response response) {
                                    mUser.setName(user.getName());
                                    mUser.setPic(user.getProperty().get("userImage"));
                                    DatabaseChatHelper.init().addUsers(mUser);
                                    tempArr.add(mUser.getJid());
                                    countInviteFriend++;
                                    if (tempArr.size() == roster.getRosterItem().size()) {
                                        checkUser = true;
                                        if (checkUser && checkGroup) {
                                            //new queryTask().execute();
                                            busGetFriendObject.setCount(countInviteFriend + countInviteGroup);
                                            BusProvider.getInstance().post(busGetFriendObject);
                                            BusProvider.getInstance().post("update_last_message");
                                        }
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {

                                }
                            });


                        }
                    }
                } else {
                    //System.out.println("///////No Roster ");
                    checkUser = true;
                    if (checkUser && checkGroup) {
                        //new queryTask().execute();
                        busGetFriendObject.setCount(countInviteFriend + countInviteGroup);
                        BusProvider.getInstance().post(busGetFriendObject);
                        BusProvider.getInstance().post("update_last_message");
                    }
                }

            }

            @Override
            public void failure(RetrofitError error) {
                BusProvider.getInstance().post(new BusGetFriendObject());
            }
        });


    }

}
