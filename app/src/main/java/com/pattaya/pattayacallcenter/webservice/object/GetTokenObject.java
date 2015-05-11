package com.pattaya.pattayacallcenter.webservice.object;

/**
 * Created by SWF on 3/13/2015.
 */
public class GetTokenObject {
    String clientId;

    public GetTokenObject(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }
}
