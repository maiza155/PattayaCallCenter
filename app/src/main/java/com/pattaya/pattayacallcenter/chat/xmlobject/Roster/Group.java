package com.pattaya.pattayacallcenter.chat.xmlobject.Roster;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

/**
 * Created by SWF on 4/1/2015.
 */
@Root
public class Group {
    @Text
    String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
