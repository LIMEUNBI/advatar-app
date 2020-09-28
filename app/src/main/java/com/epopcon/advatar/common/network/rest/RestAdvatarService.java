package com.epopcon.advatar.common.network.rest;

import com.epopcon.advatar.common.network.BaseService;
import com.epopcon.advatar.common.network.model.param.AppVersionParam;
import com.epopcon.advatar.common.network.model.param.BrandGoodsParam;
import com.epopcon.advatar.common.network.model.param.BrandParam;
import com.epopcon.advatar.common.network.model.param.CommonParam;
import com.epopcon.advatar.common.network.model.param.OnlineStoreProductParam;
import com.epopcon.advatar.common.network.model.param.OnlineStorePurchaseParam;
import com.epopcon.advatar.common.network.model.param.UserParam;
import com.epopcon.advatar.common.network.model.repo.AppVersionRepo;
import com.epopcon.advatar.common.network.model.repo.BrandGoodsRepo;
import com.epopcon.advatar.common.network.model.repo.BrandRepo;
import com.epopcon.advatar.common.network.model.repo.ExtraVersionRepo;
import com.epopcon.advatar.common.network.model.repo.OnlineStoreStatusRepo;
import com.epopcon.advatar.common.network.model.repo.ResultRepo;
import com.epopcon.advatar.common.network.model.repo.UserFindIdRepo;

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

        // --------------------------- common ---------------------------
        @POST("common/getAppVersion")
        Call<AppVersionRepo> getAppVersion(@Body AppVersionParam appVersionParam);

        @POST("common/getExtraVersion")
        Call<ExtraVersionRepo> getExtraVersion(@Body AppVersionParam appVersionParam);

        @POST("common/getOnlineStoreStatus")
        Call<List<OnlineStoreStatusRepo>> getOnlineStoreStatus(@Body CommonParam commonParam);

        // --------------------------- user ---------------------------
        @POST("user/userDuplicateCheck")
        Call<ResultRepo> userDuplicateCheck(@Body UserParam userParam);

        @POST("user/userJoin")
        Call<ResultRepo> userJoin(@Body UserParam userParam);

        @POST("user/userLogin")
        Call<ResultRepo> userLogin(@Body UserParam userParam);

        @POST("user/userSNSLogin")
        Call<ResultRepo> userSNSLogin(@Body UserParam userParam);

        @POST("user/userFindId")
        Call<UserFindIdRepo> userFindId(@Body UserParam userParam);

        @POST("user/userFindPw")
        Call<ResultRepo> userFindPw(@Body UserParam userParam);

        @POST("user/userUpdatePw")
        Call<ResultRepo> userUpdatePw(@Body UserParam userParam);

        @POST("user/userFavoriteBrands")
        Call<ResultRepo> userFavoriteBrands(@Body UserParam userParam);

        // --------------------------- brand ---------------------------
        @POST("brand/getBrandList")
        Call<List<BrandRepo>> getBrandList(@Body BrandParam brandParam);

        @POST("brand/getBrandGoodsList")
        Call<List<BrandGoodsRepo>> getBrandGoodsList(@Body BrandGoodsParam brandGoodsParam);

        // --------------------------- online ---------------------------
        @POST("online/onlineStorePurchaseList")
        Call<ResultRepo> onlineStorePurchaseList(@Body OnlineStorePurchaseParam onlineStorePurchaseParam);

        @POST("online/onlineStoreProductList")
        Call<ResultRepo> onlineStoreProductList(@Body OnlineStoreProductParam onlineStoreProductParam);

    }
}
