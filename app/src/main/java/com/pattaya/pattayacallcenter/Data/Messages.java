package com.pattaya.pattayacallcenter.Data;

/**
 * Created by SWF on 2/26/2015.
 */
public class Messages {

    String time;
    String message;
    String room;
    String sender;
    Integer error = 0;
    String senderImage;
    String senderName;


    public String toString() {
        return "<time=" + time + " message='" + message + "' room='" + room + "' sender='" + sender + "' >";
    }


    public Messages(String time, String message, String room, String sender) {

        this.time = time;
        this.message = message;
        this.room = room;
        this.sender = sender;
    }

    public Messages() {

    }


    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
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

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
