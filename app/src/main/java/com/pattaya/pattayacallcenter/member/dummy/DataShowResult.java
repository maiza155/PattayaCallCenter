package com.pattaya.pattayacallcenter.member.dummy;

/**
 * Created by SWF on 2/5/2015.
 */
public class DataShowResult {
    private String orgname;
    private String datetime;
    private String detail;
    private int pic;

    public DataShowResult(String orgname,String datetime,String detail, int pic) {
        // TODO Auto-generated constructor stub
        this.orgname = orgname;
        this.datetime = datetime;
        this.detail = detail;
        this.pic = pic;
    }

    //ใช้ส่งค่ากลับไป setText ของ TextView
    public String getOrgname() {
        return this.orgname;
    }
    public String getDatetime() {
        return this.datetime;
    }
    public String getDetail() {
        return this.detail;
    }

    public int getPic() {
        return this.pic;
    }


}