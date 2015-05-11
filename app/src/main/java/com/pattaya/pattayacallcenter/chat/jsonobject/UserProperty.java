package com.pattaya.pattayacallcenter.chat.jsonobject;

/**
 * Created by SWF on 3/14/2015.
 */
public class UserProperty {
    String username;
    Property properties;
    String name;


    @Override
    public String toString() {
        return "objectuser{" +
                "username='" + username + '\'' +
                ", properties=" + properties +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProperties(Property properties) {
        this.properties = properties;
    }
}
