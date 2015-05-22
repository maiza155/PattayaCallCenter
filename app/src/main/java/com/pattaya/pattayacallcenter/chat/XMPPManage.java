package com.pattaya.pattayacallcenter.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.mobprofs.retrofit.converters.SimpleXmlConverter;
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.Data.Messages;
import com.pattaya.pattayacallcenter.Data.Users;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.chat.xmlobject.User;
import com.pattaya.pattayacallcenter.member.TaskGetFriend;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.delay.provider.DelayInformationProvider;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.EventElement;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.ItemsExtension;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.SubscribeForm;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by SWF on 2/17/2015.
 */
public class XMPPManage implements MessageListener {
    public static final String HOST = "58.181.163.115";
    //public static final String HOST = "172.16.1.128";
    public static final int PORT = 5222;
    public static final String PASSWORD = "1234";
    static final RestAdapter restAdapterOpenFire = RestAdapterOpenFire.getInstance();
    static final OpenfireQueary openfireQueary = restAdapterOpenFire.create(OpenfireQueary.class);
    public static String SERVICE = "pattaya-data";
    public static String USERNAME = " ";
    public static XMPPManage xmppManage = null;
    static Chat chat = null;
    static Chat mChat = null;
    static ConnectionConfiguration config;
    static XMPPTCPConnection mConnection;
    static ChatHistoryData chatHistoryData = ChatHistoryData.getInstance();
    static DatabaseChatHelper databaseChatHelper = DatabaseChatHelper.init();
    static SimpleDateFormat sdf = new SimpleDateFormat("E dd MMM yyyy-HH:mm:ss ");
    static SharedPreferences spConfig;
    static SharedPreferences sp;
    static Boolean alert;
    static String jid;
    static PubSubManager manager;
    static XMPPTCPConnection admin;
    static Boolean isOfficial;
    final String GROUP_FRIEND = "Friends";
    final String GROUP_FAVORITE = "Favorite";

    ReconnectionManager reconnectionManager;

    public static XMPPManage getInstance() {
        if (xmppManage == null) {
            Log.e("XMPPManage", "CreatE nEW oBJECT");
            init();
            xmppManage = new XMPPManage();

        }
        return xmppManage;
    }

    private static void init() {
        sp = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        jid = sp.getString(MasterData.SHARED_USER_JID, null);
        isOfficial = sp.getBoolean(MasterData.SHARED_IS_OFFICIAL, false);

        USERNAME = jid.split("@")[0];
        SERVICE = jid.split("@")[1];
        spConfig = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);

