package com.pattaya.pattayacallcenter.webservice.object;

/**
 * Created by SWF on 3/13/2015.
 */
public class UpdateResult {
    Boolean result = false;
    int primaryKeyId;
    int casesId;

    public int getCasesId() {
        return casesId;
    }

    public void setCasesId(int casesId) {
        this.casesId = casesId;
    }

    public int getPrimaryKeyId() {
        return primaryKeyId;
    }

    public Boolean getResult() {
        return result;
    }
}
