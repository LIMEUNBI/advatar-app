package com.epopcon.advatar.application;

import android.app.Application;

import com.epopcon.advatar.common.CommonLibrary;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import static com.epopcon.advatar.common.CommonLibrary.getContext;

public class AdvatarApplication extends Application {

    private static final String TAG = AdvatarApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        CommonLibrary.getInstance(getApplicationContext());
        CommonLibrary.setAffiliateCode(Config.AFFILIATE_CODE_ADVATAR);

        String host = "http://192.168.0.127:18091/";
//        String host = "http://dev.epopcon.com:18091/advatar-server/";
        int timeout = 5;

        RestAdvatarProtocol.getInstance().setConnectionInfo(host, timeout);

        ImageLoaderConfiguration imageLoaderConfig = new ImageLoaderConfiguration.Builder(getContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)

                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                //.writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(imageLoaderConfig);

    }
}
