package com.epopcon.advatar.common.network.rest;

import com.epopcon.advatar.common.network.BaseService;
import com.epopcon.advatar.common.network.model.param.BrandGoodsParam;
import com.epopcon.advatar.common.network.model.param.BrandParam;
import com.epopcon.advatar.common.network.model.repo.BrandGoodsRepo;
import com.epopcon.advatar.common.network.model.repo.BrandRepo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class RestAdvatarService extends BaseService {

    public static ListAPI api(String host, int timeout) {
        return (ListAPI) retrofit(ListAPI.class, host, timeout);
    }

    public interface ListAPI {

        /**
         * ====================================================================
         *                           EUMS Protocol
         * ====================================================================
         */

        // --------------------------- brand ---------------------------
        @POST("brand/getBrandList")
        Call<List<BrandRepo>> getBrandList(@Body BrandParam brandParam);

        @POST("brand/getBrandGoodsList")
        Call<List<BrandGoodsRepo>> getBrandGoodsList(@Body BrandGoodsParam brandGoodsParam);

    }
}
