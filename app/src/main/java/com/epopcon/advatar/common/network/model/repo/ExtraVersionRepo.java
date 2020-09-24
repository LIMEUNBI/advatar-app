package com.epopcon.advatar.common.network.model.repo;

import com.google.gson.annotations.SerializedName;

public class ExtraVersionRepo {

    public @SerializedName("extraVersion")
    int extraVersion;

    public @SerializedName("url")
    String url;

    public @SerializedName("md5")
    String md5;
}
