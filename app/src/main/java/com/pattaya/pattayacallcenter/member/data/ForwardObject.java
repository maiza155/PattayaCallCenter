package com.pattaya.pattayacallcenter.member.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SWF on 4/18/2015.
 */
public class ForwardObject implements Parcelable {
    int id;
    String name;
    String image;
    String jid;

    public ForwardObject() {
    }

    public ForwardObject(int id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public ForwardObject(int id, String name, String image, String jid) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.jid = jid;
    }

    public String getJid() {
        return jid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ForwardObject(Parcel in) {
        id = in.readInt();
        name = in.readString();
        image = in.readString();
        jid = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(jid);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ForwardObject createFromParcel(Parcel in) {
            return new ForwardObject(in);
        }

        public ForwardObject[] newArray(int size) {
            return new ForwardObject[size];
        }
    };
}
