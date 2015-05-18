package com.pattaya.pattayacallcenter.webservice.object.organize;

/**
 * Created by SWF on 3/16/2015.
 */
public class OrgDetail {
    String userImage;
    String displayName;
    String userId;
    String jId;


    public String getjId() {
        return jId;
    }

    public void setjId(String jId) {
        this.jId = jId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
