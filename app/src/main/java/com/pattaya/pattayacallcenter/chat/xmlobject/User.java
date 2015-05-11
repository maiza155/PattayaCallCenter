package com.pattaya.pattayacallcenter.chat.xmlobject;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.Map;

/**
 * Created by SWF on 3/12/2015.
 */
@Root(strict = false)
public class User {
    @Element
    String username;

    @Element
    String name;

    @Element(required = false)
    String email;


    @ElementMap(name = "properties", key = "key", value = "value", attribute = true)
    public Map<String, String> property;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getProperty() {
        return property;
    }

    public void setProperty(Map<String, String> property) {
        this.property = property;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
