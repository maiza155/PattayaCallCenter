package com.pattaya.pattayacallcenter.webservice.object.casedata;

/**
 * Created by SWF on 4/2/2015.
 */
public class GetComplainObject {
    int primaryKeyId;
    String accessToken;
    String clientId;
    int updateBy;

    public void setUpdateBy(int updateBy) {
        this.updateBy = updateBy;
    }

    public void setPrimaryKeyId(int primaryKeyId) {
        this.primaryKeyId = primaryKeyId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
