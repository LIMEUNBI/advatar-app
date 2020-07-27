package com.epopcon.advatar.common;

import android.content.Context;

public class CommonLibrary {
    private static Context context;
    private static CommonLibrary instance = null;
    private static String mAffiliateCode = null;

    private CommonLibrary(Context context) {
        this.context = context;
    }

    public static CommonLibrary getInstance(Context context) {
        if(instance == null) {
            instance = new CommonLibrary(context);
        }
        return instance;
    }

    public static Context getContext() {
        return context;
    }

    public static String getPackageName() {
        return context.getPackageName();
    }

    public static String getAffiliateCode() {
        return mAffiliateCode;
    }

    public static void setAffiliateCode(String affiliateCode) {
        mAffiliateCode = affiliateCode;
    }

}