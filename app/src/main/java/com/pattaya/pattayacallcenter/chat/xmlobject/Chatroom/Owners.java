package com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by SWF on 3/14/2015.
 */
@Root
public class Owners {
    @Element(required = false)
    String owner;

    public String getOwner() {
        return owner;
    }
}
