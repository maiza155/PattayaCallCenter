package com.pattaya.pattayacallcenter.chat.xmlobject.Roster;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by SWF on 3/12/2015.
 */
@Root(strict = false, name = "rosterItem")
public class RosterItem {
    @Element
    String jid;
    @Element(required = false)
    String nickname;
    @Element(required = false)
    Groups groups;

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Groups getGroups() {
        return groups;
    }

    public void setGroups(Groups groups) {
        this.groups = groups;
    }
}