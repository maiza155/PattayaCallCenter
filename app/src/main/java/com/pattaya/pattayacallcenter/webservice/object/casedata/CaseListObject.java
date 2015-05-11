package com.pattaya.pattayacallcenter.webservice.object.casedata;

import java.util.List;

/**
 * Created by SWF on 4/2/2015.
 */
public class CaseListObject {
    List<CaseListDataObject> data;

    public List<CaseListDataObject> getData() {
        return data;
    }

    public void setData(List<CaseListDataObject> data) {
        this.data = data;
    }
}
