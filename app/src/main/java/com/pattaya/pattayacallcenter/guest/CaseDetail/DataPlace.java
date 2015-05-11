package com.pattaya.pattayacallcenter.guest.CaseDetail;

import java.io.Serializable;

/**
 * Created by SWF on 2/9/2015.
 */
public class DataPlace implements Serializable {
    String number;
    String soi;
    String street;
    String moo;
    String location;

    public String toStringData(){
        String data = "บ้านเลขที่ "+ number +" "+soi+" "+street+" "+moo+" "+detail+" ";
        return data;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSoi() {
        return soi;
    }

    public void setSoi(String soi) {
        this.soi = soi;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getMoo() {
        return moo;
    }

    public void setMoo(String moo) {
        this.moo = moo;
    }

    String detail;

    public DataPlace() {
    }
}
