package com.pattaya.pattayacallcenter.chat;

import com.pattaya.pattayacallcenter.Data.Messages;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SWF on 3/3/2015.
 */
public class ChatHistoryData {

    private static ChatHistoryData chatHistoryData = null;

    String currentListPacket;
    HashMap<String, String> map = new HashMap<>();
    HashMap<String, String> lastMaps = new HashMap<>();
    HashMap<String, ArrayList<Messages>> room = new HashMap<>();


    public static ChatHistoryData getInstance() {
        if (chatHistoryData == null) {
            chatHistoryData = new ChatHistoryData();
        }
        return chatHistoryData;
    }


    public HashMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }

    public void addMap(String key, String value) {
        this.map.put(key, value);
    }

    public HashMap<String, ArrayList<Messages>> getRoom() {
        return room;
    }

    public void setRoom(HashMap<String, ArrayList<Messages>> room) {
        this.room = room;
    }

    public void addRoom(String key, ArrayList<Messages> value) {
        this.room.put(key, value);
    }

    public HashMap<String, String> getLastMaps() {
        return lastMaps;
    }

    public void setLastMaps(HashMap<String, String> lastMaps) {
        this.lastMaps = lastMaps;
    }

    public void addLastMap(String key, String value) {
        this.lastMaps.put(key, value);
    }

    public String getCurrentListPacket() {
        return currentListPacket;
    }

    public void setCurrentListPacket(String currentListPacket) {
        this.currentListPacket = currentListPacket;
    }
}


