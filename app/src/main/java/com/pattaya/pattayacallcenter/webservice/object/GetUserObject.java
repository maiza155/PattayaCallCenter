package com.pattaya.pattayacallcenter.webservice.object;

/**
 * Created by SWF on 3/13/2015.
 */
public class GetUserObject {
    int userId;
    String jid;
    String accessToken;
    String clientId;

    public GetUserObject(int userId, String accessToken, String clientId) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.clientId = clientId;
    }

    public GetUserObject(String jid, String accessToken, String clientId) {
        this.jid = jid;
        this.accessToken = accessToken;
        this.clientId = clientId;
    }
}
