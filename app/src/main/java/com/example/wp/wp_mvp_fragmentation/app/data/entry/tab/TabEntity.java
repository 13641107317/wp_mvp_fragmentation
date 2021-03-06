package com.example.wp.wp_mvp_fragmentation.app.data.entry.tab;

import com.flyco.tablayout.listener.CustomTabEntity;

public class TabEntity implements CustomTabEntity {
    private String  mTabTitle;
    private int mTabSelectedIcon;
    private int mTabUnSelectedIcon;

    public TabEntity(String tabTitle, int tabSelectedIcon, int tabUnSelectedIcon) {
        mTabTitle = tabTitle;
        mTabSelectedIcon = tabSelectedIcon;
        mTabUnSelectedIcon = tabUnSelectedIcon;
    }
    @Override
    public String getTabTitle() {
        return mTabTitle;
    }

    @Override
    public int getTabSelectedIcon() {
        return mTabSelectedIcon;
    }

    @Override
    public int getTabUnselectedIcon() {
        return mTabUnSelectedIcon;
    }
}
