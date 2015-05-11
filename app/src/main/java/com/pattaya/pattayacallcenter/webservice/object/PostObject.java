package com.pattaya.pattayacallcenter.webservice.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SWF on 3/23/2015.
 */
public class PostObject implements Parcelable {
    String type;
    int createdBy;
    int itemPerPage;
    int pageNo;
    String detail;
    int postById;
    String postByName;
    String postByUserImage;
    String postDateTime;
    int postId;
    List postImageList;
    String postType;


    @Override
    public String toString() {
        return "PostObject{" +
                "type='" + type + '\'' +
                ", createdBy=" + createdBy +
                ", itemPerPage=" + itemPerPage +
                ", pageNo=" + pageNo +
                ", detail='" + detail + '\'' +
                ", postById=" + postById +
                ", postByName='" + postByName + '\'' +
                ", postByUserImage='" + postByUserImage + '\'' +
                ", postDateTime='" + postDateTime + '\'' +
                ", postId=" + postId +
                ", postImageList=" + postImageList +
                ", postType='" + postType + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getItemPerPage() {
        return itemPerPage;
    }

    public void setItemPerPage(int itemPerPage) {
        this.itemPerPage = itemPerPage;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
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

    public String getPostByName() {
        return postByName;
    }

    public void setPostByName(String postByName) {
        this.postByName = postByName;
    }

    public String getPostByUserImage() {
        return postByUserImage;
    }

    public void setPostByUserImage(String postByUserImage) {
        this.postByUserImage = postByUserImage;
    }

    public String getPostDateTime() {
        return postDateTime;
    }

    public void setPostDateTime(String postDateTime) {
        this.postDateTime = postDateTime;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeInt(createdBy);
        dest.writeString(detail);
        dest.writeString(postByName);
        dest.writeString(postByUserImage);
        dest.writeString(postDateTime);
        dest.writeInt(postId);
        dest.writeList(this.postImageList);


    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PostObject createFromParcel(Parcel in) {
            return new PostObject(in);
        }

        public PostObject[] newArray(int size) {
            return new PostObject[size];
        }
    };


    public PostObject(Parcel p) {

        createdBy = p.readInt();
        detail = p.readString();
        postByName = p.readString();
        postByUserImage = p.readString();
        postDateTime = p.readString();
        postId = p.readInt();
        postImageList = new ArrayList();
        p.readList(postImageList, null);

    }
}
