package com.pattaya.pattayacallcenter.webservice.object.sticker;

/**
 * Created by SWF on 3/25/2015.
 */
public class StickerObject {
    int stickerSettingId;
    String stickerImage;

    @Override
    public String toString() {
        return "StickerObject{" +
                "stickerSettingId=" + stickerSettingId +
                ", stickerImage='" + stickerImage + '\'' +
                '}';
    }

    public int getStickerSettingId() {
        return stickerSettingId;
    }

    public void setStickerSettingId(int stickerSettingId) {
        this.stickerSettingId = stickerSettingId;
    }

    public String getStickerImage() {
        return stickerImage;
    }

    public void setStickerImage(String stickerImage) {
        this.stickerImage = stickerImage;
    }
}
