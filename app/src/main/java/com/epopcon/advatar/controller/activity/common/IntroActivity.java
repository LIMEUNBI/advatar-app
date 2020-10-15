package com.epopcon.advatar.controller.activity.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.epopcon.advatar.BuildConfig;
import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.repo.common.AppVersionRepo;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.DialogClickListener;
import com.epopcon.advatar.common.util.DialogUtil;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.epopcon.advatar.controller.activity.brand.BrandChoiceActivity;
import com.epopcon.advatar.controller.activity.user.LoginActivity;

public class IntroActivity extends BaseActivity {

    private final String TAG = IntroActivity.class.getSimpleName();

    private String mUpdateType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        final Intent intent;
        if (updateYn()) {

            boolean isNegativeButton;
            // 앱 업데이트 (강제여부에 따라 팝업 표시
            if (mUpdateType.equals("Y")) {
                // 강제 업데이트 (스토어로 이동)
                isNegativeButton = true;
            } else {
                // 선택적 업데이트 (확인 및 취소버튼)
                isNegativeButton = false;
            }

            DialogUtil.showCommonDialog(getApplicationContext(), this,
                    getString(R.string.dialog_app_update_title), getString(R.string.dialog_app_update_contents), true, isNegativeButton,
                    getString(R.string.dialog_app_update_positive_btn), getString(R.string.dialog_app_update_negative_btn),
                    new DialogClickListener() {
                        @Override
                        public void onPositiveClick() {
                            // 스토어 이동
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                        }
                        @Override
                        public void onNegativeClick() {
                            getLogin();
                        }
                    }
            );

        } else {
            if (SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_ID, null) == null ||
                    SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_PW, null) == null) {
                intent = new Intent(IntroActivity.this, LoginActivity.class);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        finish();
                    }
                }, 800);
            } else {
                getLogin();
            }
        }
    }

    private boolean updateYn() {
        final boolean[] updateYn = {false};

        try {
            RestAdvatarProtocol.getInstance().getAppVersion(new RequestListener() {
                @Override
                public void onRequestSuccess(int requestCode, Object result) {
                    AppVersionRepo appVersionRepo = (AppVersionRepo) result;

                    int appVersionCode = appVersionRepo.appVersionCode;
                    mUpdateType = appVersionRepo.updateType;
                    if (appVersionCode > BuildConfig.VERSION_CODE) {
                        updateYn[0] = true;
                    } else {
                        updateYn[0] = false;
                    }
                }

                @Override
                public void onRequestFailure(Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return updateYn[0];
    }

    private void getLogin() {
        try {
            String fcmToken = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.FCM_TOKEN, "");

            String userId = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_ID, null);

            if (userId.startsWith("Naver") || userId.startsWith("Facebook")) {
                RestAdvatarProtocol.getInstance().userSNSLogin(userId, SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_EMAIL, ""),
                        SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_NAME, ""), fcmToken, new RequestListener() {
                            @Override
                            public void onRequestSuccess(int requestCode, Object result) {
                                final Intent intent;
                                if (result.toString().equals("SUCCESS")) {
                                    if (!TextUtils.isEmpty(SharedPreferenceBase.getPrefString(getApplicationContext(), Config.MY_BRAND_NAME, ""))) {
                                        intent = new Intent(IntroActivity.this, MainActivity.class);
                                    } else {
                                        intent = new Intent(IntroActivity.this, BrandChoiceActivity.class);
                                    }
                                } else {
                                    intent = new Intent(IntroActivity.this, LoginActivity.class);
                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 500);
                            }

                            @Override
                            public void onRequestFailure(Throwable t) {
                                final Intent intent = new Intent(IntroActivity.this, LoginActivity.class);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 800);
                            }
                        });
            } else {
                RestAdvatarProtocol.getInstance().userLogin(userId,
                        SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_PW, null), fcmToken, new RequestListener() {
                            @Override
                            public void onRequestSuccess(int requestCode, Object result) {
                                final Intent intent;
                                if (result.toString().equals("SUCCESS")) {
                                    if (!TextUtils.isEmpty(SharedPreferenceBase.getPrefString(getApplicationContext(), Config.MY_BRAND_NAME, ""))) {
                                        intent = new Intent(IntroActivity.this, MainActivity.class);
                                    } else {
                                        intent = new Intent(IntroActivity.this, BrandChoiceActivity.class);
                                    }
                                } else {
                                    intent = new Intent(IntroActivity.this, LoginActivity.class);
                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 800);
                            }

                            @Override
                            public void onRequestFailure(Throwable t) {
                                final Intent intent = new Intent(IntroActivity.this, LoginActivity.class);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 800);
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
