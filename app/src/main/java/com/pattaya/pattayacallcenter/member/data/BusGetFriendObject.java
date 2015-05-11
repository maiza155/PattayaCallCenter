package com.pattaya.pattayacallcenter.member.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SWF on 4/28/2015.
 */
public class BusGetFriendObject {
    int count = 0;
    Boolean isOk = false;
    List<InviteFriendObject> listGroupData = new ArrayList<>();

    public BusGetFriendObject() {

    }


    public List<InviteFriendObject> getListGroupData() {
        return listGroupData;
    }

    public void setListGroupData(List<InviteFriendObject> listGroupData) {
        this.listGroupData = listGroupData;
    }


    public void setCount(int count) {
        isOk = true;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public Boolean getIsOk() {
        return isOk;
    }
}
