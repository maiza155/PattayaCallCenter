package com.pattaya.pattayacallcenter.webservice.object.casedata.listforward;

/**
 * Created by SWF on 4/17/2015.
 */
public class GetUserObject {
    Boolean isDeleted = false;
    Boolean isResign = false;
    Boolean isLocked = false;
    String firstname;
    String lastname;
    int userType = 1;

    public GetUserObject(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public GetUserObject() {
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
