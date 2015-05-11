package com.pattaya.pattayacallcenter.member.dummy;

/**
 * Created by SWF on 2/9/2015.
 */
public class DataPopUp {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    int image;
   String name;


    public DataPopUp(int image, String name) {
        this.image = image;
        this.name = name;
    }
}
