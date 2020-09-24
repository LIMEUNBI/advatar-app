package com.epopcon.advatar.common.network.model.repo;

import com.google.gson.annotations.SerializedName;

public class ContentsRepo {

    public @SerializedName("contentsNum")
    long contentsNum;
    public @SerializedName("contentsImg")
    String contentsImg;
    public @SerializedName("contentsText")
    String contentsText;

    public String contentsUrl;

    public long getContentsNum() {
        return contentsNum;
    }

    public void setContentsNum(long contentsNum) {
        this.contentsNum = contentsNum;
    }

    public String getContentsImg() {
        return contentsImg;
    }

    public void setContentsImg(String contentsImg) {
        this.contentsImg = contentsImg;
    }

    public String getContentsText() {
        return contentsText;
    }

    public void setContentsText(String contentsText) {
        this.contentsText = contentsText;
    }

    public String getContentsUrl() {
        return contentsUrl;
    }

    public void setContentUrl(String contentsUrl) {
        this.contentsUrl = contentsUrl;
    }
}
