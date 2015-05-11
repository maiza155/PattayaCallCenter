package com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom;



import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by SWF on 3/14/2015.
 */
@Root(strict = false)
public class ChatRoom {

    @Element
    String roomName;

    @Element
    String naturalName;

    @ElementList(inline = true)
    List<Owners> owners;

    @Element
    Members members;

    @Element
    Outcasts outcasts;

    @Element(required = false)
    String description;

    public String getRoomName() {
        return roomName;
    }

    public List<Owners> getOwners() {
        return owners;
    }

    public Members getMembers() {
        return members;
    }

    public String getNaturalName() {
        return naturalName;
    }

    public String getDescription() {
        return description;
    }

    public Outcasts getOutcasts() {
        return outcasts;
    }
}
