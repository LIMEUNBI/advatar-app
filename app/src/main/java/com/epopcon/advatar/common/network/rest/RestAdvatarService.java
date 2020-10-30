package com.epopcon.advatar.common.network.rest;

import com.epopcon.advatar.common.network.BaseService;
import com.epopcon.advatar.common.network.model.param.brand.BrandContentsParam;
import com.epopcon.advatar.common.network.model.param.common.AppVersionParam;
import com.epopcon.advatar.common.network.model.param.brand.BrandGoodsParam;
import com.epopcon.advatar.common.network.model.param.brand.BrandParam;
import com.epopcon.advatar.common.network.model.param.CommonParam;
import com.epopcon.advatar.common.network.model.param.online.OnlinePickProductParam;
import com.epopcon.advatar.common.network.model.param.online.OnlineStoreCartParam;
import com.epopcon.advatar.common.network.model.param.online.OnlineStoreProductParam;
import com.epopcon.advatar.common.network.model.param.online.OnlineStorePurchaseParam;
import com.epopcon.advatar.common.network.model.param.user.AdminParam;
import com.epopcon.advatar.common.network.model.param.user.UserParam;
import com.epopcon.advatar.common.network.model.repo.brand.BrandContentsRepo;
import com.epopcon.advatar.common.network.model.repo.common.AppVersionRepo;
import com.epopcon.advatar.common.network.model.repo.brand.BrandGoodsRepo;
import com.epopcon.advatar.common.network.model.repo.brand.BrandRepo;
import com.epopcon.advatar.common.network.model.repo.common.ExtraVersionRepo;
import com.epopcon.advatar.common.network.model.repo.common.OnlineStoreStatusRepo;
import com.epopcon.advatar.common.network.model.repo.ResultRepo;
import com.epopcon.advatar.common.network.model.repo.online.OnlinePickProductRepo;
import com.epopcon.advatar.common.network.model.repo.user.UserFindIdRepo;
import com.epopcon.advatar.common.network.model.repo.user.UserLoginRepo;

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
        Call<UserLoginRepo> userLogin(@Body UserParam userParam);

        @POST("user/adminLogin")
        Call<ResultRepo> adminLogin(@Body AdminParam adminParam);

        @POST("user/userSNSLogin")
        Call<ResultRepo> userSNSLogin(@Body UserParam userParam);

        @POST("user/userInfoModify")
        Call<ResultRepo> userInfoModify(@Body UserParam userParam);

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

        @POST("brand/getBrandContentsList")
        Call<List<BrandContentsRepo>> getBrandContentsList(@Body BrandContentsParam brandContentsParam);

        // --------------------------- online ---------------------------
        @POST("online/onlineStorePurchaseList")
        Call<ResultRepo> onlineStorePurchaseList(@Body OnlineStorePurchaseParam onlineStorePurchaseParam);

        @POST("online/onlineStoreProductList")
        Call<ResultRepo> onlineStoreProductList(@Body OnlineStoreProductParam onlineStoreProductParam);

        @POST("online/onlineStoreCartList")
        Call<ResultRepo> onlineStoreCartList(@Body OnlineStoreCartParam onlineStoreCartParam);

        @POST("online/onlinePickProduct")
        Call<ResultRepo> onlinePickProduct(@Body OnlinePickProductParam onlinePickProductParam);

        @POST("online/onlinePickCancel")
        Call<ResultRepo> onlinePickCancel(@Body OnlinePickProductParam onlinePickProductParam);

        @POST("online/getOnlinePickProductList")
        Call<List<OnlinePickProductRepo>> getOnlinePickProductList(@Body OnlinePickProductParam onlinePickProductParam);
    }
}
