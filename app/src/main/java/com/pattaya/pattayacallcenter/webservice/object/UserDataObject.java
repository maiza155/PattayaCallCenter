package com.pattaya.pattayacallcenter.webservice.object;

/**
 * Created by SWF on 3/13/2015.
 */
public class UserDataObject {
    private int userId;
    private String username;
    private String userImage;
    private String displayName;
    private String userTypeEN;
    private String orgId;
    private String jId;
    String firstname;
    String lastname;

    @Override
    public String toString() {
        return "UserDataObject{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", userImage='" + userImage + '\'' +
                ", displayName='" + displayName + '\'' +
                ", userTypeEN='" + userTypeEN + '\'' +
                ", orgId='" + orgId + '\'' +
                ", jId='" + jId + '\'' +
                '}';
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUserTypeEN() {
        return userTypeEN;
    }

    public String getOrgId() {
        return orgId;
    }

    public String getUsername() {
        return username;
    }

    public String getjId() {
        return jId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
