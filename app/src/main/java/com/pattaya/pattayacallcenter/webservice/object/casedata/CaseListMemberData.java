package com.pattaya.pattayacallcenter.webservice.object.casedata;

/**
 * Created by SWF on 4/23/2015.
 */
public class CaseListMemberData {
    String caseName;
    int casesId;
    int complaintId;
    String countDate;
    Boolean isAction;
    Boolean isFollowUp;
    Boolean isNew;
    int percentSuccess;
    String date;
    Integer priority;
    String casesType;

    public String getCasesType() {
        return casesType;
    }

    public Integer getPriority() {
        return priority;
    }

    public String getDate() {
        return date;
    }

    public String getCaseName() {
        return caseName;
    }

    public int getCasesId() {
        return casesId;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public String getCountDate() {
        return countDate;
    }

    public Boolean getIsAction() {
        return isAction;
    }

    public Boolean getIsFollowUp() {
        return isFollowUp;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public int getPercentSuccess() {
        return percentSuccess;
    }
}
