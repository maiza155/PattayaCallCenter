package com.pattaya.pattayacallcenter.webservice.object.casedata;

import java.util.List;

/**
 * Created by SWF on 4/25/2015.
 */
public class CaseDataMemberObject {

    List<CaseAssignObject> caseAssignList;
    String casesName;
    CaseDataObject contactInfo;
    int contactInfoId;
    int serviceTypeId;
    int typeCaseAssign;
    String countDate;
    Boolean isAction;
    Boolean isFollowUp;
    int percentSuccess;
    Integer priorityString;

    public Integer getPriority() {
        return priorityString;
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

    public int getPercentSuccess() {
        return percentSuccess;
    }

    public int getTypeCaseAssign() {
        return typeCaseAssign;
    }

    public List<CaseAssignObject> getCaseAssignList() {
        return caseAssignList;
    }

    public String getCasesName() {
        return casesName;
    }

    public CaseDataObject getContactInfo() {
        return contactInfo;
    }

    public int getContactInfoId() {
        return contactInfoId;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }
}
