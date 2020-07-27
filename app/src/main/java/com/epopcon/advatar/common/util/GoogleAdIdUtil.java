package com.epopcon.advatar.common.util;

import android.content.Context;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class GoogleAdIdUtil {

    public static String getGoogleAdId(Context context) {
        String advertId = null;
        AdvertisingIdClient.Info idInfo = null;

        try {
            idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            advertId = idInfo.getId();
        } catch (Exception e){
            e.printStackTrace();
        }

        return advertId;
    }

    public static String getSubstitutionId() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String strTime = df.format(Calendar.getInstance().getTime());
        String uniqueUuid = strTime + "_" + UUID.randomUUID().toString();
        return uniqueUuid;
    }
}
