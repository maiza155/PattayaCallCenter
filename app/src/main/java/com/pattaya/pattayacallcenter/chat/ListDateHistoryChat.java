package com.pattaya.pattayacallcenter.chat;

import android.util.Log;

import com.pattaya.pattayacallcenter.Data.Messages;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.XmppDateTime;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Created by SWF on 3/3/2015.
 */
public class ListDateHistoryChat implements IQProvider {
    // public static final String NAMESPACE = "urn:xmpp:archive";

    public static final String ELEMENT_NAME = "list";
    public static String WITH = "telember@pattaya-data";
    ChatHistoryData chatHistoryData = ChatHistoryData.getInstance();
    XMPPManage xmppManage = XMPPManage.getInstance();
    private List<Date> dates;
    private List<Date> tempDate;
    private ArrayList<String> startTimes;
    private ArrayList<Package> packet;
    public ListDateHistoryChat() {
        super();
    }

    @Override
    public IQ parseIQ(final XmlPullParser parser) throws Exception {
        // Log.d("Start loop ", "------Room : " + parser.getNamespace("to"));
        boolean stop = false;
        dates = new ArrayList();
        tempDate = new ArrayList();
        startTimes = new ArrayList();
        packet = new ArrayList();
        String name = parser.getName();

        String mRoom = chatHistoryData.getCurrentListPacket(); // ห้องล่าสุดที่ ถูกส่ง request -> server  เพื่อดึง list



        while (false == stop) {
            switch (parser.getEventType()) {
                case XmlPullParser.START_TAG: {
                    if (ELEMENT_NAME.equals(name)) {
                        while (parser.nextTag() == XmlPullParser.START_TAG) {
                            /*** Stop when chats is finished.. ***/
                            if (parser.getName().equals("set")) {
                                //Log.d("Start loop ", "------" + parser.getName());
                                stop = true;
                                break;
                            }
                            Date date = XmppDateTime.parseDate(parser.getAttributeValue(1));


                            Log.d("ListDateHistory", chatHistoryData.getCurrentListPacket() + "------" + parser.getAttributeValue("", "with"));

                            //Log.d("Start loop ", "------" + parser.getAttributeValue(0));
                            //Log.d("Start loop ", "------" + XmppDateTime.parseDate(parser.getAttributeValue(1)));

                            //  Log.d("Start loop ","------"+parser.getAttributeValue(2));
                            //  Log.d("Start loop ","------"+parser.getAttributeValue(3));

                            //เช็คว่า ห้องที่ส่ง packet ไป reply from server is true ?
                            if (mRoom.equalsIgnoreCase(parser.getAttributeValue("", "with"))) {
                                WITH = parser.getAttributeValue("", "with");
                                Log.d("ListDateHistory", "------" + WITH);
                                String startTime = parser.getAttributeValue(1);
                                Log.d("ListDateHistory", "Get Time From List  " + startTime);
                                startTimes.add(startTime);//เก็บ date format ที่ ได้มาจาก server
                                dates.add(date); //เก็บ date format ที่ ได้มาจาก server เเต่ถูกเเปลงเเล้ว เพื่อจะนำไป sort
                                tempDate.add(date);//เก็บ date format ที่ ได้มาจาก server เเต่ถูกเเปลงเเล้ว (ตำเเหน่งเดิม ใช้ map)
                            }
                            parser.nextTag();
                        }
                    }
                    break;
                }
                case XmlPullParser.END_TAG: {
                    //stop =
                    stop = ELEMENT_NAME.equals(name);
                    break;
                }
            }
        }
        //  Log.e("GET DATE FROM SERVER","GO WITH >>>>>>>>>>"+WITH);

        ArrayList<Messages> value = chatHistoryData.getRoom().get(WITH);
        chatHistoryData.addRoom(WITH, new ArrayList<Messages>());
        if (value == null) {
            chatHistoryData.addRoom(WITH, new ArrayList<Messages>());
        } else {
            //  Log.d("EXSTENCE Data ", ""+value.size());
            chatHistoryData.getRoom().remove(WITH);
            chatHistoryData.addRoom(WITH, new ArrayList<Messages>());
            //
        }
        //final HistoryIQ historyIQ = new HistoryIQ(startTimes);
        //historyIQ.getHistory_Chats();
        //List<Date> TempDate  = dates;
        //chatHistoryData.setStartTime(startTimes);


        // เรียง เวลาที่ได้รับมาใหม่
        Collections.sort(dates, new Comparator<Date>() {
            @Override
            public int compare(Date lhs, Date rhs) {
                return lhs.compareTo(rhs);
            }
        });
        System.out.println(dates.size());

        //วน loop  เพื่อส่งค่าที่จัดเรียงเเล้วไป get chat history list มาอีกที
        for (int i = 0; i < dates.size(); i++) {
            //System.out.println(dates.get(i).toString());
            final int mIndex = tempDate.indexOf(dates.get(i));//get Date เเล้ว map จาก Date format ที่ได้มาจาก server
            //startTimes.add(temnpstartTimes.get(mIndex));
            //System.out.println("parser = [" + startTimes.get(i) + "]");
            IQ iq = new IQ() {
                @Override
                public CharSequence getChildElementXML() {
                    return "<retrieve xmlns = 'urn:xmpp:archive'" +
                            " with='" + WITH + "' " +
                            " start='" + startTimes.get(mIndex) + "' >" +
                            "<set xmlns='http://jabber.org/protocol/rsm'>" +
                            "<max>1000</max>" +
                            "</set>" +
                            "</retrieve>";
                }
            };


            try {
                iq.setType(IQ.Type.GET);
                // iq.setPacketID("request");
                //Package temp_packet = new Package(iq.getPacketID(),"");
                // packet.add(new Package(iq.getPacketID(),""));
                chatHistoryData.addLastMap(WITH, iq.getPacketID());
                chatHistoryData.addMap(iq.getPacketID(), WITH);
                Log.e("ListDateHistory", "TAG- SEND PAcket >>> TO " + iq.getPacketID());
                xmppManage.getmConnection().sendPacket(iq);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

}


