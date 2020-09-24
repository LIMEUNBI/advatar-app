package com.epopcon.advatar.common.model;

import com.epopcon.extra.online.model.OrderDetail;

public class OnlineBizDetail extends OrderDetail {

    private long companyId = -1;

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }
}
