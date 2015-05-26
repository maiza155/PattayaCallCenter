package com.pattaya.pattayacallcenter.webservice.object.friend;

/**
 * Created by SWF on 3/31/2015.
 */
public class GetListInviteFriendObject {

    String username;

    String firstname;
    String lastname;


    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
