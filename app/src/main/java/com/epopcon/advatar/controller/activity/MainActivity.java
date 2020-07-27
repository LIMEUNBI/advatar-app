package com.epopcon.advatar.controller.activity;

import android.app.Activity;
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
import com.epopcon.advatar.controller.fragment.ContentsFragment;
import com.epopcon.advatar.controller.fragment.GoodsFragment;
import com.epopcon.advatar.controller.fragment.MyPageFragment;
import com.epopcon.advatar.controller.fragment.ReviewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

public class MainActivity extends BaseActivity {

    BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment = null;

    private TextView toolbarTitle;
    private ImageView imgOption;

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
                        break;

                    case R.id.action_two:
                        toolbarTitle.setText("브랜드 상품");
                        selectedFragment = GoodsFragment.getInstance();
                        break;

                    case R.id.action_three:
                        toolbarTitle.setText("브랜드 평가");
                        selectedFragment = ReviewFragment.getInstance();
                        break;

                    case R.id.action_four:
                        toolbarTitle.setText("구매 내역");
                        selectedFragment = MyPageFragment.getInstance();
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
