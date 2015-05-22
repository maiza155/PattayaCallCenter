package com.pattaya.pattayacallcenter.webservice.object;

import java.util.List;

/**
 * Created by SWF on 3/23/2015.
 */
public class SavePostObject {
    String detail;
    int postById;
    List<String> postImageList;
    String postType;
    int postId;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getPostById() {
        return postById;
    }

    public void setPostById(int postById) {
        this.postById = postById;
    }

    public List<String> getPostImageList() {
        return postImageList;
    }

    public void setPostImageList(List postImageList) {
        this.postImageList = postImageList;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }
}
