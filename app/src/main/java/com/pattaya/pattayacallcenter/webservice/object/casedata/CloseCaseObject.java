package com.pattaya.pattayacallcenter.webservice.object.casedata;

import java.util.List;

/**
 * Created by SWF on 4/27/2015.
 */
public class CloseCaseObject {
    int casesId;
    int userId;
    int updateBy;
    int createBy;
    String accessToken;
    String clientId;
    List taskImageList;
    int resultTypeString;
    String completedDateString;
    String resultsOperations;

    public void setResultsOperations(String resultsOperations) {
        this.resultsOperations = resultsOperations;
    }

    public void setCompletedDateString(String completedDateString) {
        this.completedDateString = completedDateString;
    }

    public void setResultTypeString(int resultTypeString) {
        this.resultTypeString = resultTypeString;
    }

    public void setCasesId(int casesId) {
        this.casesId = casesId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUpdateBy(int updateBy) {
        this.updateBy = updateBy;
    }

    public void setCreateBy(int createBy) {
        this.createBy = createBy;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setTaskImageList(List taskImageList) {
        this.taskImageList = taskImageList;
    }
}
