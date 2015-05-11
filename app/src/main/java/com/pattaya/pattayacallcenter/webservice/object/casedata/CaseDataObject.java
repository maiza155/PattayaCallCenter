package com.pattaya.pattayacallcenter.webservice.object.casedata;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SWF on 3/30/2015.
 */
public class CaseDataObject implements Parcelable {
    String houseNumber;
    String village;
    String soi;
    String road;
    String nameContact;
    int casesId;
    String telephone;
    Boolean isAnonymousString;
    String moreInformation;
    String address;
    String district;
    String amphur;
    String province;
    String postCode;
    String email;


    List<ImageObject> infoImageList = new ArrayList<>();

    @Override
    public String toString() {
        return " " + houseNumber
                + " " + village
                + " " + soi
                + " " + road;
    }

    public CaseDataObject() {
    }

    public CaseDataObject(Parcel p) {
        this();
        String[] data = new String[8];
        List<ImageObject> infoImageList = new ArrayList<>();
        p.readStringArray(data);
        this.houseNumber = data[0];
        this.village = data[1];
        this.soi = data[2];
        this.road = data[3];
        this.nameContact = data[4];
        this.telephone = data[5];
        this.isAnonymousString = Boolean.parseBoolean(data[6]);
        this.moreInformation = data[7];

        p.readTypedList(infoImageList, ImageObject.CREATOR);
    }


    public int getCasesId() {
        return casesId;
    }

    public void setCasesId(int casesId) {
        this.casesId = casesId;
    }

    public List<ImageObject> getInfoImageList() {
        return infoImageList;
    }

    public void setInfoImageList(List infoImageList) {
        this.infoImageList = infoImageList;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getSoi() {
        return soi;
    }

    public void setSoi(String soi) {
        this.soi = soi;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getNameContact() {
        return nameContact;
    }

    public void setNameContact(String nameContact) {
        this.nameContact = nameContact;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Boolean getIsAnonymousString() {
        return isAnonymousString;
    }

    public void setIsAnonymousString(Boolean isAnonymousString) {
        this.isAnonymousString = isAnonymousString;
    }

    public String getMoreInformation() {
        return moreInformation;
    }

    public void setMoreInformation(String moreInformation) {
        this.moreInformation = moreInformation;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setAmphur(String amphur) {
        this.amphur = amphur;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.houseNumber,
                this.village,
                this.soi,
                this.road,
                this.nameContact,
                this.telephone,
                String.valueOf(this.isAnonymousString),
                this.moreInformation,

        });
        dest.writeTypedList(infoImageList);

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CaseDataObject createFromParcel(Parcel in) {
            return new CaseDataObject(in);
        }

        public CaseDataObject[] newArray(int size) {
            return new CaseDataObject[size];
        }
    };
}
