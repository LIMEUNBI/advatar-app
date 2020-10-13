package com.epopcon.advatar.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.controller.activity.MainActivity;
import com.epopcon.advatar.controller.fragment.online.CartFragment;
import com.epopcon.advatar.controller.fragment.online.PaymentFragment;
import com.epopcon.advatar.custom.customwidget.CustomViewPager;
import com.epopcon.advatar.custom.customwidget.SmartTabs;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class OnlineFragment extends BaseFragment {

    public static final String KEY = "connection";
    public static final String INDEX = "startIndex";
    public static final String DISPLAY = "display";

    public static final int DISPLAY_ALL          = 0;

    private CustomViewPager mViewPager = null;
    private int mCurrIndex = 0;

    private View mView = null;
    private long mRowId = -1L;

    private FragmentPagerItemAdapter adapter = null;

    public static void startActivity(Context context, SmartTabs sTab, int startTabIndex, int displayType) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY, sTab.name());
        intent.putExtra(INDEX, startTabIndex);
        intent.putExtra(DISPLAY, displayType);
        context.startActivity(intent);
    }

    private static OnlineFragment instance = null;

    public static OnlineFragment getInstance() {
        if (instance == null) {
            instance = new OnlineFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_onlinestore, container, false);
        mRowId = getActivity().getIntent().getLongExtra(Config.ROW_ID, -1L);

        int displayType = getActivity().getIntent().getIntExtra(DISPLAY, DISPLAY_ALL);
        int startTabIndex = getActivity().getIntent().getIntExtra(INDEX, 0);
        mCurrIndex = startTabIndex;

        // Create sub tab.
        final SmartTabs sTab = SmartTabs.values()[0];

        ViewGroup tab = (ViewGroup) mView.findViewById(R.id.tab);
        tab.addView(LayoutInflater.from(getActivity()).inflate(sTab.layoutResId, tab, false));
        if (displayType != DISPLAY_ALL) {
            tab.setVisibility(View.GONE);
        }

        mViewPager = (CustomViewPager) mView.findViewById(R.id.viewpager);
        SmartTabLayout viewPagerTab = (SmartTabLayout) mView.findViewById(R.id.smart_tab_layout);
        sTab.setup(viewPagerTab);

        FragmentPagerItems pages = new FragmentPagerItems(getContext());
        mViewPager.setOffscreenPageLimit(2);

        pages.add(FragmentPagerItem.of(getString(sTab.onlineTabs()[0]), PaymentFragment.class));
        pages.add(FragmentPagerItem.of(getString(sTab.onlineTabs()[1]), CartFragment.class));

        adapter = new FragmentPagerItemAdapter(getChildFragmentManager(), pages);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(startTabIndex);
        viewPagerTab.setViewPager(mViewPager);

        // Report to Google Analytics
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrIndex = position;
                refresh();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh() {
        if (getChildFragmentManager().getFragments() != null) {
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment instanceof PaymentFragment && mCurrIndex == 0) {
                    ((PaymentFragment) fragment).refresh();
                    break;
                } else if (fragment instanceof CartFragment && mCurrIndex == 1) {
                    ((CartFragment) fragment).refresh();
                    break;
                }
            }
        }
    }

    protected void showSoftKeyboard() {
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(getActivity().getCurrentFocus(), InputMethodManager.SHOW_FORCED);
    }

    protected void hideSoftKeyboard(View view) {
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
