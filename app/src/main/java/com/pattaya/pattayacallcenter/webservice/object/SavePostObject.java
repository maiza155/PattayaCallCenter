package com.pattaya.pattayacallcenter.webservice.object;

import java.util.List;

/**
 * Created by SWF on 3/23/2015.
 */
public class SavePostObject {
    String detail;
    int postById;
    List postImageList;
    String postType;


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

    public List getPostImageList() {
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
