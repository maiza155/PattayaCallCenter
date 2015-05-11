package com.pattaya.pattayacallcenter.member.dummy;

import java.io.Serializable;

/**
 * Created by SWF on 2/4/2015.
 */
public class DataShowAddFriend extends DataShowAddGroup {

    private String alert;


    public DataShowAddFriend(String name, String depart,String alert, int pic) {
        super(name, depart, pic);
        this.alert = alert;
    }

    public String getAlert(){
        return this.alert;
    }
}