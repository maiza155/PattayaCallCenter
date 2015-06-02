package com.pattaya.pattayacallcenter.member;

import android.content.Context;
import android.content.SharedPreferences;
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
public class TaskGetFriend {
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
        getRosterFromServer();
        getGroupFromServer();
    }

    void getGroupFromServer() {
        checkGroup = false;
        openfireQueary.getChatRoom(new Callback<ChatRooms>() {
            @Override
            public void success(final ChatRooms chatRooms, Response response) {
                Log.e("countInviteGroup", "start");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final ArrayList<Users> arrUser = new ArrayList<>();
                        countInviteGroup = 0;
                        if (chatRooms.getChatRoom() != null) {
                            for (ChatRoom e : chatRooms.getChatRoom()) {
                                final String room = e.getRoomName() + "@conference.pattaya-data";

////                        for (Owners owners : e.getOwners()) {
////                            if (!owners.getOwner().matches(jid)) {
////                                final Users mUser = new Users(room, e.getNaturalName(), null, Users.TYPE_GROUP);
////                                mUser.setPic(e.getDescription());
////                                arrUser.add(mUser);
////                                DatabaseChatHelper.init().addUsers(mUser);
////                            }
////                        }
                                if (e.getOutcasts().getListMember() != null) {
                                    for (Member member : e.getOutcasts().getListMember()) {
                                        if (member.getText().matches(jid)) {
                                            countInviteGroup++;
                                            Log.e("countInviteGroup", "" + countInviteGroup);
                                            childListGroup.add(new InviteFriendObject(room, e.getNaturalName(), e.getDescription()));
                                            final Users mUser = new Users(room, e.getNaturalName(), null, Users.TYPE_INVITE_GROUP);
                                            mUser.setPic(e.getDescription());
                                            DatabaseChatHelper.init().addUsers(mUser);
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
                }).start();


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

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final ArrayList<String> tempArr = new ArrayList<>();
                        //DatabaseChatHelper.init().clearUserTable();
                        if (roster.getRosterItem() != null) {
                            countInviteFriend = 0;
                            for (final RosterItem e : roster.getRosterItem()) {

                                // มี Group ใน Rooster
                                if (e.getGroups().getGroup() != null) {
                                    //Friend
                                    if (e.getGroups().getGroup().size() == 1) {
                                        final Users mUser = new Users(e.getJid(), e.getNickname(), null, Users.TYPE_FRIEND);
                                        User user = openfireQueary.getUserInTask(e.getJid().split("@")[0]);
                                        if (user != null) {
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
                                    }
                                    // Friend & Favorite
                                    else if (e.getGroups().getGroup().size() == 2) {

                                        final Users mUser = new Users(e.getJid(), e.getNickname(), null, Users.TYPE_FRIEND);
                                        mUser.setFavorite(true);
                                        User user = openfireQueary.getUserInTask(e.getJid().split("@")[0]);
                                        if (user != null) {
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


                                    }

                                }// ไม่มี group
                                else {
                                    System.out.println(jid + "///////No Group " + e.getJid());
                                    final Users mUser = new Users(e.getJid(), e.getNickname(), null, Users.TYPE_NOT_FRIEND);
                                    User user = openfireQueary.getUserInTask(e.getJid().split("@")[0]);
                                    if (user != null) {
                                        mUser.setName(user.getName());
                                        mUser.setPic(user.getProperty().get("userImage"));
                                        if (!e.getJid().matches(jid)) {
                                            DatabaseChatHelper.init().addUsers(mUser);
                                            countInviteFriend++;
                                        }

                                        tempArr.add(mUser.getJid());

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
                }).start();

            }

            @Override
            public void failure(RetrofitError error) {
                BusProvider.getInstance().post(new BusGetFriendObject());
            }
        });


    }

}
