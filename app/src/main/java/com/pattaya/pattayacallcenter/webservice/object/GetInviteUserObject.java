package com.pattaya.pattayacallcenter.webservice.object;

/**
 * Created by SWF on 3/17/2015.
 */
public class GetInviteUserObject {
    String username;

    public GetInviteUserObject(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
