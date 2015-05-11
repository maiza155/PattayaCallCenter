package com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by SWF on 3/14/2015.
 */
@Root
public class Outcasts {
    @ElementList(inline = true,entry = "outcast",required = false)
    public List<Member> outcast;

    public List<Member> getListMember() {
        return outcast;
    }
}
