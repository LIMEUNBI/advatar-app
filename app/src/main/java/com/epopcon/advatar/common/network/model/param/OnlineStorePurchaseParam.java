package com.epopcon.advatar.common.network.model.param;

public class OnlineStorePurchaseParam extends CommonParam{

    public String userId;
    public String storeName;
    public String orderNumber;
    public String orderDate;
    public long orderDateTime;
    public Integer totalAmount;
    public Integer payAmount;
    public Integer refundAmount;
    public String cancelYn;
    public String discountDetail;
    public Integer deliveryCost;
}
