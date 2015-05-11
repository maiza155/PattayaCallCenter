package com.pattaya.pattayacallcenter.Data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SWF on 2/26/2015.
 */
public class Users implements Parcelable {

    public static final int TYPE_GROUP = 1;
    public static final int TYPE_FRIEND = 0;
    public static final int TYPE_NOT_FRIEND = 5;

    String name;
    String jid;
    String pic;
    String organize;
    String email;
    Boolean favorite = false;
    int type;


    public Users(String jid, String name, String pic, int type) {
        this.name = name;
        this.jid = jid;
        this.pic = pic;
        this.type = type;
    }

    public Users(String name, String jid, String pic, String organize, int type) {
        this.name = name;
        this.jid = jid;
        this.pic = pic;
        this.organize = organize;
        this.type = type;
    }

    public Users(Parcel p) {
        String[] data = new String[6];
        p.readStringArray(data);
        this.jid = data[0];
        this.name = data[1];
        this.organize = data[2];
        this.pic = data[3];
        this.favorite = Boolean.parseBoolean(data[4]);
        this.type = Integer.parseInt(data[5]);


    }

    public Users() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOrganize() {
        return organize;
    }

    public void setOrganize(String organize) {
        this.organize = organize;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]
                {
                        this.jid,
                        this.name,
                        this.organize,
                        this.pic,
                        String.valueOf(this.favorite),
                        String.valueOf(this.type)

                });

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        public Users[] newArray(int size) {
            return new Users[size];
        }
    };
}
