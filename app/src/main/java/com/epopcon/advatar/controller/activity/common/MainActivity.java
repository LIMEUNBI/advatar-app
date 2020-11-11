package com.epopcon.advatar.controller.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.epopcon.advatar.R;
import com.epopcon.advatar.controller.activity.brand.BrandChoiceActivity;
import com.epopcon.advatar.controller.activity.online.OnlineListActivity;
import com.epopcon.advatar.controller.activity.online.OnlineSearchActivity;
import com.epopcon.advatar.controller.fragment.FavoriteFragment;
import com.epopcon.advatar.controller.fragment.GoodsFragment;
import com.epopcon.advatar.controller.fragment.MyPageFragment;
import com.epopcon.advatar.controller.fragment.OnlineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends BaseActivity {

    BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment = null;

    private TextView toolbarTitle;
    private ImageView imgOption;
    private ImageView imgOnlineOption;
    private ImageView imgSearch;

    private long backPressedTime;

    public static int mCurrentFragmentId = R.id.action_one;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Home");

        imgOption = (ImageView) findViewById(R.id.img_option);

        imgOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BrandChoiceActivity.class);
                if (getBrandList().isEmpty()) {
                    getBrandListAPI();
                }
                intent.putParcelableArrayListExtra("brandList", getBrandList());
                intent.putExtra("beforeActivity", "MainActivity");
                startActivityForResult(intent, 0);
            }
        });

        imgOnlineOption = (ImageView) findViewById(R.id.img_online_option);

        imgOnlineOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OnlineListActivity.class);
                startActivity(intent);
            }
        });

        imgSearch = (ImageView) findViewById(R.id.img_search);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OnlineSearchActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        View view = bottomNavigationView.findViewById(R.id.action_one);
        view.performClick();

        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        bottomNavigationView();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (getIntent().getStringExtra("Fragment") != null && getIntent().getStringExtra("Fragment").equals("Favorite")) {
            view = bottomNavigationView.findViewById(R.id.action_two);
            view.performClick();
            transaction.replace(R.id.frame_layout, FavoriteFragment.getInstance());
        } else {
            transaction.replace(R.id.frame_layout, GoodsFragment.getInstance());
        }
        transaction.commit();

        getHashKey();

        if (getBrandList().isEmpty()) {
            getBrandListAPI();
        }

    }

    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void bottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                mCurrentFragmentId = menuItem.getItemId();

                switch (menuItem.getItemId()) {

                    case R.id.action_one:
                        toolbarTitle.setText("베스트셀러 아이템");
                        selectedFragment = GoodsFragment.getInstance();
                        imgOption.setVisibility(View.VISIBLE);
                        imgOnlineOption.setVisibility(View.GONE);
                        imgSearch.setVisibility(View.GONE);
                        break;

                    case R.id.action_two:
                        toolbarTitle.setText("상품 추적");
                        selectedFragment = FavoriteFragment.getInstance();
                        imgOption.setVisibility(View.GONE);
                        imgSearch.setVisibility(View.VISIBLE);
                        imgOnlineOption.setVisibility(View.GONE);
                        break;

                    case R.id.action_three:
                        toolbarTitle.setText("쇼핑몰 구매내역");
                        selectedFragment = OnlineFragment.getInstance();
                        imgOption.setVisibility(View.GONE);
                        imgOnlineOption.setVisibility(View.VISIBLE);
                        imgSearch.setVisibility(View.GONE);
                        break;

                    case R.id.action_four:
                        toolbarTitle.setText("마이 페이지");
                        selectedFragment = MyPageFragment.getInstance();
                        imgOption.setVisibility(View.GONE);
                        imgOnlineOption.setVisibility(View.GONE);
                        imgSearch.setVisibility(View.GONE);
                        break;
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commitAllowingStateLoss();
                return true;
            }
        });
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case 0:
                bottomNavigationView();
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;

            if (intervalTime < 2000) {
                finish();
            } else {
                backPressedTime = tempTime;
                Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
