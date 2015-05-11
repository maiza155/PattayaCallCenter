package com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

/**
 * Created by SWF on 3/31/2015.
 */
@Root
public class Member{
    @Text
    private String text;

    public String getText() {
        return text;
    }
}
