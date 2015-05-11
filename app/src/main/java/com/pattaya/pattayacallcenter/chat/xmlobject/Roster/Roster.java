package com.pattaya.pattayacallcenter.chat.xmlobject.Roster;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by SWF on 3/11/2015.
 */
@Root
public class Roster {
    @ElementList(name = "rosterItem", inline = true,required = false)
    List<RosterItem> rosterItem;

    public List<RosterItem> getRosterItem() {
        return rosterItem;
    }

    public void setRosterItem(List<RosterItem> rosterItem) {
        this.rosterItem = rosterItem;
    }


}