package com.pattaya.pattayacallcenter.webservice.object;

/**
 * Created by SWF on 5/12/2015.
 */
public class OfficialObject {
    int id;
    String displayName;
    String userImage;

    public String getDisplayname() {
        return displayName;
    }

    public void setDisplayname(String displayname) {
        this.displayName = displayname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
