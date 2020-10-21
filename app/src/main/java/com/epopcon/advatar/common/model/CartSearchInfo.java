package com.epopcon.advatar.common.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CartSearchInfo implements Parcelable {

    private String productName;
    private String productUrl;
    private String productImg;
    private int productPrice;
    private String productCate;
    private String productDetail;
    private int productReviewCount;
    private int productPurchaseCount;
    private String productRegDt;
    private String sellerName;

    private String sellerStore1;
    private String sellerStore2;
    private String sellerStore3;
    private String sellerStore4;
    private String sellerStore5;

    private int sellerPrice1;
    private int sellerPrice2;
    private int sellerPrice3;
    private int sellerPrice4;
    private int sellerPrice5;

    private String sellerDelivery1;
    private String sellerDelivery2;
    private String sellerDelivery3;
    private String sellerDelivery4;
    private String sellerDelivery5;

    private String sellerUrl1;
    private String sellerUrl2;
    private String sellerUrl3;
    private String sellerUrl4;
    private String sellerUrl5;

    private String deliveryInfo;

    public CartSearchInfo() {}

    protected CartSearchInfo(Parcel in) {
        productName = in.readString();
        productUrl = in.readString();
        productImg = in.readString();
        productPrice = in.readInt();
        productCate = in.readString();
        productDetail = in.readString();
        productReviewCount = in.readInt();
        productPurchaseCount = in.readInt();
        productRegDt = in.readString();
        sellerName = in.readString();

        sellerStore1 = in.readString();
        sellerStore2= in.readString();
        sellerStore3 = in.readString();
        sellerStore4 = in.readString();
        sellerStore5 = in.readString();

        sellerPrice1 = in.readInt();
        sellerPrice2 = in.readInt();
        sellerPrice3 = in.readInt();
        sellerPrice4 = in.readInt();
        sellerPrice5 = in.readInt();

        sellerDelivery1 = in.readString();
        sellerDelivery2 = in.readString();
        sellerDelivery3 = in.readString();
        sellerDelivery4 = in.readString();
        sellerDelivery5 = in.readString();

        sellerUrl1 = in.readString();
        sellerUrl2 = in.readString();
        sellerUrl3 = in.readString();
        sellerUrl4 = in.readString();
        sellerUrl5 = in.readString();

        deliveryInfo = in.readString();
    }

    public static final Parcelable.Creator<CartSearchInfo> CREATOR = new Parcelable.Creator<CartSearchInfo>() {
        @Override
        public CartSearchInfo createFromParcel(Parcel in) {
            return new CartSearchInfo(in);
        }

        @Override
        public CartSearchInfo[] newArray(int size) {
            return new CartSearchInfo[size];
        }
    };

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductCate() {
        return productCate;
    }

    public void setProductCate(String productCate) {
        this.productCate = productCate;
    }

    public String getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(String productDetail) {
        this.productDetail = productDetail;
    }

    public int getProductReviewCount() {
        return productReviewCount;
    }

    public void setProductReviewCount(int productReviewCount) {
        this.productReviewCount = productReviewCount;
    }

    public int getProductPurchaseCount() {
        return productPurchaseCount;
    }

    public void setProductPurchaseCount(int productPurchaseCount) {
        this.productPurchaseCount = productPurchaseCount;
    }

    public String getProductRegDt() {
        return productRegDt;
    }

    public void setProductRegDt(String productRegDt) {
        this.productRegDt = productRegDt;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerStore1() {
        return sellerStore1;
    }

    public void setSellerStore1(String sellerStore1) {
        this.sellerStore1 = sellerStore1;
    }

    public String getSellerStore2() {
        return sellerStore2;
    }

    public void setSellerStore2(String sellerStore2) {
        this.sellerStore2 = sellerStore2;
    }

    public String getSellerStore3() {
        return sellerStore3;
    }

    public void setSellerStore3(String sellerStore3) {
        this.sellerStore3 = sellerStore3;
    }

    public String getSellerStore4() {
        return sellerStore4;
    }

    public void setSellerStore4(String sellerStore4) {
        this.sellerStore4 = sellerStore4;
    }

    public String getSellerStore5() {
        return sellerStore5;
    }

    public void setSellerStore5(String sellerStore5) {
        this.sellerStore5 = sellerStore5;
    }

    public int getSellerPrice1() {
        return sellerPrice1;
    }

    public void setSellerPrice1(int sellerPrice1) {
        this.sellerPrice1 = sellerPrice1;
    }

    public int getSellerPrice2() {
        return sellerPrice2;
    }

    public void setSellerPrice2(int sellerPrice2) {
        this.sellerPrice2 = sellerPrice2;
    }

    public int getSellerPrice3() {
        return sellerPrice3;
    }

    public void setSellerPrice3(int sellerPrice3) {
        this.sellerPrice3 = sellerPrice3;
    }

    public int getSellerPrice4() {
        return sellerPrice4;
    }

    public void setSellerPrice4(int sellerPrice4) {
        this.sellerPrice4 = sellerPrice4;
    }

    public int getSellerPrice5() {
        return sellerPrice5;
    }

    public void setSellerPrice5(int sellerPrice5) {
        this.sellerPrice5 = sellerPrice5;
    }

    public String getSellerDelivery1() {
        return sellerDelivery1;
    }

    public void setSellerDelivery1(String sellerDelivery1) {
        this.sellerDelivery1 = sellerDelivery1;
    }

    public String getSellerDelivery2() {
        return sellerDelivery2;
    }

    public void setSellerDelivery2(String sellerDelivery2) {
        this.sellerDelivery2 = sellerDelivery2;
    }

    public String getSellerDelivery3() {
        return sellerDelivery3;
    }

    public void setSellerDelivery3(String sellerDelivery3) {
        this.sellerDelivery3 = sellerDelivery3;
    }

    public String getSellerDelivery4() {
        return sellerDelivery4;
    }

    public void setSellerDelivery4(String sellerDelivery4) {
        this.sellerDelivery4 = sellerDelivery4;
    }

    public String getSellerDelivery5() {
        return sellerDelivery5;
    }

    public void setSellerDelivery5(String sellerDelivery5) {
        this.sellerDelivery5 = sellerDelivery5;
    }

    public String getSellerUrl1() {
        return sellerUrl1;
    }

    public void setSellerUrl1(String sellerUrl1) {
        this.sellerUrl1 = sellerUrl1;
    }

    public String getSellerUrl2() {
        return sellerUrl2;
    }

    public void setSellerUrl2(String sellerUrl2) {
        this.sellerUrl2 = sellerUrl2;
    }

    public String getSellerUrl3() {
        return sellerUrl3;
    }

    public void setSellerUrl3(String sellerUrl3) {
        this.sellerUrl3 = sellerUrl3;
    }

    public String getSellerUrl4() {
        return sellerUrl4;
    }

    public void setSellerUrl4(String sellerUrl4) {
        this.sellerUrl4 = sellerUrl4;
    }

    public String getSellerUrl5() {
        return sellerUrl5;
    }

    public void setSellerUrl5(String sellerUrl5) {
        this.sellerUrl5 = sellerUrl5;
    }

    public String getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(String deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(productName);
        parcel.writeString(productUrl);
        parcel.writeString(productImg);
        parcel.writeInt(productPrice);
        parcel.writeString(productCate);
        parcel.writeString(productDetail);
        parcel.writeInt(productReviewCount);
        parcel.writeInt(productPurchaseCount);
        parcel.writeString(productRegDt);
        parcel.writeString(sellerName);

        parcel.writeString(sellerStore1);
        parcel.writeString(sellerStore2);
        parcel.writeString(sellerStore3);
        parcel.writeString(sellerStore4);
        parcel.writeString(sellerStore5);

        parcel.writeInt(sellerPrice1);
        parcel.writeInt(sellerPrice2);
        parcel.writeInt(sellerPrice3);
        parcel.writeInt(sellerPrice4);
        parcel.writeInt(sellerPrice5);

        parcel.writeString(sellerDelivery1);
        parcel.writeString(sellerDelivery2);
        parcel.writeString(sellerDelivery3);
        parcel.writeString(sellerDelivery4);
        parcel.writeString(sellerDelivery5);

        parcel.writeString(sellerUrl1);
        parcel.writeString(sellerUrl2);
        parcel.writeString(sellerUrl3);
        parcel.writeString(sellerUrl4);
        parcel.writeString(sellerUrl5);

        parcel.writeString(deliveryInfo);
    }
}