        Log.e("XMPPManage", jid);


    }

    public static Chat getChat() {
        return chat;
    }

    public static void setChat(Chat chat) {
        XMPPManage.chat = chat;
    }

    static void login() {
        try {
            mConnection.connect();
            ProviderManager.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());
            int randomNum = 500 + (int) (Math.random() * 2000);
            mConnection.login(USERNAME, PASSWORD, "Android" + randomNum);
            Log.e("XMPPManage", "Connection is : " + mConnection.isConnected());
            Log.e("XMPPManage", "Connected to " + mConnection.getHost());


        } catch (SmackException e) {
            // Log.e("XMPPManage", "" + e);
            // e.printStackTrace();
        } catch (IOException e) {
            // Log.e("XMPPManage", "" + e);
            // e.printStackTrace();
        } catch (XMPPException e) {
            // Log.e("XMPPManage", "" + e);
            // e.printStackTrace();
        }
    }

    static void messageReceiver() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                mConnection.addConnectionListener(new ConnectionListener() {
                    @Override
                    public void reconnectionSuccessful() {
                        Log.e("xmppadapter", "Successfully reconnected to the XMPP server. >>" + mConnection.isConnected());
                        Application.getContext().startService(new Intent(Application.getContext(), XMPPService.class));
                        new TaskGetFriend(Application.getContext()).execute();
                    }

                    @Override
                    public void reconnectionFailed(Exception arg0) {
                        Log.e("xmppadapter", "Failed to reconnect to the XMPP server.>>" + mConnection.isConnected());
                    }

                    @Override
                    public void reconnectingIn(int seconds) {
                        Log.i("xmppadapter", "Reconnecting in " + seconds + " seconds.>>" + mConnection.isConnected());
                    }

                    @Override
                    public void connectionClosedOnError(Exception arg0) {
                        Log.i("xmppadapter", "Connection to XMPP server was lost.>>" + mConnection.isConnected());
                    }

                    @Override
                    public void connected(XMPPConnection xmppConnection) {
                        Log.i("xmppadapter", "XMPP connection was connected.>>" + mConnection.isConnected());
                    }

                    @Override
                    public void authenticated(XMPPConnection xmppConnection) {
                        Log.i("xmppadapter", "XMPP connection was authenticated.>>" + mConnection.isConnected());
                    }

                    @Override
                    public void connectionClosed() {
                        Log.i("xmppadapter", "XMPP connection was closed.>>" + mConnection.isConnected());

                    }
                });


                PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
                mConnection.addPacketListener(new PacketListener() {
                    public void processPacket(Packet packet) {
                        Log.e("XMPPManage", "Packet Reciver >>" + packet);
                        DelayInformation inf = null;
                        try {
                            inf = packet.getExtension("x", "jabber:x:delay");
                        } catch (Exception e) {
                            Log.e("XMPPManage Error", "" + e);
                        }
                        // get offline message timestamp
                        if (inf != null) {
                            Date date = inf.getStamp();
                            Log.e("XMPPManage", "Time Stamp" + date);

                        }
                        final Message message = (Message) packet;
                        String from = message.getFrom().split("/")[0];
                        Log.e("XMPPManage", "////////////// User  Chat //////////////////////////");
                        Log.e("XMPPManage", "From :: " + " >> " + from);
                        Log.e("XMPPManage", "Received message :: " + " >> " + message.getBody());
                        Log.d("XMPPManage", "XML Message  " + " >> " + message);

                        if (!from.equalsIgnoreCase(mConnection.getUser().split("/")[0])
                                && DatabaseChatHelper.init().getOneUsers(from) != null) {
                            Calendar c = Calendar.getInstance();
                            System.out.println(sdf.format(c.getTime())); //2014/08/06 16:00:22
                            final Messages messages = new Messages();
                            messages.setMessage(message.getBody());
                            messages.setRoom(from);
                            messages.setSender(from);
                            messages.setTime(sdf.format(c.getTime()));
                            //BusProvider.getInstance().post(messages);
                            getUserData(messages);


                        } else if (from.equalsIgnoreCase(xmppManage.getmConnection().getUser().split("/")[0])) {
                            String room = message.getSubject();
                            System.out.println("XMPPManage  " + "room = [" + room + "]");
                            if (room != null) {
                                Calendar c = Calendar.getInstance();
                                System.out.println(sdf.format(c.getTime())); //2014/08/06 16:00:22
                                Messages messages = new Messages();
                                messages.setMessage(message.getBody());
                                messages.setRoom(room);
                                messages.setSender(from);
                                messages.setTime(sdf.format(c.getTime()));
                                // DataChat mData = new DataChat(otherUser, messages);
                                databaseChatHelper.addLogs(messages);
                                BusProvider.getInstance().post(messages);
                            }

                        }


                    }
                }, filter);
                PacketFilter filterMulti = new MessageTypeFilter(Message.Type.groupchat);
                mConnection.addPacketListener(new PacketListener() {
                    public void processPacket(Packet packet) {
                        Message message = (Message) packet;
                        DelayInformation inf = null;
                        try {
                            inf = packet.getExtension("x", "jabber:x:delay");
                        } catch (Exception e) {
                            Log.e("XMPPManage Error", "" + e);
                        }
                        //Log.e("TAG DELAY TIME", "" + inf);
                        if (inf == null) {
                            String[] arry = message.getFrom().split("/");
                            String room = arry[0];
                            String from = arry[1] + "@" + SERVICE;
                            Log.e("XMPPManage", "////////////// MultiUser  Chat //////////////////////////");
                            Log.e("XMPPManage", "From :: " + " >> " + from);
                            Log.e("XMPPManage", "room :: " + " >> " + room);
                            Log.e("XMPPManage", "Received message :: " + " >> " + message.getBody());
                            Log.d("XMPPManage", "XML Message :: " + " >> " + message);
                            Calendar c = Calendar.getInstance();
                            System.out.println(sdf.format(c.getTime())); //2014/08/06 16:00:22
                            Messages messages = new Messages();
                            messages.setMessage(message.getBody());
                            messages.setRoom(room);
                            messages.setSender(from);
                            messages.setTime(sdf.format(c.getTime()));
                            String checkcase = room.split("@")[0];
                            checkcase = checkcase.split("-")[0];
                            Log.e("IsOfficial is flase >>>", "    " + checkcase);
                            isOfficial = sp.getBoolean(MasterData.SHARED_IS_OFFICIAL, false);
                            if (checkcase.matches("case") && isOfficial) {
                                Log.e("IsOfficial is true >>>", "    JA");

                            } else {
                                Log.e("IsOfficial is flase >>>", "    JA");
                                getUserData(messages);
                            }


                        }


                    }
                }, filterMulti);

                PacketFilter filter2 = new MessageTypeFilter(Message.Type.normal);

                mConnection.addPacketListener(new PacketListener() {
                    public void processPacket(Packet packet) {
                        Log.e("Tag, ", "////////////// notify messages //////////////////////////");
                        PubsubObject pubsubObject = new PubsubObject();

                        Message message = (Message) packet;
                        SimpleXmlConverter xmlphase = new SimpleXmlConverter();

                        EventElement event = packet.getExtension("event", PubSubNamespace.EVENT.getXmlns());
                        if (event != null) {
                            ItemsExtension itemsElem = (ItemsExtension) event.getEvent();
                            List<? extends PacketExtension> pubItems = itemsElem.getItems();
                            PayloadItem itemPubsub = (PayloadItem) pubItems.get(0);

                            DataForm dataForm = (DataForm) itemPubsub.getPayload();
                            for (FormField e : dataForm.getFields()) {
                                if (e.getVariable().matches("ownerImage")) {
                                    pubsubObject.setImage(e.getValues().get(0));
                                } else if (e.getVariable().matches("ownerName")) {
                                    pubsubObject.setName(e.getValues().get(0));
                                } else if (e.getVariable().matches("title")) {
                                    pubsubObject.setTitle(e.getValues().get(0));
                                } else if (e.getVariable().matches("displayDate")) {
                                    pubsubObject.setDisplayData(String.valueOf(System.currentTimeMillis()));
                                } else if (e.getVariable().matches("action")) {
                                    pubsubObject.setAction(e.getValues().get(0));
                                } else if (e.getVariable().matches("primaryKey")) {
                                    pubsubObject.setPrimarykey(Integer.parseInt(e.getValues().get(0)));
                                } else if (e.getVariable().matches("caseId")) {
                                    pubsubObject.setCaseId(Integer.parseInt(e.getValues().get(0)));
                                } else if (e.getVariable().matches("complainId")) {
                                    pubsubObject.setComplainId(Integer.parseInt(e.getValues().get(0)));
                                }
                            }
                            if (pubsubObject.getAction().matches("org")) {
                                Log.e("TAg", "INVITE TO oRG");
                                BusProvider.getInstance().post("org_update");
                            } else {
                                NotifyCase.setNotifyChat(pubsubObject);
                            }


                        }


                    }
                }, filter2);
                PacketFilter filter3 = new MessageTypeFilter(Message.Type.headline);

                mConnection.addPacketListener(new PacketListener() {
                    public void processPacket(Packet packet) {
                        Message message = (Message) packet;
                        Log.e("Tag, ", "////////////// Other2 //////////////////////////");
                        Log.e("From :: ", " >> " + message.getFrom());
                        Log.e("Received message :: ", " >> " + message.getBody());
                        Log.d("XML Message :: ", " >> " + message);


                    }
                }, filter3);


                PacketFilter filter4 = new MessageTypeFilter(Message.Type.headline);

                mConnection.addPacketListener(new PacketListener() {
                    public void processPacket(Packet packet) {
                        Message message = (Message) packet;
                        Log.e("Tag, ", "////////////// Other3 //////////////////////////");
                        Log.e("From :: ", " >> " + message.getFrom());
                        Log.e("Received message :: ", " >> " + message.getBody());
                        Log.d("XML Message :: ", " >> " + message);


                    }
                }, filter4);


                mConnection.addPacketListener(new PacketListener() {
                    @Override
                    public void processPacket(Packet packet) throws SmackException.NotConnectedException {
                        // เช็ค PackageID จากที่  ChatHistoryData.class โดยดึงจากที่เราเรียกจาก Server ผ่าน ListDateHistory.class


                        String room = chatHistoryData.getMap().get(packet.getPacketID());

                        if (room != null) {
                            // Log.e("TAG -Jingle+++++++ >>>", "" + packet.getPacketID());
                            //Log.e("TAG -Jingle+++++++ >>>", "" + room);
                            //เช็ค packet สุดท้ายที่ถูกส่งออกไป
                            String lastPacket = chatHistoryData.getLastMaps().get(room);
                            // Log.e("TAG -Jingle+++++++ >>>", "" + lastPacket);
                            if (packet.getPacketID().equalsIgnoreCase(lastPacket)) {
                                Log.e("XMPPManage", "Get Last Room Packet" + "" + lastPacket);
                                databaseChatHelper.UpdateChatLogs(room, chatHistoryData.getRoom().get(room));
                            }
                            //ลบ key value ออกจาก map เนื่องจากได้รับการตอบกลับเเล้ว
                            chatHistoryData.getMap().remove(packet.getPacketID());
                        } else {
                            Log.d("TAG SET >>> REsult", "" + packet);

                            List<Users> arrUsers = DatabaseChatHelper.init().getUsers();
                   /* for (Users e : arrUsers) {
                        if (e.getType() == Users.TYPE_GROUP) {
                            setJoinRoom(e.getJid());
                        }

                    }*/

                        }

                    }
                }, new IQTypeFilter(IQ.Type.RESULT));


                mConnection.addPacketListener(new PacketListener() {
                    @Override
                    public void processPacket(Packet packet) throws SmackException.NotConnectedException {
                        Log.d("TAG Error >>>", "" + packet);


                    }
                }, new IQTypeFilter(IQ.Type.ERROR));


                mConnection.addPacketListener(new PacketListener() {
                    @Override
                    public void processPacket(Packet packet) throws SmackException.NotConnectedException {

                        new TaskGetFriend(Application.getContext()).execute();
                        Log.d("TAG SET >>>", "" + packet);

                    }
                }, new IQTypeFilter(IQ.Type.SET));


                mConnection.addPacketListener(new PacketListener() {
                    @Override
                    public void processPacket(Packet packet) throws SmackException.NotConnectedException {
                        Log.d("TAG SET >>>", "" + packet);


                    }
                }, new IQTypeFilter(IQ.Type.GET));


                //Ping();
                ProviderManager.addIQProvider("list", "urn:xmpp:archive", new ListDateHistoryChat());
                ProviderManager.addIQProvider("chat", "urn:xmpp:archive", new ListHistoryChat());
                ProviderManager.addIQProvider("jingle", "urn:xmpp:jingle:1", new ListHistoryChat());
                return null;
            }
        }.execute();


    }

    static void getUserData(final Messages messages) {
        new Thread() {
            @Override
            public void run() {
                final Users users = databaseChatHelper.getOneUsers(messages.getSender());
                if (users != null) {
                    messages.setSenderImage(users.getPic());
                    messages.setSenderName(users.getName());
                    databaseChatHelper.addLogs(messages);
                    BusProvider.getInstance().post(messages);
                    alert = spConfig.getBoolean(MasterData.SHARED_CONFIG_ALERT, true);
                    if (!messages.getSender().equalsIgnoreCase(mConnection.getUser().split("/")[0])) {
                        if (alert) {
                            if (messages.getRoom().matches(messages.getSender())) {
                                NotifyChat.setNotifyChat(users.getName(), users.getJid(), messages.getMessage());
                            }

                            String checkCase = messages.getRoom().split("@")[0];
                            String[] room = checkCase.split("-");

                            System.out.println("indatabase " + checkCase);

                            if (room[0].matches("case") || room[0].matches("complaint")) {
                                NotifyCase.setNotifyChatCase(users.getName(), room[1], messages.getMessage());
                            } else {
                                NotifyChat.setNotifyChat(users.getName(), messages.getRoom(), messages.getMessage());
                            }

                        }
                    }


                } else {
                    // new TaskGetFriend(Application.getContext()).execute();
                    openfireQueary.getUser(messages.getSender().split("@")[0], new Callback<User>() {
                        @Override
                        public void success(User user, Response response) {
                            System.out.println("user = [" + user + "], response = [" + response + "]");
                            System.out.println("   " + user.getName());
                            messages.setSenderImage(user.getProperty().get("userImage"));
                            messages.setSenderName(user.getName());
                            databaseChatHelper.addLogs(messages);
                            BusProvider.getInstance().post(messages);
                            alert = spConfig.getBoolean(MasterData.SHARED_CONFIG_ALERT, true);
                            if (!messages.getSender().equalsIgnoreCase(mConnection.getUser().split("/")[0])) {
                                if (alert) {
                                    if (messages.getRoom().matches(messages.getSender())) {
                                        NotifyChat.setNotifyChat(user.getName(), user.getUsername() + "@pattaya-data", messages.getMessage());
                                    }

                                    String checkCase = messages.getRoom().split("@")[0];
                                    String[] room = checkCase.split("-");
                                    System.out.println("in server " + checkCase);

                                    if (room[0].matches("case") || room[0].matches("complaint")) {
                                        NotifyCase.setNotifyChatCase(user.getName(), room[1], messages.getMessage());
                                    } else {
                                        NotifyChat.setNotifyChat(user.getName(), messages.getRoom(), messages.getMessage());
                                    }


                                }
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            System.out.println("error = [" + error + "]");

                        }
                    });
                }
            }
        }.start();


    }

    public static void createPubSub() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                ConnectionConfiguration config = new ConnectionConfiguration(HOST, PORT, SERVICE);
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                //config.setDebuggerEnabled(true);
                config.setReconnectionAllowed(true);
                XMPPTCPConnection admin = new XMPPTCPConnection(config);
                PubSubManager manager;
                String username = USERNAME + "_notify";
                try {
                    admin.connect();
                    admin.login("admin", "p@ssw0rd", "Android");
                    Log.e("XMPPManage", "Admin Connection is : " + admin.isConnected());
                    ConfigureForm form = new ConfigureForm(FormType.submit);
                    form.setAccessModel(AccessModel.open);
                    form.setDeliverPayloads(true);
                    form.setNotifyRetract(true);
                    form.setNotifyDelete(true);
                    form.setMaxPayloadSize(1024000);
                    form.setPublishModel(PublishModel.open);
                    manager = new PubSubManager(admin);

                    try {
                        System.out.println("///////////////////////////////////////////////////////");
                        LeafNode myNode = (LeafNode) manager.createNode(username, form);

                        subscribePubSub();
                        String itemData =
                                "<x xmlns='jabber:x:data' type='result'>" +
                                        "<field var='title'>" +
                                        "<value>Subscribe Complete</value>" +
                                        "</field>" +
                                        "<field var='author'>" +
                                        "<value>" + USERNAME + "</value>" +
                                        "</field>" +
                                        "</x>";

                        SimplePayload payload = new SimplePayload(
                                "x",
                                "pubsub:" + username + ":x",
                                itemData);


                        PayloadItem<SimplePayload> item = new PayloadItem(
                                null, payload);
                        myNode.publish(item);
                        System.out.println("///////////////////////////////////////////////////////");

                    } catch (SmackException.NoResponseException e) {
                        e.printStackTrace();
                    } catch (XMPPException.XMPPErrorException e) {
                        e.printStackTrace();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }

                try {
                    admin.disconnect();
                    //mConnection.connect();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

    }

    public static void subscribePubSub() {
        String username = USERNAME + "_notify";
        PubSubManager mPubSubManager = new PubSubManager(mConnection);
        SubscribeForm subscriptionForm = new SubscribeForm(FormType.submit);
        subscriptionForm.setDeliverOn(true);
        subscriptionForm.setDigestFrequency(5000);
        subscriptionForm.setDigestOn(true);
        subscriptionForm.setIncludeBody(true);
        try {
            LeafNode existingNode = mPubSubManager.getNode(username);
            existingNode.subscribe(jid, subscriptionForm);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) Application.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        System.out.println("Connection check");
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    private void setChatRoomInvitationListener() {
        //MultiUserChat.getHostedRooms(mConnection,"");
        MultiUserChat.addInvitationListener(mConnection,
                new InvitationListener() {
                    @Override
                    public void invitationReceived(XMPPConnection xmppConnection, String room, String invite, String reason, String unknown, Message message) {
                        Log.e("XMPPManage", "Invite Room ::" + "room" + room + "  invite" + invite);
                        NotifyInviteGroup.getInstant().init(room, invite);
                    }
                });
    }

    public void setJoinRoom(String room) {
        new TaskJoin(room).execute();
    }

    public void initConnection() {
        // Initialization de la connexion
        Log.e("XMPPManage", "Login");
        config = new ConnectionConfiguration(HOST, PORT, SERVICE);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        //config.setDebuggerEnabled(true);
        config.setSendPresence(true);
        // config.
        mConnection = new XMPPTCPConnection(config);
        setChatRoomInvitationListener();
        messageReceiver();


    }

    public XMPPTCPConnection getmConnection() {
        return mConnection;
    }

    public void setmConnection(XMPPTCPConnection mConnection) {
        this.mConnection = mConnection;
    }


    void getGroupRoom() {
        ArrayList joinedRooms = null;
        try {
            joinedRooms = (ArrayList) MultiUserChat.getJoinedRooms(mConnection, mConnection.getUser());
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < joinedRooms.size(); i++) {
            Log.d("XMPPManage", "TAG Multi Room" + ":: " + joinedRooms.get(i));

        }
    }

    public MultiUserChat initGroupChat(String room) {
        //room = room;
        MultiUserChat muc = null;
        if (mConnection != null) {
            muc = new MultiUserChat(mConnection, room);
        }
        return muc;
    }

    public void setJoinGroupChat(MultiUserChat muc) {
        if (mConnection != null
                && muc != null
                && mConnection.isConnected()
                && mConnection.getUser() != null) {
            try {
                Log.e("XMPPManage", "Join ? : " + muc.isJoined());
                Log.e("XMPPManage", "Nick Name >>" + mConnection.getUser());
                String name = jid.split("@")[0];
                muc.join(name);
                Log.e("XMPPManage", "mConnector  : " + mConnection.isConnected());
                Log.e("XMPPManage", "Join ? : " + muc.isJoined());

            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }


    }

    public void sendInviteJoinRoom(final String room, final ArrayList<String> jid) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if (mConnection != null && mConnection.isConnected()) {
                    MultiUserChat muc = new MultiUserChat(mConnection, room);
                    try {
                        muc.join(USERNAME);
                        for (String e : jid) {
                            muc.invite(e, "Please Join From Android");
                        }

                        Log.d("adnan", "gg : " + room + "  ------------- " + jid);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (XMPPException.XMPPErrorException e) {
                        e.printStackTrace();
                    } catch (SmackException.NoResponseException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute();


    }

    public void disConnect() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Presence presence = new Presence(org.jivesoftware.smack.packet.Presence.Type.unavailable);
                    presence.setStatus(mConnection.getUser() + "logout");
                    //  pm = null;

                    try {
                        mConnection.disconnect(presence);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        mConnection.disconnect();
                    }

                    Log.w("LogOut", "LogOut");


                    mConnection.disconnect();
                    mConnection = null;
                    xmppManage = null;
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    public Boolean removeRoster(String jid) {
        if (mConnection != null && mConnection.isConnected()) {
            Roster roster = mConnection.getRoster();
            RosterEntry entry = roster.getEntry(jid);
            if (entry != null) {
                try {
                    roster.removeEntry(entry);
                    return true;
                } catch (XMPPException e) {

                    e.printStackTrace();
                    return false;
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                    return false;
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                    return false;
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                    return false;
                }

            }


        }
        return false;
    }

    public Boolean createRoster(String jid, String name) {
        System.out.println(name);
        final String[] group = {GROUP_FRIEND};
        if (mConnection != null && mConnection.isConnected()) {
            Roster roster = mConnection.getRoster();
            if (roster.getGroup(GROUP_FRIEND) == null) {
                roster.createGroup(GROUP_FRIEND);
            } else {
                roster.getGroup(GROUP_FRIEND);
            }
            Presence subscribe = new Presence(Presence.Type.subscribe);
            subscribe.setTo(jid);
            try {
                mConnection.sendPacket(subscribe);
                roster.createEntry(jid, name, group);
                return true;
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                return false;
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
                return false;
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                return false;
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                return false;
            }

        }

        return false;
    }

    public Boolean updateFavourite(String jid) {
        String group = GROUP_FAVORITE;
        String[] groups = {GROUP_FRIEND, group};
        System.out.println(mConnection + "'''''''''" + mConnection.isConnected());
        if (mConnection != null && mConnection.isConnected()) {
            System.out.println(mConnection + "'''''''''" + mConnection.isConnected());
            Roster roster = mConnection.getRoster();
            RosterEntry userEntry = roster.getEntry(jid);
            RosterGroup rosterGroup = roster.getGroup(group);
            if (rosterGroup == null) roster.createGroup(group);
            System.out.println(jid + "'''''''''" + userEntry);
            if (userEntry != null) {
                System.out.println("get group >>>" + userEntry.getGroups().isEmpty() + "   size" + userEntry.getGroups().size());
                try {
                    roster.createEntry(userEntry.getUser(), userEntry.getName(), groups);
                    return true;
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }

            }
        }
        return false;
    }

    public Boolean removeFavourite(String jid) {
        final String[] groups = {GROUP_FRIEND};
        if (mConnection != null && mConnection.isConnected()) {
            Roster roster = mConnection.getRoster();
            RosterEntry userEntry = roster.getEntry(jid);
            if (roster.getGroup(GROUP_FRIEND) == null) {
                roster.createGroup(GROUP_FRIEND);
            } else {
                roster.getGroup(GROUP_FRIEND);
            }
            if (userEntry != null) {
                try {
                    roster.createEntry(jid, userEntry.getName(), groups);
                    return true;
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                }

            }


        }

        return false;
    }

    public Boolean checkRoster(String jid) {
        if (mConnection != null && mConnection.isConnected()) {
            Roster roster = mConnection.getRoster();
            if (!roster.contains(jid)) {
                return true;
            } else {
                RosterEntry entry = roster.getEntry(jid);
                if (entry != null) {
                    if (entry.getGroups().isEmpty()) {
                        return true;
                    }
                }

            }
        }
        return false;

    }

    public void initIQForChatreQuest(final String room) {
        chatHistoryData.setCurrentListPacket(room);

        IQ iq1 = new IQ() {
            @Override
            public CharSequence getChildElementXML() {
                return "<list xmlns = 'urn:xmpp:archive'" +
                        " with='" + room + "' >" +
                        "<set xmlns='http://jabber.org/protocol/rsm'>" +
                        "<max>100</max>" +
                        "</set>" +
                        "</list>";
            }
        };
        try {
            iq1.setType(IQ.Type.GET);
            // iq.setPacketID("request");
            Log.e("XMPPManage", "TAG- SEND PAcket >>> TO " + "" + iq1);
            xmppManage.getmConnection().sendPacket(iq1);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

    }

    public void initChat(String user) {
        ChatManager chatmanager = ChatManager.getInstanceFor(mConnection);
        chat = chatmanager.createChat(user, this);
        mChat = chatmanager.createChat(mConnection.getUser(), this);
        //chat("Hi From " + USERNAME);
    }

    public void chat(final String message, final String room) {

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                int randomNum = 500 + (int) (Math.random() * 2000);
                String packetId = "adpckt" + randomNum;
                try {
                    Message messages = new Message();
                    messages.setSubject(room);
                    messages.setBody(message);
                    messages.setPacketID(packetId);
                    //messages.setTo("ffff");
                    chat.sendMessage(message);
                    mChat.sendMessage(messages);


                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();


    }

    public void closeChat() {
        if (chat != null) {
            chat.close();
            mChat.close();
        }


        Log.e("Check Chat is Null", ">>>" + chat + "   " + mChat);

    }

    public void sendMessageNotify(final PubsubObject pubsubObject) {
        Log.e("AG", "/////////////////////////////////////////////////////////");

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                final String image = (pubsubObject.getImage() == null) ? "" : pubsubObject.getImage();
                final String name = (pubsubObject.getName() == null) ? "" : pubsubObject.getName();
                final String title = (pubsubObject.getTitle() == null) ? "" : pubsubObject.getTitle();
                final String displayData = (pubsubObject.getDisplayData() == null) ? "" : pubsubObject.getDisplayData();
                final String action = (pubsubObject.getAction() == null) ? "" : pubsubObject.getAction();
                final Integer primarykey = pubsubObject.getPrimarykey();
                final Integer caseId = pubsubObject.getCaseId();
                final Integer complainId = pubsubObject.getComplainId();


                ConnectionConfiguration config = new ConnectionConfiguration(HOST, PORT, SERVICE);
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                // config.setDebuggerEnabled(true);
                admin = new XMPPTCPConnection(config);
                manager = new PubSubManager(admin);
                final String username = pubsubObject.getUsername() + "_notify";

                try {
                    if (isNetworkConnected()) {
                        admin.connect();
                        admin.login("admin", "p@ssw0rd", "Android");

                        Log.e("XMPPManage", "Admin Connection is SendMessage : " + admin.isConnected());
                        ConfigureForm form = new ConfigureForm(FormType.submit);
                        form.setAccessModel(AccessModel.open);
                        form.setDeliverPayloads(true);
                        form.setNotifyRetract(true);
                        form.setNotifyDelete(true);
                        form.setPublishModel(PublishModel.open);
                        manager = new PubSubManager(admin);
                        LeafNode myNode = manager.getNode(username);

                        DataForm dataForm = new DataForm("result");

                        ///////////////  ownerImage ////////////////////
                        FormField formImage = new FormField("ownerImage");
                        formImage.addValue(image);

                        dataForm.addField(formImage);

                        ///////////////  ownerName ////////////////////
                        FormField formName = new FormField("ownerName");
                        formName.addValue(name);

                        dataForm.addField(formName);

                        ///////////////  Title ////////////////////
                        FormField formTitle = new FormField("title");
                        formTitle.addValue(title);


                        dataForm.addField(formTitle);


                        ///////////////  Action ////////////////////
                        FormField formAction = new FormField("action");
                        formAction.addValue(action);

                        dataForm.addField(formAction);


                        ///////////////  primaryKey ////////////////////
                        FormField formKey = new FormField("primaryKey");
                        formKey.addValue(String.valueOf(primarykey));

                        dataForm.addField(formKey);

                        ///////////////  primaryKey ////////////////////
                        FormField formCaseKey = new FormField("caseId");
                        formCaseKey.addValue(String.valueOf(caseId));

                        dataForm.addField(formCaseKey);

                        ///////////////  primaryKey ////////////////////
                        FormField formComplainKey = new FormField("complainId");
                        formComplainKey.addValue(String.valueOf(complainId));

                        dataForm.addField(formComplainKey);


                        ///////////////  displayDate ////////////////////
                        FormField formDate = new FormField("displayDate");
                        formDate.addValue(displayData);

                        dataForm.addField(formDate);

                        SimplePayload payload = new SimplePayload(
                                "x",
                                "pubsub:" + username + ":x",
                                dataForm.toXML().toString());


                        PayloadItem<SimplePayload> item = new PayloadItem(
                                null, payload);
                        myNode.send(item);
                        //System.out.println("///////////////////////////////////////////////////////");

                    } else {

                    }

                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    //subscribePubSub();
                    e.printStackTrace();
                }

                try {

                    admin.disconnect();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();


    }

    @Override
    public void processMessage(Chat chat, Message message) {

    }

    public class TaskSendNotify extends AsyncTask<Void, Void, Boolean> {
        PubsubObject pubsubObject;

        public TaskSendNotify(PubsubObject pubsubObject) {
            this.pubsubObject = pubsubObject;

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            sendMessageNotify(pubsubObject);

            return null;
        }
    }

    public class TaskJoin extends AsyncTask<Void, Void, Boolean> {
        String room;

        public TaskJoin(String room) {
            this.room = room;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            if (mConnection != null && mConnection.isConnected()) {
                MultiUserChat muc = new MultiUserChat(mConnection, room);
                try {
                    // Log.e("XMPPManage", "Join ? 1: " + room + "   " + muc.isJoined());
                    String name = jid.split("@")[0];
                    muc.join(name);
                    Log.e("XMPPManage", "mConnector  1: " + room + "   " + muc.isJoined());
                    //Log.e("XMPPManage", "Join ? 1 : " + room + "   " + muc.isJoined());

                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
}
