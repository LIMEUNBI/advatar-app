package com.epopcon.advatar.common.model;

import com.epopcon.advatar.R;
import com.epopcon.extra.online.OnlineConstant;

import java.util.HashMap;
import java.util.Map;

public class OnlineType {

    public static Map<OnlineConstant, Integer> onlineStoreLogoMap = new HashMap<>();

    static {
        // 소셜커머스
        onlineStoreLogoMap.put(OnlineConstant.COUPANG, R.drawable.online_store_logo_coupang);
        onlineStoreLogoMap.put(OnlineConstant.WEMAP, R.drawable.online_store_logo_wemakeprice);
        onlineStoreLogoMap.put(OnlineConstant.TMON, R.drawable.online_store_logo_tmon);

        // 인터넷쇼핑
        onlineStoreLogoMap.put(OnlineConstant.GMARKET, R.drawable.online_store_logo_gmarket);
        onlineStoreLogoMap.put(OnlineConstant._11ST, R.drawable.online_store_logo_11st);
        onlineStoreLogoMap.put(OnlineConstant.AUCTION, R.drawable.online_store_logo_auction);
        onlineStoreLogoMap.put(OnlineConstant.NAVER, R.drawable.online_store_logo_naver);
        onlineStoreLogoMap.put(OnlineConstant.INTERPARK, R.drawable.online_store_logo_interpark);
        onlineStoreLogoMap.put(OnlineConstant.LOTTE_COM, R.drawable.online_store_logo_lotte_com);
        onlineStoreLogoMap.put(OnlineConstant.SSG, R.drawable.online_store_logo_shinsegaemall);
        onlineStoreLogoMap.put(OnlineConstant.AK_MALL, R.drawable.online_store_logo_ak_mall);
        onlineStoreLogoMap.put(OnlineConstant.G9, R.drawable.online_store_logo_g9);

        // 홈쇼핑
        onlineStoreLogoMap.put(OnlineConstant.GS_SHOP, R.drawable.online_store_logo_gs_shop);
        onlineStoreLogoMap.put(OnlineConstant.CJ_MALL, R.drawable.online_store_logo_cj_mall);
    }

    public static Map<String, OnlineConstant> onlineStoreCodeMap = new HashMap<>();

    static {
        onlineStoreCodeMap.put("COUPANG", OnlineConstant.COUPANG);
        onlineStoreCodeMap.put("WEMAP", OnlineConstant.WEMAP);
        onlineStoreCodeMap.put("TMON", OnlineConstant.TMON);

        onlineStoreCodeMap.put("GMARKET", OnlineConstant.GMARKET);
        onlineStoreCodeMap.put("_11ST", OnlineConstant._11ST);
        onlineStoreCodeMap.put("AUCTION", OnlineConstant.AUCTION);
        onlineStoreCodeMap.put("NAVER", OnlineConstant.NAVER);
        onlineStoreCodeMap.put("INTERPARK", OnlineConstant.INTERPARK);
        onlineStoreCodeMap.put("LOTTE_COM", OnlineConstant.LOTTE_COM);
        onlineStoreCodeMap.put("SSG", OnlineConstant.SSG);
        onlineStoreCodeMap.put("AK_MALL", OnlineConstant.AK_MALL);
        onlineStoreCodeMap.put("G9", OnlineConstant.G9);

        onlineStoreCodeMap.put("GS_SHOP", OnlineConstant.GS_SHOP);
        onlineStoreCodeMap.put("CJ_MALL", OnlineConstant.CJ_MALL);
    }
}
