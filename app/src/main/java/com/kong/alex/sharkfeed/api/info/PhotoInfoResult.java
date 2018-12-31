package com.kong.alex.sharkfeed.api.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoInfoResult {

    @SerializedName("photo")
    @Expose
    private PhotoInfo photoInfo;
    @SerializedName("stat")
    @Expose
    private String stat;

    public PhotoInfo getPhotoInfo() {
        return photoInfo;
    }

    public void setPhotoInfo(PhotoInfo photoInfo) {
        this.photoInfo = photoInfo;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }
}
