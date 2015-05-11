package com.pattaya.pattayacallcenter.webservice.object.casedata;

import java.util.List;

/**
 * Created by SWF on 4/27/2015.
 */
public class CaseResultData {

    String completedDateString;
    String resultsOperations;
    String userImageUrl;
    List<TaskImageObject> taskImageList;
    String userName;


    public String getUserName() {
        return userName;
    }

    public void setCompletedDateString(String completedDateString) {
        this.completedDateString = completedDateString;
    }

    public String getCompletedDateString() {
        return completedDateString;
    }

    public String getResultsOperations() {
        return resultsOperations;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public List<TaskImageObject> getTaskImageList() {
        return taskImageList;
    }
}
