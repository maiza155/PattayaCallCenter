package com.pattaya.pattayacallcenter.webservice.object.casedata;

/**
 * Created by SWF on 3/30/2015.
 */
public class GetCaseListData {


    String textSearch;
    int filterType;
    int userId;
    int casesId;
    String accessToken;
    String clientId;
    Boolean action;
    int completed;


    public void setAction(Boolean action) {
        this.action = action;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int getCasesId() {
        return casesId;
    }

    public void setCasesId(int casesId) {
        this.casesId = casesId;
    }

    public String getTextSearch() {
        return textSearch;
    }

    public void setTextSearch(String textSearch) {
        this.textSearch = textSearch;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
