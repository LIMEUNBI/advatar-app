package com.epopcon.advatar.common.network.rest;

import android.text.TextUtils;
import android.util.Log;

import com.epopcon.advatar.common.CommonLibrary;
import com.epopcon.advatar.common.network.RequestListener;
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
import com.epopcon.advatar.common.util.EncrypterUtil;
import com.epopcon.extra.common.utils.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestAdvatarProtocol {

    private final String TAG = RestAdvatarProtocol.class.getSimpleName();

    private static RestAdvatarProtocol instance;

    private String host;
    private int timeout;

    public static final int PROTOCOL_GET_APP_VERSION = 0x100;
    public static final int PROTOCOL_GET_EXTRA_VERSION = 0x101;

    public static final int PROTOCOL_USER_DUPLICATE_CHECK = 0x102;
    public static final int PROTOCOL_USER_JOIN = 0x103;
    public static final int PROTOCOL_USER_LOGIN = 0x104;

    public static final int PROTOCOL_USER_SNS_LOGIN = 0x105;
    public static final int PROTOCOL_USER_FIND_ID = 0x106;
    public static final int PROTOCOL_USER_FIND_PW = 0x107;
    public static final int PROTOCOL_USER_PW_UPDATE = 0x108;
    public static final int PROTOCOL_USER_FAVORITE_BRANDS = 0x109;

    public static final int PROTOCOL_GET_BRAND_LIST = 0x201;
    public static final int PROTOCOL_GET_BRAND_GOODS_LIST = 0x202;

    public static final int PROTOCOL_ONLINE_STORE_PURCHASE_LIST = 0x301;
    public static final int PROTOCOL_ONLINE_STORE_PRODUCT_LIST = 0x302;

    public static synchronized RestAdvatarProtocol getInstance() {
        if (instance == null) {
            synchronized (RestAdvatarProtocol.class) {
                if (instance == null) {
                    instance = new RestAdvatarProtocol();
                }
            }
        }
        return instance;
    }

    private RestAdvatarProtocol() {}

    public void setConnectionInfo(String host, int timeout) {
        Log.d(TAG, "Connection Info : " + host + ", " + timeout);
        this.host = host;
        this.timeout = timeout;
    }

    public void getAppVersion(final RequestListener requestListener) throws Exception {
        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final AppVersionParam appVersionParam = new AppVersionParam();
            appVersionParam.affiliateCode = CommonLibrary.getAffiliateCode();
            appVersionParam.licenseKey = licenseKey;
            appVersionParam.osType = "A";

            Callback<AppVersionRepo> callback = new Callback<AppVersionRepo>() {
                @Override
                public void onResponse(Call<AppVersionRepo> call, Response<AppVersionRepo> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        AppVersionRepo repo = response.body();
                        requestListener.onRequestSuccess(PROTOCOL_GET_APP_VERSION, repo);
                    }
                }

                @Override
                public void onFailure(Call<AppVersionRepo> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };
            RestAdvatarService.api(host, timeout).getAppVersion(appVersionParam).enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getExtraVersion(final RequestListener requestListener) throws Exception {
        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final AppVersionParam appVersionParam = new AppVersionParam();
            appVersionParam.affiliateCode = CommonLibrary.getAffiliateCode();
            appVersionParam.licenseKey = licenseKey;
            appVersionParam.osType = "A";

            Callback<ExtraVersionRepo> callback = new Callback<ExtraVersionRepo>() {
                @Override
                public void onResponse(Call<ExtraVersionRepo> call, Response<ExtraVersionRepo> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        ExtraVersionRepo repo = response.body();
                        requestListener.onRequestSuccess(PROTOCOL_GET_EXTRA_VERSION, repo);
                    }
                }

                @Override
                public void onFailure(Call<ExtraVersionRepo> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };
            RestAdvatarService.api(host, timeout).getExtraVersion(appVersionParam).enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getOnlineStoreStatus(final RequestListener requestListener) throws Exception {
        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final CommonParam commonParam = new CommonParam();
            commonParam.affiliateCode = CommonLibrary.getAffiliateCode();
            commonParam.licenseKey = licenseKey;

            Callback<List<OnlineStoreStatusRepo>> callback = new Callback<List<OnlineStoreStatusRepo>>() {
                @Override
                public void onResponse(Call<List<OnlineStoreStatusRepo>> call, Response<List<OnlineStoreStatusRepo>> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        List<OnlineStoreStatusRepo> repo = response.body();
                        requestListener.onRequestSuccess(PROTOCOL_GET_APP_VERSION, repo);
                    }
                }

                @Override
                public void onFailure(Call<List<OnlineStoreStatusRepo>> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };
            RestAdvatarService.api(host, timeout).getOnlineStoreStatus(commonParam).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userDuplicateCheck(String userId, final RequestListener requestListener) throws Exception {
        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final UserParam userParam = new UserParam();
            userParam.affiliateCode = CommonLibrary.getAffiliateCode();
            userParam.licenseKey = licenseKey;
            userParam.userId = userId;

            Callback<ResultRepo> callback = new Callback<ResultRepo>() {
                @Override
                public void onResponse(Call<ResultRepo> call, Response<ResultRepo> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        ResultRepo result = response.body();
                        requestListener.onRequestSuccess(PROTOCOL_USER_DUPLICATE_CHECK, result.result);
                    }
                }

                @Override
                public void onFailure(Call<ResultRepo> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };

            RestAdvatarService.api(host, timeout).userDuplicateCheck(userParam).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userJoin(String userId, String userPw, String deviceInfo, String fcmToken, String userName, String userBirth,
                         String userGender, String userPhone, String userAddress, String userEmail, final RequestListener requestListener) throws Exception {

        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final UserParam userParam = new UserParam();
            userParam.affiliateCode = CommonLibrary.getAffiliateCode();
            userParam.licenseKey = licenseKey;
            userParam.userId = userId;
            userParam.userPw = userPw;
            userParam.deviceInfo = deviceInfo;
            userParam.osType = "A";
            userParam.fcmToken = fcmToken;
            userParam.userName = userName;
            userParam.userBirth = userBirth;
            userParam.userGender = userGender;
            userParam.userPhone = userPhone;
            userParam.userAddress = userAddress;
            userParam.userEmail = userEmail;
            userParam.userBrands = "";

            Callback<ResultRepo> callback = new Callback<ResultRepo>() {
                @Override
                public void onResponse(Call<ResultRepo> call, Response<ResultRepo> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        ResultRepo result = response.body();
                        requestListener.onRequestSuccess(PROTOCOL_USER_JOIN, result.result);
                    }
                }

                @Override
                public void onFailure(Call<ResultRepo> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };

            RestAdvatarService.api(host, timeout).userJoin(userParam).enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userLogin(String userId, String userPw, String fcmToken, final RequestListener requestListener) throws Exception {
        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final UserParam userParam = new UserParam();
            userParam.affiliateCode = CommonLibrary.getAffiliateCode();
            userParam.licenseKey = licenseKey;
            userParam.userId = userId;
            userParam.userPw = userPw;
            userParam.fcmToken = fcmToken;

            Callback<ResultRepo> callback = new Callback<ResultRepo>() {
                @Override
                public void onResponse(Call<ResultRepo> call, Response<ResultRepo> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        ResultRepo result = response.body();
                        requestListener.onRequestSuccess(PROTOCOL_USER_LOGIN, result.result);
                    } else {
                        requestListener.onRequestFailure(new Throwable());
                    }
                }

                @Override
                public void onFailure(Call<ResultRepo> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };

            RestAdvatarService.api(host, timeout).userLogin(userParam).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userSNSLogin(String userId, String userEmail, String userName, String fcmToken, final RequestListener requestListener) throws Exception {
        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final UserParam userParam = new UserParam();
            userParam.affiliateCode = CommonLibrary.getAffiliateCode();
            userParam.licenseKey = licenseKey;
            userParam.userId = userId;
            userParam.userEmail = userEmail;
            userParam.userName = userName;
            userParam.fcmToken = fcmToken;

            Callback<ResultRepo> callback = new Callback<ResultRepo>() {
                @Override
                public void onResponse(Call<ResultRepo> call, Response<ResultRepo> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        ResultRepo result = response.body();
                        requestListener.onRequestSuccess(PROTOCOL_USER_SNS_LOGIN, result.result);
                    } else {
                        requestListener.onRequestFailure(new Throwable());
                    }
                }

                @Override
                public void onFailure(Call<ResultRepo> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };

            RestAdvatarService.api(host, timeout).userSNSLogin(userParam).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userFindId(String userEmail, String userName, final RequestListener requestListener) throws Exception {
        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final UserParam userParam = new UserParam();
            userParam.affiliateCode = CommonLibrary.getAffiliateCode();
            userParam.licenseKey = licenseKey;
            userParam.userEmail = userEmail;
            userParam.userName = userName;

            Callback<UserFindIdRepo> callback = new Callback<UserFindIdRepo>() {
                @Override
                public void onResponse(Call<UserFindIdRepo> call, Response<UserFindIdRepo> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        UserFindIdRepo result = response.body();
                        requestListener.onRequestSuccess(PROTOCOL_USER_FIND_ID, result.result);
                    } else {
                        requestListener.onRequestFailure(new Throwable());
                    }
                }

                @Override
                public void onFailure(Call<UserFindIdRepo> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };

            RestAdvatarService.api(host, timeout).userFindId(userParam).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userFindPw(String userId, String userName, String userPhone, final RequestListener requestListener) throws Exception {
        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final UserParam userParam = new UserParam();
            userParam.affiliateCode = CommonLibrary.getAffiliateCode();
            userParam.licenseKey = licenseKey;
            userParam.userId = userId;
            userParam.userName = userName;
            userParam.userPhone = userPhone;

            Callback<ResultRepo> callback = new Callback<ResultRepo>() {
                @Override
                public void onResponse(Call<ResultRepo> call, Response<ResultRepo> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        ResultRepo result = response.body();
                        requestListener.onRequestSuccess(PROTOCOL_USER_FIND_PW, result.result);
                    } else {
                        requestListener.onRequestFailure(new Throwable());
                    }
                }

                @Override
                public void onFailure(Call<ResultRepo> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };

            RestAdvatarService.api(host, timeout).userFindPw(userParam).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userUpdatePw(String userId, String userPw, final RequestListener requestListener) throws Exception {
        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final UserParam userParam = new UserParam();
            userParam.affiliateCode = CommonLibrary.getAffiliateCode();
            userParam.licenseKey = licenseKey;
            userParam.userId = userId;
            userParam.userPw = userPw;

            Callback<ResultRepo> callback = new Callback<ResultRepo>() {
                @Override
                public void onResponse(Call<ResultRepo> call, Response<ResultRepo> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        ResultRepo result = response.body();
                        requestListener.onRequestSuccess(PROTOCOL_USER_PW_UPDATE, result.result);
                    } else {
                        requestListener.onRequestFailure(new Throwable());
                    }
                }

                @Override
                public void onFailure(Call<ResultRepo> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };

            RestAdvatarService.api(host, timeout).userUpdatePw(userParam).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userFavoriteBrands(String userId, String userBrands, final RequestListener requestListener) throws Exception {
        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final UserParam userParam = new UserParam();
            userParam.affiliateCode = CommonLibrary.getAffiliateCode();
            userParam.licenseKey = licenseKey;
            userParam.userId = userId;
            userParam.userBrands = userBrands;

            Callback<ResultRepo> callBack = new Callback<ResultRepo>() {
                @Override
                public void onResponse(Call<ResultRepo> call, Response<ResultRepo> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        ResultRepo result = response.body();
                        requestListener.onRequestSuccess(PROTOCOL_USER_FAVORITE_BRANDS, result.result);
                    } else {
                        requestListener.onRequestFailure(new Throwable());
                    }
                }

                @Override
                public void onFailure(Call<ResultRepo> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };

            RestAdvatarService.api(host, timeout).userFavoriteBrands(userParam).enqueue(callBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBrandList(final int maxCount, final RequestListener requestListener) throws Exception {

        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final BrandParam brandParam = new BrandParam();
            brandParam.licenseKey = licenseKey;
            brandParam.affiliateCode = CommonLibrary.getAffiliateCode();
            brandParam.maxCount = maxCount;

            Callback<List<BrandRepo>> callback = new Callback<List<BrandRepo>>() {
                @Override
                public void onResponse(Call<List<BrandRepo>> call, Response<List<BrandRepo>> response) {
                    if (response != null && response.isSuccessful() && response.body() != null)
                    {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        List<BrandRepo> brandList =  (List<BrandRepo>) response.body();
                        requestListener.onRequestSuccess(PROTOCOL_GET_BRAND_LIST, brandList);
                    }
                }

                @Override
                public void onFailure(Call<List<BrandRepo>> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };

            RestAdvatarService.api(host, timeout).getBrandList(brandParam).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBrandGoodsList(final List<String> brandCodes, final String collectDay, final int maxCount, final RequestListener requestListener) throws Exception {

        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final BrandGoodsParam brandGoodsParam = new BrandGoodsParam();
            brandGoodsParam.licenseKey = licenseKey;
            brandGoodsParam.affiliateCode = CommonLibrary.getAffiliateCode();
            brandGoodsParam.brandCodes = brandCodes;
            brandGoodsParam.collectDay = collectDay;
            brandGoodsParam.maxCount = maxCount;

            Callback<List<BrandGoodsRepo>> callback = new Callback<List<BrandGoodsRepo>>() {
                @Override
                public void onResponse(Call<List<BrandGoodsRepo>> call, Response<List<BrandGoodsRepo>> response) {
                    if (response != null && response.isSuccessful() && response.body() != null)
                    {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        List<BrandGoodsRepo> goodsList =  (List<BrandGoodsRepo>) response.body();
                        requestListener.onRequestSuccess(PROTOCOL_GET_BRAND_GOODS_LIST, goodsList);
                    }
                }

                @Override
                public void onFailure(Call<List<BrandGoodsRepo>> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };

            RestAdvatarService.api(host, timeout).getBrandGoodsList(brandGoodsParam).enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onlineStorePurchaseList(String userId, String storeName, String orderNumber, String orderDate, long orderDateTime, int totalAmount, int payAmount,
                                        int refundAmount, String cancelYn, String discountDetail, int deliveryCost, final RequestListener requestListener) throws Exception {

        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final OnlineStorePurchaseParam onlineStorePurchaseParam = new OnlineStorePurchaseParam();
            onlineStorePurchaseParam.licenseKey = licenseKey;
            onlineStorePurchaseParam.affiliateCode = CommonLibrary.getAffiliateCode();

            onlineStorePurchaseParam.userId = userId;
            onlineStorePurchaseParam.storeName = storeName;
            onlineStorePurchaseParam.orderNumber = orderNumber;
            onlineStorePurchaseParam.orderDate = orderDate;
            onlineStorePurchaseParam.orderDateTime = String.valueOf(orderDateTime);
            onlineStorePurchaseParam.totalAmount = totalAmount;
            onlineStorePurchaseParam.payAmount = payAmount;
            onlineStorePurchaseParam.refundAmount = refundAmount;
            onlineStorePurchaseParam.cancelYn = cancelYn;
            onlineStorePurchaseParam.discountDetail = discountDetail;
            onlineStorePurchaseParam.deliveryCost = deliveryCost;

            Callback<ResultRepo> callback = new Callback<ResultRepo>() {
                @Override
                public void onResponse(Call<ResultRepo> call, Response<ResultRepo> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        ResultRepo result = response.body();
                        requestListener.onRequestSuccess(PROTOCOL_ONLINE_STORE_PURCHASE_LIST, result.result);
                    }
                }

                @Override
                public void onFailure(Call<ResultRepo> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };
            RestAdvatarService.api(host, timeout).onlineStorePurchaseList(onlineStorePurchaseParam).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onlineStoreProductList(String storeName, String orderNumber, String category, String productName, String productOption, int price, int quantity,
                                       String url, String imageUrl, String blankImageUrl, String seller, String status, final RequestListener requestListener) throws Exception {

        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final OnlineStoreProductParam onlineStoreProductParam = new OnlineStoreProductParam();
            onlineStoreProductParam.licenseKey = licenseKey;
            onlineStoreProductParam.affiliateCode = CommonLibrary.getAffiliateCode();

            onlineStoreProductParam.storeName = storeName;
            onlineStoreProductParam.orderNumber = orderNumber;
            onlineStoreProductParam.category = category;
            onlineStoreProductParam.productName = productName;
            onlineStoreProductParam.productOption = productOption;
            onlineStoreProductParam.price = price;
            onlineStoreProductParam.quantity = quantity;
            onlineStoreProductParam.url = url;
            onlineStoreProductParam.imageUrl = imageUrl;
            onlineStoreProductParam.blankImageUrl = blankImageUrl;
            onlineStoreProductParam.seller = seller;
            onlineStoreProductParam.status = status;

            Callback<ResultRepo> callback = new Callback<ResultRepo>() {
                @Override
                public void onResponse(Call<ResultRepo> call, Response<ResultRepo> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        ResultRepo result = response.body();
                        requestListener.onRequestSuccess(PROTOCOL_ONLINE_STORE_PRODUCT_LIST, result.result);
                    }
                }

                @Override
                public void onFailure(Call<ResultRepo> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };
            RestAdvatarService.api(host, timeout).onlineStoreProductList(onlineStoreProductParam).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
