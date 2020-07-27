package com.epopcon.advatar.common.network.rest;

import com.epopcon.advatar.common.CommonLibrary;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.util.SharedPreferenceBase;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

public class CookiesReceivedInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }

            // Preference에 cookies를 넣어주는 작업을 수행
            SharedPreferenceBase.putPrefStringSet(CommonLibrary.getContext(), Config.SHARED_PREFERENCE_NAME_COOKIE, cookies);
        }

        return originalResponse;
    }
}
