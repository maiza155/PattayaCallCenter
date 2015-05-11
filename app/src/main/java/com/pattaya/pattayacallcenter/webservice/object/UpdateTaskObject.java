package com.pattaya.pattayacallcenter.webservice.object;

/**
 * Created by SWF on 4/26/2015.
 */
public class UpdateTaskObject {
    int casesId;
    int userId;
    String startDateString;
    String duleDateString;
    String accessToken;
    String clientId;


    public void setCasesId(int casesId) {
        this.casesId = casesId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setStartDateString(String startDateString) {
        this.startDateString = startDateString;
    }

    public void setDuleDateString(String duleDateString) {
        this.duleDateString = duleDateString;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
