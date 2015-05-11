package com.pattaya.pattayacallcenter.chat;

import com.pattaya.pattayacallcenter.Data.Messages;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.XmppDateTime;
import org.xmlpull.v1.XmlPullParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


/**
 * Created by SWF on 3/3/2015.
 */
public class ListHistoryChat implements IQProvider {

    public static final String ELEMENT_NAME = "chat";
    public String WITH = "telember@pattaya-data";
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    ChatHistoryData chatHistoryData = ChatHistoryData.getInstance();

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        ArrayList<Messages> messageArr = new ArrayList<>();
        HashMap<String, Messages> map = new HashMap<>();
        Messages messages = null;
        long time = 0;
        int eventType = parser.getEventType();
        /*
        DOM2XmlPullBuilder dom2XmlPullBuilder = new DOM2XmlPullBuilder();
        Element element = dom2XmlPullBuilder.parseSubTree(parser);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(element);
        transformer.transform(source, result);

        String xmlString = result.getWriter().toString();
        System.out.println(xmlString);
        */

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equalsIgnoreCase(ELEMENT_NAME)) {
                    messages = new Messages();
                    Date date = XmppDateTime.parseDate(parser.getAttributeValue("", "start"));
                    time = date.getTime() / 1000;
                    // Log.e("....Time>>>", time + "_________" + date);
                    // Log.e("....with>>>", parser.getAttributeValue("", "with"));
                    WITH = parser.getAttributeValue("", "with");
                    messages.setRoom(WITH);
                } else if (parser.getName().equalsIgnoreCase("body")) {
                    String text = parser.nextText();
                    //Log.e("....Body2>>>", messages.getMessage());
                    messages.setMessage(text);
//                    Log.e("....Message Time>>>", ""+map.get(messages.getTime()).toString());

                    // map เช็คเผื่อค่า ซ้ำ กรณีที่เป็น muc เนื่องจากตอน get มาจาก server  เเล้วเกิด bug
                    if (map.get(messages.getTime()) == null) {
                        // Log.e("....Body>>>"+messages.getTime(), messages.getMessage());
                        Messages tempMessage = new Messages(messages.getTime(), messages.getMessage(), messages.getRoom(), messages.getSender());
                        messageArr.add(tempMessage);
                        map.put(messages.getTime(), tempMessage);
                    } else {
                        //  Log.e("....duplicate Values>>>", messages.getMessage());
                    }
                    // extract the headline
                    // parser.nextTag();
                } else if (parser.getName().equalsIgnoreCase("to")) {
                    long tempTimp = Long.parseLong(parser.getAttributeValue("", "secs"));
                    Date d = new Date((time + tempTimp) * 1000);
                    // String from = parser.getAttributeValue("", "jid");
                    // Log.d("FROM >>>>>>>>>>>>>", "" + from);

                    messages.setSender(XMPPManage.getInstance().getmConnection().getUser().split("/")[0]);
                    messages.setTime(dateFormat.format(d)); // set Time
                    if (map.get(messages.getTime()) == null) {
                        map.put(messages.getTime(), null);
                    }
                    // Log.i("to ....", XMPPManage.getInstance().getmConnection().getUser().split("/")[0] + "====" + d);  // extract the link of article
                } else if (parser.getName().equalsIgnoreCase("from")) {
                    long tempTimp = Long.parseLong(parser.getAttributeValue("", "secs"));
                    Date d = new Date((time + tempTimp) * 1000);

                    // ใช้เมื่อ ต้องดึงค่า history เเบบ muc จึงต้อง check null ด้วย
                    String from = parser.getAttributeValue("", "jid");
                    if (from == null) {
                        from = WITH;
                    }
                    // Log.d("FROM >>>>>>>>>>>>>", "" + from);
                    messages.setSender(from);
                    messages.setTime(dateFormat.format(d)); // set Time
                    if (map.get(messages.getTime()) == null) {
                        map.put(messages.getTime(), null);
                    }

                    /// Log.i("from ....", WITH + "   " + d);  // extract the link of article
                } else if (parser.getName().equalsIgnoreCase("set")) {
                    addChatData(messageArr);
                    break;
                }
            }
            eventType = parser.next(); // move to next element
        }
        return null;
    }

    public void addChatData(ArrayList<Messages> message) {
        ArrayList<Messages> data = new ArrayList<>();
        ArrayList<Messages> pChatData = chatHistoryData.getRoom().get(WITH);
        //  Log.d("Data", "  " + pChatData);
        //  Log.d("DAta2 ", "  " + message);
        data.addAll(pChatData);
        data.addAll(message);
        chatHistoryData.getRoom().put(WITH, data);
    }


}









