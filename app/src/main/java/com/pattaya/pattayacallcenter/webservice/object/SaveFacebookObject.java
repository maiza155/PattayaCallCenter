package com.pattaya.pattayacallcenter.webservice.object;

/**
 * Created by SWF on 4/26/2015.
 */
public class SaveFacebookObject {
    String username;
    String firstName;
    String lastName;
    String image;

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
