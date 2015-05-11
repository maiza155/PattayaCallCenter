package com.pattaya.pattayacallcenter.chat.jsonobject;

/**
 * Created by SWF on 4/7/2015.
 */
public class ChatRoomObject {
    String roomName;
    String naturalName;
    String description;
    String subject;
    Members members;
    Outcasts outcasts;
    Boolean  persistent = true;


    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getNaturalName() {
        return naturalName;
    }

    public void setNaturalName(String naturalName) {
        this.naturalName = naturalName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Members getMembers() {
        return members;
    }

    public void setMembers(Members members) {
        this.members = members;
    }

    public Outcasts getOutcasts() {
        return outcasts;
    }

    public void setOutcasts(Outcasts outcasts) {
        this.outcasts = outcasts;
    }
}
