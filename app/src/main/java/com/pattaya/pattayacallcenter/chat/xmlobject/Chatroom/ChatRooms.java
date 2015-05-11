package com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by SWF on 3/14/2015.
 */
@Root
public class ChatRooms {
    @ElementList(inline = true, required = false)
    List<ChatRoom> chatRooms;

    public List<ChatRoom> getChatRoom() {
        return chatRooms;
    }
}