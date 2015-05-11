package com.pattaya.pattayacallcenter.chat.jsonobject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SWF on 3/14/2015.
 */
public class Property {
    List<MapProperty> property = new ArrayList<>();

    public Property(List<MapProperty> property) {
        this.property = property;
    }

    public void setProperty(List<MapProperty> property) {
        this.property = property;
    }
}
