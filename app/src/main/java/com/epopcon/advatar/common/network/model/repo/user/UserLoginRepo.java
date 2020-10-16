package com.epopcon.advatar.common.network.model.repo.user;

import com.google.gson.annotations.SerializedName;

public class UserLoginRepo {

    public @SerializedName("result")
    String result;

    public @SerializedName("userName")
    String userName;

    public @SerializedName("userBirth")
    String userBirth;

    public @SerializedName("userGender")
    String userGender;

    public @SerializedName("userPhone")
    String userPhone;

    public @SerializedName("userAddress")
    String userAddress;

    public @SerializedName("userEmail")
    String userEmail;

    public @SerializedName("userBrandCodes")
    String userBrandCodes;

    public @SerializedName("userBrandNames")
    String userBrandNames;

}

