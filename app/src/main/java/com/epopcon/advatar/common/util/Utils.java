package com.epopcon.advatar.common.util;

import android.content.Context;

import com.epopcon.advatar.common.CommonLibrary;

public class Utils {
    static final String TAG = Utils.class.getSimpleName();

    protected Utils() {

    }

    public static Context getApplicationContext() {
        return CommonLibrary.getContext();
    }
}
