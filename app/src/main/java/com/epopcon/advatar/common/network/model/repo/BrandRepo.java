package com.epopcon.advatar.common.network.model.repo;

import com.google.gson.annotations.SerializedName;

public class BrandRepo {

    public @SerializedName("brandCode")
    String brandCode;
    public @SerializedName("brandName")
    String brandName;
    public @SerializedName("displayOrder")
    int displayOrder;

    boolean isMyBrandYn;

    public boolean isMyBrandYn() {
        return isMyBrandYn;
    }

    public void setMyBrandYn(boolean myBrandYn) {
        isMyBrandYn = myBrandYn;
    }

    public String getBrandcode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
