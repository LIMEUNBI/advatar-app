package com.epopcon.advatar.common.util;

import com.epopcon.advatar.common.config.Config;

import static com.epopcon.advatar.common.util.Utils.getApplicationContext;

public class MyBrandUtil {

    public static void putBrandCodeList(String brandCode) {
        SharedPreferenceBase.putPrefString(getApplicationContext(), Config.MY_BRAND_LIST, brandCode);
    }

    public static void putBrandNameList(String brandName) {
        SharedPreferenceBase.putPrefString(getApplicationContext(), Config.MY_BRAND_NAME, brandName);
    }

    public static String getBrandCodeList() {
        return SharedPreferenceBase.getPrefString(getApplicationContext(), Config.MY_BRAND_LIST, "");
    }

    public static String getBrandNameList() {
        return SharedPreferenceBase.getPrefString(getApplicationContext(), Config.MY_BRAND_NAME, "");
    }
}
