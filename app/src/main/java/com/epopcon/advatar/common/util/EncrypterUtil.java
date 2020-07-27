package com.epopcon.advatar.common.util;

import android.util.Log;

import com.epopcon.advatar.common.CommonLibrary;
import com.epopcon.advatar.common.config.Config;

public class EncrypterUtil {

    private static EncrypterUtil instance;
    private static Encrypter encrypter = null;

    public static EncrypterUtil getInstance() {
        if (instance == null) {
            instance = new EncrypterUtil();
        }
        return instance;
    }

    private EncrypterUtil() {
        try {
            encrypter = new AESEncrypter(AESPassword.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLicenseKey() throws Exception {

        // serviceLicense 생성규칙
        // https://epopcon.atlassian.net/wiki/spaces ... 어딘가에 설명
        String licenseKey;
        String affiliateCode = CommonLibrary.getAffiliateCode();
        Log.d("EncrypterUtil", "get LicenseKey : " + affiliateCode);

        StringBuffer sb = new StringBuffer();
        if (affiliateCode.equals(Config.AFFILIATE_CODE_ADVATAR)) {
            sb = new StringBuffer("advatar1").append("||").append("");
        }
        // 시간 : 키값 바뀌는 용도로만 사용
        licenseKey = encrypter.encrypt(sb.toString() + "||" + System.currentTimeMillis());
        Log.d("EncrypterUtil", "licenseKey : " + licenseKey);
        return licenseKey;
    }
}
