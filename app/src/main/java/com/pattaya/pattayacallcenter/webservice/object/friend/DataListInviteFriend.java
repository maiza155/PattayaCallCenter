package com.pattaya.pattayacallcenter.webservice.object.friend;

/**
 * Created by SWF on 3/31/2015.
 */
public class DataListInviteFriend  {

    String dataType;
    String displayName;
    int id;
    String userList;
    String userImage;
    String jid;

    @Override
    public String toString() {
        return "DataListInviteFriend{" +
                "dataType='" + dataType + '\'' +
                ", displayName='" + displayName + '\'' +
                ", id=" + id +
                ", userList='" + userList + '\'' +
                ", userImage='" + userImage + '\'' +
                ", jid='" + jid + '\'' +
                '}';
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserList() {
        return userList;
    }

    public void setUserList(String userList) {
        this.userList = userList;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }
}
