package com.pattaya.pattayacallcenter.webservice.object.sticker;

import java.util.List;

/**
 * Created by SWF on 3/25/2015.
 */
public class StickerListObject {
    List<StickerObject> stickerSettingList;

    public List<StickerObject> getStickerSettingList() {
        return stickerSettingList;
    }

    public void setStickerSettingList(List<StickerObject> stickerSettingList) {
        this.stickerSettingList = stickerSettingList;
    }
}
