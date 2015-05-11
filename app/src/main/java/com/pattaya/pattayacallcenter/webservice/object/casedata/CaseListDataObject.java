package com.pattaya.pattayacallcenter.webservice.object.casedata;

/**
 * Created by SWF on 3/30/2015.
 */
public class CaseListDataObject {
    String caseName;
    int casesId;
    int complaintId;
    String dateShort;
    Boolean isNew;
    int percentSuccess;

    public int getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(int complaintId) {
        this.complaintId = complaintId;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public int getCasesId() {
        return casesId;
    }

    public void setCasesId(int casesId) {
        this.casesId = casesId;
    }

    public String getDate() {
        return dateShort;
    }

    public void setDate(String date) {
        this.dateShort = date;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public int getPercentSuccess() {
        return percentSuccess;
    }

    public void setPercentSuccess(int percentSuccess) {
        this.percentSuccess = percentSuccess;
    }
}
