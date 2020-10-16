package com.epopcon.advatar.common.network.model.repo.brand;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class BrandRepo implements Parcelable {

    public @SerializedName("brandCode")
    String brandCode;
    public @SerializedName("brandName")
    String brandName;
    public @SerializedName("displayOrder")
    int displayOrder;

    boolean isMyBrandYn;

    protected BrandRepo(Parcel in) {
        brandCode = in.readString();
        brandName = in.readString();
        displayOrder = in.readInt();
        isMyBrandYn = in.readByte() != 0;
    }

    public static final Creator<BrandRepo> CREATOR = new Creator<BrandRepo>() {
        @Override
        public BrandRepo createFromParcel(Parcel in) {
            return new BrandRepo(in);
        }

        @Override
        public BrandRepo[] newArray(int size) {
            return new BrandRepo[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(brandCode);
        parcel.writeString(brandName);
        parcel.writeInt(displayOrder);
        parcel.writeByte((byte) (isMyBrandYn ? 1 : 0));
    }
}
