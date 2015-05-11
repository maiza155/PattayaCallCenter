package com.pattaya.pattayacallcenter.webservice.object.upload;

/**
 * Created by SWF on 3/23/2015.
 */
public class GetPostObject {
    int itemPerPage = 10;
    int pageNo;

    public int getItemPerPage() {
        return itemPerPage;
    }

    public void setItemPerPage(int itemPerPage) {
        this.itemPerPage = itemPerPage;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
}
