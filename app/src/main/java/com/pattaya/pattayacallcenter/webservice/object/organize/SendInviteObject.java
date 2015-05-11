package com.pattaya.pattayacallcenter.webservice.object.organize;

import java.util.List;

/**
 * Created by SWF on 3/17/2015.
 */
public class SendInviteObject {
    List<UserIdObject> userList;
    String orgId;
    int inviteByUserId;

    public List<UserIdObject> getUserList() {
        return userList;
    }

    public void setUserList(List<UserIdObject> userList) {
        this.userList = userList;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public int getInviteByUserId() {
        return inviteByUserId;
    }

    public void setInviteByUserId(int inviteByUserId) {
        this.inviteByUserId = inviteByUserId;
    }
}
