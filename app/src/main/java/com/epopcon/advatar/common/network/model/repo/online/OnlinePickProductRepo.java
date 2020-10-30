package com.epopcon.advatar.common.network.model.repo.online;

import com.google.gson.annotations.SerializedName;

public class OnlinePickProductRepo {

    public @SerializedName("siteName")
    String siteName;

    public @SerializedName("productName")
    String productName;

    public @SerializedName("productPrice")
    int productPrice;

    public @SerializedName("deliveryAmount")
    int deliveryAmount;

    public @SerializedName("collectionType")
    String collectionType;

    public @SerializedName("productUrl")
    String productUrl;

    public @SerializedName("productImg")
    String productImg;

    public @SerializedName("dateTime")
    String dateTime;

}
