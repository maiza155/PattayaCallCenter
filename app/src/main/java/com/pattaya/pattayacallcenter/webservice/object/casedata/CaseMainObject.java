package com.pattaya.pattayacallcenter.webservice.object.casedata;

/**
 * Created by SWF on 3/30/2015.
 */
public class CaseMainObject {
    String complaintName;
    String complaintDateString;
    Boolean isActive = true;
    int createBy;
    String accessToken;
    String clientId;
    CaseDataObject contactInfo;
    int refCasesId;
    int complaintTypeString;
    int complaintId;
    int contactInfoId;
    ChaConObject channelContact = new ChaConObject();

    public int getContactInfoId() {
        return contactInfoId;
    }

    public void setContactInfoId(int contactInfoId) {
        this.contactInfoId = contactInfoId;
    }

    public int getComplaintTypeString() {
        return complaintTypeString;
    }

    public void setComplaintTypeString(int complaintTypeString) {
        this.complaintTypeString = complaintTypeString;
    }

    public int getCreateBy() {
        return createBy;
    }

    public void setCreateBy(int createBy) {
        this.createBy = createBy;
    }

    public int getRefCasesId() {
        return refCasesId;
    }

    public void setRefCasesId(int refCasesId) {
        this.refCasesId = refCasesId;
    }

    public String getComplaintName() {
        return complaintName;
    }

    public void setComplaintName(String complaintName) {
        this.complaintName = complaintName;
    }

    public String getComplaintDateString() {
        return complaintDateString;
    }

    public void setComplaintDateString(String complaintDateString) {
        this.complaintDateString = complaintDateString;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setCreatedBy(int createdBy) {
        this.createBy = createdBy;
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

    public CaseDataObject getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(CaseDataObject contactInfo) {
        this.contactInfo = contactInfo;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(int complaintId) {
        this.complaintId = complaintId;
    }
}
