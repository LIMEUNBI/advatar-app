package com.epopcon.advatar.common.network.rest;

import android.text.TextUtils;
import android.util.Log;

import com.epopcon.advatar.common.CommonLibrary;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.param.brand.BrandContentsParam;
import com.epopcon.advatar.common.network.model.param.common.AppVersionParam;
import com.epopcon.advatar.common.network.model.param.brand.BrandGoodsParam;
import com.epopcon.advatar.common.network.model.param.brand.BrandParam;
import com.epopcon.advatar.common.network.model.param.CommonParam;
import com.epopcon.advatar.common.network.model.param.online.OnlineStoreCartParam;
import com.epopcon.advatar.common.network.model.param.online.OnlineStoreProductParam;
import com.epopcon.advatar.common.network.model.param.online.OnlineStorePurchaseParam;
import com.epopcon.advatar.common.network.model.param.user.UserParam;
import com.epopcon.advatar.common.network.model.repo.brand.BrandContentsRepo;
import com.epopcon.advatar.common.network.model.repo.common.AppVersionRepo;
import com.epopcon.advatar.common.network.model.repo.brand.BrandGoodsRepo;
import com.epopcon.advatar.common.network.model.repo.brand.BrandRepo;
import com.epopcon.advatar.common.network.model.repo.common.ExtraVersionRepo;
import com.epopcon.advatar.common.network.model.repo.common.OnlineStoreStatusRepo;
import com.epopcon.advatar.common.network.model.repo.ResultRepo;
import com.epopcon.advatar.common.network.model.repo.user.UserFindIdRepo;
import com.epopcon.advatar.common.util.EncrypterUtil;

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
    public static final int PROTOCOL_GET_BRAND_AD_LIST = 0x203;
    public static final int PROTOCOL_GET_BRAND_CONTENTS_LIST = 0x204;

    public static final int PROTOCOL_ONLINE_STORE_PURCHASE_LIST = 0x301;
    public static final int PROTOCOL_ONLINE_STORE_PRODUCT_LIST = 0x302;
    public static final int PROTOCOL_ONLINE_STORE_CART_LIST = 0x303;

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

    /**
     * ============================ common ============================
     */

    /**
     * 최신 앱버전을 조회하여 업데이트 처리한다.
     * @param requestListener
     * @throws Exception
     */
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

    /**
     * 최신 extra 버전을 조회하여 dex 업데이트 처리한다.
     * @param requestListener
     * @throws Exception
     */
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

    /**
     * 온라인쇼핑몰 상태를 조회하여 로그인 및 자동 동기화 기능을 차단한다.
     * @param requestListener
     * @throws Exception
     */
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


    /**
     * ============================ user ============================
     */

    /**
     * 사용자 아이디로 조회하여 중복 여부를 판단한다.
     * @param userId 사용자 아이디
     * @param requestListener
     * @throws Exception
     */
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

    /**
     * 회원가입을 처리한다.
     * @param userId 사용자 아이디
     * @param userPw 사용자 패스워드
     * @param deviceInfo 단말 정보
     * @param fcmToken FCM 토큰값
     * @param userName 사용자 이름
     * @param userBirth 사용자 생년월일
     * @param userGender 사용자 성별
     * @param userPhone 사용자 전화번호
     * @param userAddress 사용자 주소
     * @param userEmail 사용자 이메일
     * @param requestListener
     * @throws Exception
     */
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

    /**
     * 사용자 로그인 처리한다. (자동 로그인 동일)
     * @param userId 사용자 아이디
     * @param userPw 사용자 패스워드
     * @param fcmToken FCM 토큰값 (업데이트를 위해 로그인할 때 마다 전송)
     * @param requestListener
     * @throws Exception
     */
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

    /**
     * SNS 로그인 기능을 수행한다.
     * @param userId 사용자 아이디
     * @param userEmail 사용자 이메일
     * @param userName 사용자 이름
     * @param fcmToken FCM 토큰값 (업데이트를 위해 로그인할 때 마다 전송)
     * @param requestListener
     * @throws Exception
     */
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

    /**
     * 사용자 이메일과 이름으로 아이디를 조회한다.
     * @param userEmail 사용자 이메일
     * @param userName 사용자 이름
     * @param requestListener
     * @throws Exception
     */
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

    /**
     * 사용자 아이디와 이름, 휴대폰 번호로 사용자 정보를 조회한다.
     * @param userId 사용자 아이디
     * @param userName 사용자 이름
     * @param userPhone 사용자 전화번호
     * @param requestListener
     * @throws Exception
     */
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

    /**
     * userFindPw 를 통해 사용자 정보가 있을 경우 비밀번호를 변경 처리한다.
     * @param userId 사용자 아이디
     * @param userPw 사용자 비밀번호
     * @param requestListener
     * @throws Exception
     */
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

    /**
     * 사용자가 선호하는 브랜드를 서버에 저장한다.
     * @param userId 사용자 아이디
     * @param userBrands 선호 브랜드 리스트
     * @param requestListener
     * @throws Exception
     */
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


    /**
     * ============================ brand ============================
     */

    /**
     * 브랜드 목록을 조회한다.
     * @param maxCount 최대 갯수
     * @param requestListener
     * @throws Exception
     */
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

    /**
     * 전달한 브랜드들의 상품 목록을 조회한다. (날짜는 D -2일 고정)
     * @param brandCodes 브랜드 코드 목록
     * @param collectDay 수집날짜 (D-2일)
     * @param maxCount 최대 갯수
     * @param requestListener
     * @throws Exception
     */
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

    /**
     * 선호하는 브랜드의 컨텐츠 목록을 조회한다.
     * @param brandCodes 선호 브랜드 코드 목록
     * @param requestListener
     * @throws Exception
     */
    public void getBrandContentsList(final List<String> brandCodes, final RequestListener requestListener) throws Exception {

        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final BrandContentsParam brandContentsParam = new BrandContentsParam();
            brandContentsParam.licenseKey = licenseKey;
            brandContentsParam.affiliateCode = CommonLibrary.getAffiliateCode();
            brandContentsParam.brandCodes = brandCodes;

            Callback<List<BrandContentsRepo>> callback = new Callback<List<BrandContentsRepo>>() {
                @Override
                public void onResponse(Call<List<BrandContentsRepo>> call, Response<List<BrandContentsRepo>> response) {
                    if (response != null && response.isSuccessful() && response.body() != null)
                    {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        List<BrandContentsRepo> goodsList =  (List<BrandContentsRepo>) response.body();
                        requestListener.onRequestSuccess(PROTOCOL_GET_BRAND_CONTENTS_LIST, goodsList);
                    }
                }

                @Override
                public void onFailure(Call<List<BrandContentsRepo>> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };
            RestAdvatarService.api(host, timeout).getBrandContentsList(brandContentsParam).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * ============================ online ============================
     */

    /**
     * 온라인 쇼핑몰 구매 내역을 서버에 저장한다.
     * @param userId 사용자 아이디
     * @param storeName 온라인 스토어 이름
     * @param orderNumber 주문 번호
     * @param orderDate 주문 날짜
     * @param orderDateTime 주문 일시
     * @param totalAmount 전체 금액
     * @param payAmount 결제 금액
     * @param refundAmount 환불 금액
     * @param cancelYn 취소 여부
     * @param discountDetail 할인 상세
     * @param deliveryCost 배송비
     * @param requestListener
     * @throws Exception
     */
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

    /**
     * 온라인 쇼핑몰에서 구매한 상품 목록을 서버에 저장한다.
     * @param storeName 온라인 스토어 이름
     * @param orderNumber 주문 번호
     * @param category 상품 카테고리
     * @param productName 상품 명
     * @param productOption 상품 옵션
     * @param price 가격
     * @param quantity 갯수
     * @param url 상품 url
     * @param imageUrl 상품 이미지 url
     * @param blankImageUrl 빈 이미지 url
     * @param seller 판매자 정보
     * @param status 상태 (배달상태)
     * @param requestListener
     * @throws Exception
     */
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

    /**
     * 온라인 쇼핑몰 장바구니 목록 전달
     * @param storeName 온라인 스토어 이름
     * @param title 딜 제목
     * @param optionPrice 옵션 가격
     * @param promoTitle 프로모션 제목
     * @param options 옵션
     * @param imgUrl 이미지 url
     * @param selectCount 상품 갯수
     * @param discount 할인금액
     * @param totalAmount 전체 금액
     * @param expectedDeliveryEndDate 배송 예정일
     * @param deliveryPolicy 배송 정책 (무료배송 여부)
     * @param deliveryIfAmount 무료배송 조건 금액
     * @param deliveryAmount 배송비
     * @param avgDeliveryDays 평균 배송일
     * @param sellerInfo 판매자 정보
     * @param cartType 장바구니 타입 (Cart : 장바구니, Recent : 최근 본 상품)
     * @param requestListener
     * @throws Exception
     */
    public void onlineStoreCartList(String userId, String storeName, String title, String dealUrl, int optionPrice, String promoTitle, String options,
                                    String imgUrl, int selectCount, int discount, int totalAmount, String expectedDeliveryEndDate,
                                    String deliveryPolicy, int deliveryIfAmount, int deliveryAmount, double avgDeliveryDays,
                                    String sellerInfo, String cartType, final RequestListener requestListener) throws Exception {

        final String licenseKey = EncrypterUtil.getInstance().getLicenseKey();
        if (TextUtils.isEmpty(licenseKey)) {
            return;
        }

        try {
            final OnlineStoreCartParam onlineStoreCartParam = new OnlineStoreCartParam();
            onlineStoreCartParam.licenseKey = licenseKey;
            onlineStoreCartParam.affiliateCode = CommonLibrary.getAffiliateCode();

            onlineStoreCartParam.userId = userId;
            onlineStoreCartParam.storeName = storeName;
            onlineStoreCartParam.title = title;
            onlineStoreCartParam.dealUrl = dealUrl;
            onlineStoreCartParam.optionPrice = optionPrice;
            onlineStoreCartParam.promoTitle = promoTitle;
            onlineStoreCartParam.options = options;
            onlineStoreCartParam.imgUrl = imgUrl;
            onlineStoreCartParam.selectCount = selectCount;
            onlineStoreCartParam.discount = discount;
            onlineStoreCartParam.totalAmount = totalAmount;
            onlineStoreCartParam.expectedDeliveryEndDate = expectedDeliveryEndDate;
            onlineStoreCartParam.deliveryPolicy = deliveryPolicy;
            onlineStoreCartParam.deliveryIfAmount = deliveryIfAmount;
            onlineStoreCartParam.deliveryAmount = deliveryAmount;
            onlineStoreCartParam.avgDeliveryDays = avgDeliveryDays;
            onlineStoreCartParam.sellerInfo = sellerInfo;
            onlineStoreCartParam.cartType = cartType;

            Callback<ResultRepo> callback = new Callback<ResultRepo>() {
                @Override
                public void onResponse(Call<ResultRepo> call, Response<ResultRepo> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        if (response.code() != 200) {
                            requestListener.onRequestFailure(new Throwable(response.message()));
                            return;
                        }

                        ResultRepo result = response.body();
                        requestListener.onRequestSuccess(PROTOCOL_ONLINE_STORE_CART_LIST, result.result);
                    }
                }

                @Override
                public void onFailure(Call<ResultRepo> call, Throwable t) {
                    requestListener.onRequestFailure(t);
                }
            };
            RestAdvatarService.api(host, timeout).onlineStoreCartList(onlineStoreCartParam).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
