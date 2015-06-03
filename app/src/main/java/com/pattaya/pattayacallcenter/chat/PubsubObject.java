package com.pattaya.pattayacallcenter.chat;

/**
 * Created by SWF on 5/7/2015.
 */
public class PubsubObject {
    String ownerImage;
    String title;
    String displayDate;
    String action;
    int caseId;
    int complainId;
    String ownerName;
    String to;


    public String getUsername() {
        return to;
    }

    public void setUsername(String to) {
        this.to = to;
    }

    public String getImage() {
        return ownerImage;
    }

    public void setImage(String image) {
        this.ownerImage = image;
    }

    public String getName() {
        return ownerName;
    }

    public void setName(String name) {
        this.ownerName = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }



    public int getComplainId() {
        return complainId;
    }

    public void setComplainId(int complainId) {
        this.complainId = complainId;
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }
}
