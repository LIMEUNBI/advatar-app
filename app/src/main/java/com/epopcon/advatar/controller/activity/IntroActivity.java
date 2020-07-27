package com.epopcon.advatar.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.util.SharedPreferenceBase;

public class IntroActivity extends BaseActivity {

    private final String TAG = IntroActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        final Intent intent;

        if (TextUtils.isEmpty(SharedPreferenceBase.getPrefString(getApplicationContext(), Config.MY_BRAND_LIST, null))) {
            intent = new Intent(IntroActivity.this, BrandChoiceActivity.class);
        } else {
            intent = new Intent(IntroActivity.this, MainActivity.class);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 800);


    }
}
