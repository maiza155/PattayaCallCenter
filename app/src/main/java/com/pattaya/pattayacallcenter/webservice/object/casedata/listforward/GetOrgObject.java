package com.pattaya.pattayacallcenter.webservice.object.casedata.listforward;

/**
 * Created by SWF on 4/17/2015.
 */
public class GetOrgObject {
    Boolean isActive = true;
    String orgName;

    public GetOrgObject(String orgName) {
        this.orgName = orgName;
    }

    public GetOrgObject() {
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
