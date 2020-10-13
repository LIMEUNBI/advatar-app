package com.epopcon.advatar.common.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.epopcon.advatar.BuildConfig;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.repo.common.ExtraVersionRepo;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.extra.ExtraClassLoader;
import com.epopcon.extra.common.utils.ExecutorPool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionManager {

    private static final String TAG = VersionManager.class.getSimpleName();

    private Context context;
    private static VersionManager instance = null;

    private String mAppVersionName;
    private int mAppVersionCode;
    private String mUpdateType;
    private int mExtraVersion;
    private String mUrl;
    private String mMd5;

    private VersionManager(Context context) {
        this.context = context;
    }

    public static synchronized VersionManager getInstance(Context context) {
        if (instance == null) {
            synchronized (VersionManager.class) {
                if (instance == null) {
                    instance = new VersionManager(context);
                }
            }
        }
        return instance;
    }

    public void checkVersion() {
        try {
            checkAndExecuteUpdate();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private synchronized void checkAndExecuteUpdate() {

        final String version = SharedPreferenceBase.getPrefString(context, Config.VERSION, "");
        final int versionCode = SharedPreferenceBase.getPrefInt(context, Config.VERSION_CODE, 0);

        if (TextUtils.isEmpty(version) || !BuildConfig.VERSION_NAME.equalsIgnoreCase(version)) {
            Config.VERSION_NAME = BuildConfig.VERSION_NAME;

            SharedPreferenceBase.putPrefString(context, Config.VERSION, BuildConfig.VERSION_NAME);
            SharedPreferenceBase.putPrefInt(context, Config.VERSION_CODE, BuildConfig.VERSION_CODE);
            SharedPreferenceBase.putPrefInt(context, Config.PREV_VERSION_CODE, versionCode);
            // 앱 초기 설치 혹은 업데이트 시에 asset 경로에 있는 lib 를 load
            SharedPreferenceBase.putPrefInt(context, ExtraClassLoader.EXTRA_VERSION, -1);
        }

        try {
            RestAdvatarProtocol.getInstance().getExtraVersion(new RequestListener() {
                @Override
                public void onRequestSuccess(int requestCode, Object result) {
                    ExtraVersionRepo extraVersionRepo = (ExtraVersionRepo) result;

                    mExtraVersion = extraVersionRepo.extraVersion;
                    mUrl = extraVersionRepo.url;
                    mMd5 = extraVersionRepo.md5;

                    if (mExtraVersion != SharedPreferenceBase.getPrefInt(context, ExtraClassLoader.EXTRA_VERSION, -1)) {

                        ExecutorPool.NETWORK.execute(new Runnable() {
                            @Override
                            public void run() {
                                upgradeExtraModule(mExtraVersion, mUrl, mMd5, 1);
                            }
                        });
                    }
                }

                @Override
                public void onRequestFailure(Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Extra Module 을 업그레이드
     *
     * @param versionCode
     * @param downloadUrl
     * @param md5String
     */
    private void upgradeExtraModule(int versionCode, String downloadUrl, String md5String, int tryCnt) {

        final int BEFORE_VERSION = ExtraClassLoader.getInstance().getVersionCode();
        Log.d(TAG, "current extra ver. " + BEFORE_VERSION);

        if (BEFORE_VERSION > -1) {
            if (versionCode > BEFORE_VERSION) {
                if (tryCnt >= 3) {
                    Log.w(TAG, "upgradeExtraModule -> fail!!");
                    return;
                }

                InputStream inputStream = null;
                FileOutputStream outputStream = null;

                File dexDir = context.getDir("dexin", Context.MODE_PRIVATE);

                try {
                    URL url = new URL(downloadUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();

                    if (responseCode == 200) {
                        File defaultPath = new File(dexDir, "/extra-online-update.zip");

                        if (defaultPath.exists())
                            defaultPath.delete();

                        inputStream = connection.getInputStream();
                        outputStream = new FileOutputStream(defaultPath);

                        Utils.copy(inputStream, outputStream);

                        if (defaultPath.exists()) {
                            if (!TextUtils.isEmpty(md5String)) {
                                String temp = Utils.getMd5String(defaultPath);
                                Log.d(TAG, String.format("MD5 -> md5String : %s, real : %s, tryCnt : %s ", md5String, temp, tryCnt));

                                if (!md5String.equals(temp)) {
                                    upgradeExtraModule(versionCode, downloadUrl, md5String, tryCnt + 1);
                                    return;
                                }
                            }
                        } else {
                            Log.d(TAG, "Download Fail!! -> tryCnt : " + tryCnt);

                            upgradeExtraModule(versionCode, downloadUrl, md5String, tryCnt + 1);
                            return;
                        }

                        if (ExtraClassLoader.getInstance().reload(context, defaultPath)) {

                            File newDex = new File(dexDir, String.format("/extra-online-%s.zip", versionCode));

                            if (newDex.exists())
                                newDex.delete();
                            if (com.epopcon.advatar.common.util.Utils.copyFile(defaultPath, newDex) && newDex.exists()) {
                                SharedPreferenceBase.putPrefInt(context, ExtraClassLoader.EXTRA_VERSION, versionCode);

                                // 이전 버전 삭제
                                String[] list = dexDir.list(new FilenameFilter() {
                                    @Override
                                    public boolean accept(File dir, String name) {
                                        if (name.matches("(?i)extra-online-\\d+\\.zip")) {
                                            String version = name.replaceAll("extra-online-(\\d+)\\.zip", "$1");
                                            return !TextUtils.isEmpty(version) && Integer.parseInt(version) < BEFORE_VERSION;
                                        }
                                        return false;
                                    }
                                });

                                for (String name : list) {
                                    File file = new File(dexDir, name);
                                    if (file.exists())
                                        file.delete();
                                }
                            } else {
                                Log.e(TAG, "new dex file copy -> fail!!");
                            }
                        }

                        Log.d(TAG, "versionCode | Before -> " + BEFORE_VERSION + ", After -> " + ExtraClassLoader.getInstance().getVersionCode());
                    }
                } catch (Throwable e) {
                    Log.e(TAG, e.getMessage(), e);
                } finally {
                    com.epopcon.advatar.common.util.Utils.closeQuietly(outputStream);
                    com.epopcon.advatar.common.util.Utils.closeQuietly(inputStream);
                }
            }
        } else {
            SharedPreferenceBase.putPrefInt(context, ExtraClassLoader.EXTRA_VERSION, -1);
        }
    }
}
