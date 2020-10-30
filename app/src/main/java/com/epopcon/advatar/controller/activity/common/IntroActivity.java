package com.epopcon.advatar.controller.activity.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.epopcon.advatar.BuildConfig;
import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.param.online.OnlinePickProductParam;
import com.epopcon.advatar.common.network.model.repo.common.AppVersionRepo;
import com.epopcon.advatar.common.network.model.repo.user.UserLoginRepo;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.DialogClickListener;
import com.epopcon.advatar.common.util.DialogUtil;
import com.epopcon.advatar.common.util.MyBrandUtil;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.epopcon.advatar.controller.activity.brand.BrandChoiceActivity;
import com.epopcon.advatar.controller.activity.user.LoginActivity;
import com.epopcon.extra.common.utils.ExecutorPool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IntroActivity extends BaseActivity {

    private final String TAG = IntroActivity.class.getSimpleName();

    private String mUpdateType;
    private String mSharedUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if (getBrandList().isEmpty()) {
            getBrandListAPI();
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                mSharedUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        final Intent intent;
        if (updateYn()) {

            boolean isNegativeButton;
            // 앱 업데이트 (강제여부에 따라 팝업 표시
            if (mUpdateType.equals("Y")) {
                // 강제 업데이트 (스토어로 이동)
                isNegativeButton = true;
            } else {
                // 선택적 업데이트 (확인 및 취소버튼)
                isNegativeButton = false;
            }

            DialogUtil.showCommonDialog(getApplicationContext(), this,
                    getString(R.string.dialog_app_update_title), getString(R.string.dialog_app_update_contents), true, isNegativeButton,
                    getString(R.string.dialog_app_update_positive_btn), getString(R.string.dialog_app_update_negative_btn),
                    new DialogClickListener() {
                        @Override
                        public void onPositiveClick() {
                            // 스토어 이동
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                        }
                        @Override
                        public void onNegativeClick() {
                            getLogin();
                        }
                    }
            );

        } else {
            if (SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_ID, null) == null ||
                    SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_PW, null) == null) {
                intent = new Intent(IntroActivity.this, LoginActivity.class);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        finish();
                    }
                }, 800);
            } else {
                getLogin();
            }
        }
    }

    private boolean updateYn() {
        final boolean[] updateYn = {false};

        try {
            RestAdvatarProtocol.getInstance().getAppVersion(new RequestListener() {
                @Override
                public void onRequestSuccess(int requestCode, Object result) {
                    AppVersionRepo appVersionRepo = (AppVersionRepo) result;

                    int appVersionCode = appVersionRepo.appVersionCode;
                    mUpdateType = appVersionRepo.updateType;
                    if (appVersionCode > BuildConfig.VERSION_CODE) {
                        updateYn[0] = true;
                    } else {
                        updateYn[0] = false;
                    }
                }

                @Override
                public void onRequestFailure(Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return updateYn[0];
    }

    private void getLogin() {
        try {
            String fcmToken = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.FCM_TOKEN, "");

            final String userId = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_ID, null);

            if (userId.startsWith("Naver") || userId.startsWith("Facebook")) {
                RestAdvatarProtocol.getInstance().userSNSLogin(userId, SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_EMAIL, ""),
                        SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_NAME, ""), fcmToken, Build.MODEL, new RequestListener() {
                            @Override
                            public void onRequestSuccess(int requestCode, Object result) {
                                final Intent intent;
                                UserLoginRepo userLoginRepo = (UserLoginRepo) result;
                                if (userLoginRepo.result.equals("SUCCESS")) {
                                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_NAME, userLoginRepo.userName);
                                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_BIRTH, userLoginRepo.userBirth);
                                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_PHONE, userLoginRepo.userPhone);
                                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_GENDER, userLoginRepo.userGender);
                                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_ADDRESS, userLoginRepo.userAddress);
                                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_EMAIL, userLoginRepo.userEmail);
                                    MyBrandUtil.putBrandCodeList(userLoginRepo.userBrandCodes);
                                    MyBrandUtil.putBrandNameList(userLoginRepo.userBrandNames);
                                    if (!TextUtils.isEmpty(SharedPreferenceBase.getPrefString(getApplicationContext(), Config.MY_BRAND_NAME, ""))) {
                                        intent = new Intent(IntroActivity.this, MainActivity.class);
                                        if (!TextUtils.isEmpty(mSharedUrl)) {
                                            final String storeName = getStoreName(mSharedUrl);
                                            if (storeName.equals("미지원")) {
                                                Toast.makeText(getApplicationContext(), "지원하지 않는 쇼핑몰 링크입니다.", Toast.LENGTH_LONG).show();
                                            }
                                            ExecutorPool.NETWORK.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    getOnlinePickInfo(storeName, mSharedUrl);
                                                }
                                            });
                                        }
                                    } else {
                                        intent = new Intent(IntroActivity.this, BrandChoiceActivity.class);
                                        if (getBrandList().isEmpty()) {
                                            getBrandListAPI();
                                            try {
                                                Thread.sleep(2000);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        intent.putParcelableArrayListExtra("brandList", getBrandList());
                                    }
                                } else {
                                    intent = new Intent(IntroActivity.this, LoginActivity.class);
                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 500);
                            }

                            @Override
                            public void onRequestFailure(Throwable t) {
                                final Intent intent = new Intent(IntroActivity.this, LoginActivity.class);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 800);
                            }
                        });
            } else {
                String userPassword = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_PW, null);
                String userPw = getPasswordEncryption(userId, userPassword);
                RestAdvatarProtocol.getInstance().userLogin(userId, userPw, fcmToken, Build.MODEL, new RequestListener() {
                            @Override
                            public void onRequestSuccess(int requestCode, Object result) {
                                final Intent intent;
                                UserLoginRepo userLoginRepo = (UserLoginRepo) result;
                                if (userLoginRepo.result.equals("SUCCESS")) {
                                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_NAME, userLoginRepo.userName);
                                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_BIRTH, userLoginRepo.userBirth);
                                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_GENDER, userLoginRepo.userGender);
                                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_PHONE, userLoginRepo.userPhone);
                                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_ADDRESS, userLoginRepo.userAddress);
                                    SharedPreferenceBase.putPrefString(getApplicationContext(), Config.USER_EMAIL, userLoginRepo.userEmail);
                                    MyBrandUtil.putBrandCodeList(userLoginRepo.userBrandCodes);
                                    MyBrandUtil.putBrandNameList(userLoginRepo.userBrandNames);
                                    if (!TextUtils.isEmpty(SharedPreferenceBase.getPrefString(getApplicationContext(), Config.MY_BRAND_NAME, ""))) {
                                        intent = new Intent(IntroActivity.this, MainActivity.class);
                                        if (!TextUtils.isEmpty(mSharedUrl)) {
                                            final String storeName = getStoreName(mSharedUrl);
                                            intent.putExtra("Fragment", "Favorite");
                                            if (storeName.equals("미지원")) {
                                                Toast.makeText(getApplicationContext(), "지원하지 않는 쇼핑몰 링크입니다.", Toast.LENGTH_LONG).show();
                                            }
                                            ExecutorPool.NETWORK.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    getOnlinePickInfo(storeName, mSharedUrl);
                                                }
                                            });
                                        }
                                    } else {
                                        intent = new Intent(IntroActivity.this, BrandChoiceActivity.class);
                                        if (getBrandList().isEmpty()) {
                                            getBrandListAPI();
                                        } else {
                                            intent.putParcelableArrayListExtra("brandList", getBrandList());
                                        }
                                    }
                                } else {
                                    intent = new Intent(IntroActivity.this, LoginActivity.class);
                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 800);
                            }

                            @Override
                            public void onRequestFailure(Throwable t) {
                                final Intent intent = new Intent(IntroActivity.this, LoginActivity.class);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 800);
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getStoreName(String url) {
        String storeName;

        if (url.contains("11st.co.kr")) {
            storeName = "top.11st.co.kr";
        } else if (url.contains("gmarket.co.kr")) {
            storeName = "top.gmarket.co.kr";
        } else if (url.contains("auction.co.kr")) {
            storeName = "top.auction.co.kr";
        } else if (url.contains("shopping.naver.com")) {
            storeName = "top.naverstore.com";
        } else if (url.contains("interpark.com")) {
            storeName = "top.interpark.com";
        } else if (url.contains("coupang.com")) {
            storeName = "top.coupang.com";
        } else if (url.contains("tmon.co.kr")) {
            storeName = "top.tmon.co.kr";
        } else if (url.contains("wemakeprice.com")) {
            storeName = "top.wemakeprice.com";
        } else if (url.contains("ssg.com")) {
            storeName = "top.ssg.com";
        } else if (url.contains("lotteon.com")) {
            storeName = "top.lotteon.com";
        } else if (url.contains("hyundaihmall.com")) {
            storeName = "top.hyundaihmall.com";
        } else if (url.contains("cjmall.com")) {
            storeName = "top.cjmall.com";
        } else if (url.contains("akmall.com")) {
            storeName = "top.akmall.com";
        } else {
            storeName = "미지원";
        }

        return storeName;
    }

    private void getOnlinePickInfo(final String siteName, final String productUrl) {
        final OnlinePickProductParam onlinePickProductParam = new OnlinePickProductParam();
        try {
            URL url = new URL(productUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStreamReader in = new InputStreamReader((InputStream) connection.getContent(), "euc-kr");
                BufferedReader br = new BufferedReader(in);
                String line;
                String text = "";
                while ((line = br.readLine()) != null) {
                    text += line;
                }

                switch (siteName) {
                    case "top.11st.co.kr":
                        productParser11st(siteName, productUrl, onlinePickProductParam, text);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void productParser11st(String siteName, String productUrl, OnlinePickProductParam onlinePickProductParam, String content) {
        try {
            Document doc = Jsoup.parse(content);

            String productName = doc.select("div.dt_title > h1").text();
            String noSale = doc.select("div.dt_price > p.no_sale").text();
            if (noSale.equals("현재 판매중인 상품이 아닙니다.")) {
                return;
            }
            int productPrice = Integer.valueOf(doc.select("div.dt_price > div.price > span > b").text().replace(",", ""));
            String delivery = doc.select("div.d_delivery > a > strong").text();
            int deliveryAmount;
            if (delivery.equals("무료배송")) {
                deliveryAmount = 0;
            } else {
                deliveryAmount = Integer.valueOf(doc.select("div.d_delivery > a > strong").text().replace("배송비", "")
                        .replace(",", "").replace("원", "").trim());

            }
            String productImg = doc.select("div.zone > ul > li > img").attr("src");

            onlinePickProductParam.productName = productName;
            onlinePickProductParam.productPrice = productPrice;
            onlinePickProductParam.deliveryAmount = deliveryAmount;
            onlinePickProductParam.collectionType = "A";
            onlinePickProductParam.productImg = productImg;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = dateFormat.format(new Date());

            onlinePickProductParam.userId = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_ID, null);
            onlinePickProductParam.siteName = siteName;
            onlinePickProductParam.productUrl = productUrl;
            onlinePickProductParam.dateTime = dateTime;

            try {
                RestAdvatarProtocol.getInstance().onlinePickProduct(onlinePickProductParam, new RequestListener() {
                    @Override
                    public void onRequestSuccess(int requestCode, Object result) {

                    }

                    @Override
                    public void onRequestFailure(Throwable t) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
