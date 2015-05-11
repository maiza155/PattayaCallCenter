package com.pattaya.pattayacallcenter.Data;

/**
 * Created by SWF on 3/6/2015.
 */
public class LastMessageData {
    private Users user;
    private String message;
    private String time;
    private int count;
    //private int pic;

    public LastMessageData() {

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
