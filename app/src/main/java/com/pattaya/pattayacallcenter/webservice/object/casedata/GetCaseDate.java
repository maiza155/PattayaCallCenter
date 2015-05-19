package com.pattaya.pattayacallcenter.webservice.object.casedata;

/**
 * Created by SWF on 5/19/2015.
 */
public class GetCaseDate {
    int primaryKeyId;
    int foreignKeyId;
    String accessToken;
    String clientId;

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

    public int getForeignKeyId() {
        return foreignKeyId;
    }

    public void setForeignKeyId(int foreignKeyId) {
        this.foreignKeyId = foreignKeyId;
    }

    public int getPrimaryKeyId() {
        return primaryKeyId;
    }

    public void setPrimaryKeyId(int primaryKeyId) {
        this.primaryKeyId = primaryKeyId;
    }
}
