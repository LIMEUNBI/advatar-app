package com.epopcon.advatar.common.network.model.repo.online;

import com.google.gson.annotations.SerializedName;

public class OnlineSharedUrlRepo {

    public @SerializedName("siteName")
    String siteName;

    public @SerializedName("productName")
    String productName;

    public @SerializedName("productPrice")
    int productPrice;

    public @SerializedName("deliveryAmount")
    int deliveryAmount;

    public @SerializedName("productUrl")
    String productUrl;

    public @SerializedName("productImg")
    String productImg;

    public @SerializedName("dateTime")
    String dateTime;

}
