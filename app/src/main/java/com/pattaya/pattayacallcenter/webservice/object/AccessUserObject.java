package com.pattaya.pattayacallcenter.webservice.object;

/**
 * Created by SWF on 3/13/2015.
 */
public class AccessUserObject {
    String userId;
    String firstname;
    String lastname;
    String mobile;
    String idCard;
    String address;
    String province;
    String amphur;
    String district;
    String postCode;
    String email;
    String userImage;
    String accessToken;
    String clientId;
    String orgName;
    String displayName;


    @Override
    public String toString() {
        return "AccessUserObject{" +
                "userId=" + userId +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", idCard='" + idCard + '\'' +
                ", address='" + address + '\'' +
                ", province='" + province + '\'' +
                ", amphur='" + amphur + '\'' +
                ", district='" + district + '\'' +
                ", postCode='" + postCode + '\'' +
                ", email='" + email + '\'' +
                ", userImage='" + userImage + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getMobile() {
        return mobile;
    }

    public String getIdCard() {
        return idCard;
    }

    public String getAddress() {
        return address;
    }

    public String getProvince() {
        return province;
    }

    public String getAmphur() {
        return amphur;
    }

    public String getDistrict() {
        return district;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getEmail() {
        return email;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setAmphur(String amphur) {
        this.amphur = amphur;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
