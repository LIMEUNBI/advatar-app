package com.epopcon.advatar.common.network.model.repo.common;

import com.google.gson.annotations.SerializedName;

public class AppVersionRepo {

    public @SerializedName("appVersionName")
    String appVersionName;

    public @SerializedName("appVersionCode")
    int appVersionCode;

    public @SerializedName("updateType")
    String updateType;

}
