package com.pattaya.pattayacallcenter.guest.CaseDetail;

/**
 * Created by SWF on 1/27/2015.
 */
public class DataCaseDetail {

    public DataCaseDetail() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getDetailCase() {
        return detailCase;
    }

    public void setDetailCase(String detailCase) {
        this.detailCase = detailCase;
    }

    public DataPlace getDataPlace() {
        return dataPlace;
    }

    public void setDataPlace(DataPlace dataPlace) {
        this.dataPlace = dataPlace;
    }

    DataPlace dataPlace;
    String place;
    Boolean state;
    String phone;
    String detailCase;
    String name;
    String image;

}
