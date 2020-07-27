package com.epopcon.advatar.common.network.rest;

import android.text.TextUtils;
import android.util.Log;

import com.epopcon.advatar.common.CommonLibrary;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.param.BrandGoodsParam;
import com.epopcon.advatar.common.network.model.param.BrandParam;
import com.epopcon.advatar.common.network.model.repo.BrandGoodsRepo;
import com.epopcon.advatar.common.network.model.repo.BrandRepo;
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

    public static final int PROTOCOL_GET_BRAND_LIST = 0x201;
    public static final int PROTOCOL_GET_BRAND_GOODS_LIST = 0x202;

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

}
