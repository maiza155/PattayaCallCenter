package com.pattaya.pattayacallcenter.chat.xmlobject.Roster;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by SWF on 3/12/2015.
 */
@Root
public class Groups {
    @ElementList(required = false,inline = true)
    List<Group> group;


    public List<Group> getGroup() {
        return group;
    }

    public void setGroup(List<Group> group) {
        this.group = group;
    }
}