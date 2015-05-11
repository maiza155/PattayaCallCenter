package com.pattaya.pattayacallcenter.webservice.object.casedata;

import java.util.List;

/**
 * Created by SWF on 4/27/2015.
 */
public class SendReassignTaskObject {
    int casesId;
    int userId;
    String resultsOperations;
    List taskImageList;
    int typeCaseAssign;
    List caseAssignList;
    int updateBy;
    int createBy;
    String accessToken;
    String clientId;


    public void setCasesId(int casesId) {
        this.casesId = casesId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setResultsOperations(String resultsOperations) {
        this.resultsOperations = resultsOperations;
    }

    public void setTaskImageList(List taskImageList) {
        this.taskImageList = taskImageList;
    }

    public void setTypeCaseAssign(int typeCaseAssign) {
        this.typeCaseAssign = typeCaseAssign;
    }

    public void setCaseAssignList(List caseAssignList) {
        this.caseAssignList = caseAssignList;
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
}
