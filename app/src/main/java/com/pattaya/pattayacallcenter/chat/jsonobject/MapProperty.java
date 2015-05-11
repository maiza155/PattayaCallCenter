package com.pattaya.pattayacallcenter.chat.jsonobject;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SWF on 3/16/2015.
 */
public class MapProperty {
    @SerializedName("@key")
    String key;
    @SerializedName("@value")
    String value;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
