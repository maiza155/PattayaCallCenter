package com.pattaya.pattayacallcenter.member.dummy;

import java.io.Serializable;

/**
 * Created by SWF on 2/4/2015.
 */
public  class DataShowAddGroup implements Serializable {
    private String name;
    private String depart;
    private int pic;

    public DataShowAddGroup(String name,String depart, int pic) {
        // TODO Auto-generated constructor stub
        this.name = name;
        this.depart = depart;
        this.pic = pic;
    }

    //ใช้ส่งค่ากลับไป setText ของ TextView
    public String getName() {
        return this.name;
    }
    public String getDepart() {
        return this.depart;
    }


    public int getPic() {
        return this.pic;
    }


}