package com.epopcon.advatar.common.network.model.repo.brand;

import com.google.gson.annotations.SerializedName;

public class BrandContentsRepo {

    public @SerializedName("brandCode")
    long brandCode;
    public @SerializedName("brandName")
    String brandName;
    public @SerializedName("contentsImg")
    String contentsImg;
    public @SerializedName("contentsTitle")
    String contentsTitle;
    public @SerializedName("contentsText")
    String contentsText;
    public @SerializedName("contentsUrl")
    String contentsUrl;
    public @SerializedName("adYn")
    int adYn;
}
