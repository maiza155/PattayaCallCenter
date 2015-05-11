package com.pattaya.pattayacallcenter.webservice.object.organize;

/**
 * Created by SWF on 3/17/2015.
 */
public class GetOrgObject {
    String orgId;
    String accessToken;
    String isDeleted = "false";
    String isResign = "false";
    String clientId;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
