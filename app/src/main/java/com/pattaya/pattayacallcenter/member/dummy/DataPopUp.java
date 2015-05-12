package com.pattaya.pattayacallcenter.member.dummy;

/**
 * Created by SWF on 2/9/2015.
 */
public class DataPopUp {
    String image;
    String name;
    int position = 0;

    public DataPopUp(String image, String name) {
        this.image = image;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
