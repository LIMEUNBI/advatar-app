package com.epopcon.advatar.custom.customwidget;

import com.epopcon.advatar.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

public enum SmartTabs {

    ONLINE_TAB(R.string.online_title, R.layout.viewpager_input) {
        @Override
        public int[] onlineTabs() {
            return onlineTab();
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

    public void setup(final SmartTabLayout layout) {
        //Do nothing.
    }

}
