package com.pattaya.pattayacallcenter.webservice.object.casedata.listforward;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SWF on 4/18/2015.
 */
public class OrganizeObject implements Parcelable {
    String displayname;
    String orgName;
    int orgId;

    public OrganizeObject() {
    }

    public OrganizeObject(String displayname, String orgName, int orgId) {
        this.displayname = displayname;
        this.orgName = orgName;
        this.orgId = orgId;
    }

    public OrganizeObject(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);
        displayname = data[0];
        orgName = data[1];
        orgId = Integer.parseInt(data[2]);

    }

    public String getDisplayname() {
        return displayname;
    }

    public String getOrgName() {
        return orgName;
    }

    public int getOrgId() {
        return orgId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeStringArray(new String[]
                {
                        this.displayname,
                        this.orgName,
                        String.valueOf(this.orgId),


                });

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public OrganizeObject createFromParcel(Parcel in) {
            return new OrganizeObject(in);
        }

        public OrganizeObject[] newArray(int size) {
            return new OrganizeObject[size];
        }
    };


}
