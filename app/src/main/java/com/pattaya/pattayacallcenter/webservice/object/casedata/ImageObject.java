package com.pattaya.pattayacallcenter.webservice.object.casedata;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SWF on 3/30/2015.
 */
public class ImageObject implements Parcelable {
    String infoImage;

    public String getInfoImage() {

        return infoImage;
    }

    public void setInfoImage(String infoImage) {
        this.infoImage = infoImage;
    }


    public ImageObject() {
    }

    public ImageObject(Parcel p) {
        infoImage = p.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(infoImage);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ImageObject createFromParcel(Parcel in) {
            return new ImageObject(in);
        }

        public ImageObject[] newArray(int size) {
            return new ImageObject[size];
        }
    };
}
