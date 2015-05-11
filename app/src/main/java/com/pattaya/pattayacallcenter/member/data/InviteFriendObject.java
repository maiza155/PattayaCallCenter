package com.pattaya.pattayacallcenter.member.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SWF on 4/7/2015.
 */
public class InviteFriendObject implements Parcelable {
    String jid;
    String name;
    String image;

    public InviteFriendObject(String jid, String name, String image) {
        this.jid = jid;
        this.name = name;
        this.image = image;
    }

    public InviteFriendObject() {
    }

    public InviteFriendObject(Parcel p) {
        jid = p.readString();
        name = p.readString();
        image = p.readString();

    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jid);
        dest.writeString(name);
        dest.writeString(image);
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public InviteFriendObject createFromParcel(Parcel in) {
            return new InviteFriendObject(in);
        }

        public InviteFriendObject[] newArray(int size) {
            return new InviteFriendObject[size];
        }
    };
}
