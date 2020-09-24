package com.epopcon.advatar.common.model;

import com.epopcon.extra.online.model.ProductDetail;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OnlineProductInfo extends ProductDetail {

    private long id;
    private String orderNumber;
    private String storeName;
    private Integer orderDate;
    private int totalAmount;
    private int refundAmount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreName() {
        return this.storeName;
    }

    public void setOrderDate(int orderDate) {
        this.orderDate = orderDate;
    }

    public int getOrderDate() {
        return this.orderDate;
    }

    public String getOrderDate(String format) {
        String strOrderDate = null;
        try {
            strOrderDate = new SimpleDateFormat(format, Locale.KOREAN).format(new SimpleDateFormat("yyyyMMdd").parse(this.orderDate.toString()));
        } catch (Exception e) {
            strOrderDate = this.orderDate.toString();
        }
        return strOrderDate;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalAmount() {
        return this.totalAmount;
    }

    public void setRefundAmount(int refundAmount) {
        this.refundAmount = refundAmount;
    }

    public int getRefundAmount() {
        return this.refundAmount;
    }
}
