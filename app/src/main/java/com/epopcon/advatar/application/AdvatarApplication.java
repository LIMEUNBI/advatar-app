package com.epopcon.advatar.application;

import android.app.Application;

import com.epopcon.advatar.common.CommonLibrary;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.db.SqlBuilder;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.VersionManager;
import com.epopcon.extra.BuildConfig;
import com.epopcon.extra.ExtraClassLoader;
import com.epopcon.extra.ExtraLibHelper;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
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
        SqlBuilder.initialize(this.getApplicationContext());

        String host = "http://192.168.0.104:18091/";
//        String host = "http://dev.epopcon.com:18091/advatar-server/";
        int timeout = 15;

        RestAdvatarProtocol.getInstance().setConnectionInfo(host, timeout);

        SqlBuilder.initialize(this.getApplicationContext());

        // Facebook API 초기화
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        ExtraClassLoader.initialize(this);
        ExtraLibHelper.setLogReport(true);
        ExtraLibHelper.setSaveIdAndPassword(true);
        ExtraLibHelper.setConsoleOutput(BuildConfig.DEBUG);

        VersionManager.getInstance(this).checkVersion();

        ImageLoaderConfiguration imageLoaderConfig = new ImageLoaderConfiguration.Builder(getContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)

                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(imageLoaderConfig);
    }
}
