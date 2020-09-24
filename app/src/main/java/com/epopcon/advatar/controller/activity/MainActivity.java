package com.epopcon.advatar.controller.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.epopcon.advatar.controller.activity.online.OnlineLoginActivity;
import com.epopcon.advatar.controller.fragment.ContentsFragment;
import com.epopcon.advatar.controller.fragment.GoodsFragment;
import com.epopcon.advatar.controller.fragment.MyPageFragment;
import com.epopcon.advatar.controller.fragment.OnlineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import org.mozilla.javascript.tools.jsc.Main;

public class MainActivity extends BaseActivity {

    BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment = null;

    private TextView toolbarTitle;
    private ImageView imgOption;
    private ImageView imgOnlineOption;

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
                startActivityForResult(intent, 0);
                finish();
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

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        View view = bottomNavigationView.findViewById(R.id.action_one);
        view.performClick();

        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        bottomNavigationView();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, ContentsFragment.getInstance());
        transaction.commit();

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
                        toolbarTitle.setText("브랜드 컨텐츠");
                        selectedFragment = ContentsFragment.getInstance();
                        imgOption.setVisibility(View.VISIBLE);
                        imgOnlineOption.setVisibility(View.GONE);
                        break;

                    case R.id.action_two:
                        toolbarTitle.setText("브랜드 상품");
                        selectedFragment = GoodsFragment.getInstance();
                        imgOption.setVisibility(View.VISIBLE);
                        imgOnlineOption.setVisibility(View.GONE);
                        break;

                    case R.id.action_three:
                        toolbarTitle.setText("쇼핑몰 구매내역");
                        selectedFragment = OnlineFragment.getInstance();
                        imgOption.setVisibility(View.GONE);
                        imgOnlineOption.setVisibility(View.VISIBLE);
                        break;

                    case R.id.action_four:
                        toolbarTitle.setText("마이 페이지");
                        selectedFragment = MyPageFragment.getInstance();
                        imgOption.setVisibility(View.GONE);
                        imgOnlineOption.setVisibility(View.GONE);
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
