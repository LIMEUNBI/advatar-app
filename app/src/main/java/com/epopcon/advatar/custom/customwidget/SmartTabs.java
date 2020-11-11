package com.epopcon.advatar.custom.customwidget;

import com.epopcon.advatar.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

public enum SmartTabs {

    ONLINE_TAB(R.string.online_title, R.layout.viewpager_input) {
        @Override
        public int[] onlineTabs() {
            return onlineTab();
        }
    },
    ONLINE_STORE_TAB(R.string.online_store_title, R.layout.viewpager_input) {
        @Override
        public int[] onlineStoreTabs() {
            return onlineStoreTab();
        }
    };

    public final int titleResId;
    public final int layoutResId;

    SmartTabs(int titleResId, int layoutResId) {
        this.titleResId = titleResId;
        this.layoutResId = layoutResId;
    }

    public int[] onlineTabs() {
        return onlineTabs();
    }
    public static int[] onlineTab() {
        return new int[] {
                R.string.online_tab_payment,
                R.string.online_tab_cart
        };
    }

    public int[] onlineStoreTabs() {
        return onlineStoreTabs();
    }

    public static int[] onlineStoreTab() {
        return new int[] {
            R.string.online_11st,
            R.string.online_gmarket,
            R.string.online_auction,
            R.string.online_naverstore,
            R.string.online_coupang,
            R.string.online_tmon,
            R.string.online_wemap,
            R.string.online_interpark
        };
    }

    public void setup(final SmartTabLayout layout) {
        //Do nothing.
    }

}
