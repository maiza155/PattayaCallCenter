package com.pattaya.pattayacallcenter.webservice.object.casedata;

import java.util.List;

/**
 * Created by SWF on 4/20/2015.
 */
public class OpenCaseAssignUserObject {
    String casesName;
    String complaintDateString;
    int typeCaseAssign;
    List<UserIdObject> caseAssignList;
    int typeCloseTaskAssign;
    List closeTaskAssignList;
    int createBy;
    int serviceTypeId;
    CaseDataObject contactInfo;
    String accessToken;
    String clientId;

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public void setCasesName(String casesName) {
        this.casesName = casesName;
    }

    public void setComplaintDateString(String complaintDateString) {
        this.complaintDateString = complaintDateString;
    }

    public void setTypeCaseAssign(int typeCaseAssign) {
        this.typeCaseAssign = typeCaseAssign;
    }

    public void setCaseAssignList(List caseAssignList) {
        this.caseAssignList = caseAssignList;
    }

    public void setTypeCloseTaskAssign(int typeCloseTaskAssign) {
        this.typeCloseTaskAssign = typeCloseTaskAssign;
    }

    public void setCloseTaskAssignList(List closeTaskAssignList) {
        this.closeTaskAssignList = closeTaskAssignList;
    }

    public void setCreateBy(int createBy) {
        this.createBy = createBy;
    }

    public void setContactInfo(CaseDataObject contactInfo) {
        this.contactInfo = contactInfo;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
