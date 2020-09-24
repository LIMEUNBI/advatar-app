package com.epopcon.advatar.common.network.model.repo;

import com.google.gson.annotations.SerializedName;

public class OnlineStoreStatusRepo {

    public @SerializedName("storeId")
    int storeId;
    public @SerializedName("status")
    String status;
}
