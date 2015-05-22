package com.pattaya.pattayacallcenter.webservice.object;

/**
 * Created by SWF on 5/22/2015.
 */
public class DeletePostObject {
    int postId;

    public DeletePostObject(int postId) {
        this.postId = postId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
