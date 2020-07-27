package com.epopcon.advatar.common.network.rest;

import com.epopcon.advatar.common.CommonLibrary;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.util.SharedPreferenceBase;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CookiesAddInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        // Preference에서 cookies를 가져오는 작업을 수행
        Set<String> preferences = SharedPreferenceBase.getPrefStringSet(CommonLibrary.getContext(), Config.SHARED_PREFERENCE_NAME_COOKIE, new HashSet<String>());

        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
        }

        // Web,Android,iOS 구분을 위해 User-Agent세팅
        builder.removeHeader("User-Agent").addHeader("User-Agent", "Android");

        return chain.proceed(builder.build());
    }
}