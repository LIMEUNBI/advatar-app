package com.epopcon.advatar.common.network.model.param.brand;

import com.epopcon.advatar.common.network.model.param.CommonParam;

import java.util.List;

public class BrandGoodsParam extends CommonParam {

    public String userId;
    public List<String> brandCodes;
    public int collectDay;
    public int maxCount;
    public String orderBy;
}
