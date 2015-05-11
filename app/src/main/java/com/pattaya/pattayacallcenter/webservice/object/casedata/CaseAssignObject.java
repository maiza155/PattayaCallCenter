package com.pattaya.pattayacallcenter.webservice.object.casedata;

/**
 * Created by SWF on 4/25/2015.
 */
public class CaseAssignObject {
    int caseAssignId;
    int organizeId;
    int userId;
    String userName;
    String userImage;
    String organizeName;

    public String getOrganizeName() {
        return organizeName;
    }

    public int getCaseAssignId() {
        return caseAssignId;
    }

    public int getOrganizeId() {
        return organizeId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserImage() {
        return userImage;
    }
}
