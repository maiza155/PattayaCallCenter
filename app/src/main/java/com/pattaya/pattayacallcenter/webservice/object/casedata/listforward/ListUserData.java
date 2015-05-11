package com.pattaya.pattayacallcenter.webservice.object.casedata.listforward;

import com.pattaya.pattayacallcenter.webservice.object.UserDataObject;

import java.util.List;

/**
 * Created by SWF on 4/18/2015.
 */
public class ListUserData {
    List<UserDataObject> data;

    public List<UserDataObject> getData() {
        return data;
    }

    public void setData(List<UserDataObject> data) {
        this.data = data;
    }
}
