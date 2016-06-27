/*
* Copyright (C) 2015 MallowRom
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.android.settings.mallow.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.R;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.PagerSlidingTabStrip;

import com.android.settings.mallow.advanced.AppSidebar;
import com.android.settings.mallow.advanced.GestureAnywhere;
import com.android.settings.mallow.advanced.PieControl;
import com.android.settings.mallow.navigation.NavigationBar;
import com.android.settings.mallow.statusbar.BatteryStyles;
import com.android.settings.mallow.statusbar.ClockStyles;
import com.android.settings.mallow.statusbar.NotificationDrawer;
import com.android.settings.mallow.statusbar.StatusBar;
import com.android.settings.mallow.ui.HeadsUp;
import com.android.settings.mallow.ui.LockScreen;
import com.android.settings.mallow.ui.PowerMenu;
import com.android.settings.mallow.ui.RecentPanel;

public class MallowTweaks extends SettingsPreferenceFragment {

    ViewPager mViewPager;
    String titleString[];
    ViewGroup mContainer;
    PagerSlidingTabStrip mTabs;
    static Bundle mSavedState;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainer = container;

        View view = inflater.inflate(R.layout.tab_ui, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        StatusBarAdapter StatusBarAdapter = new StatusBarAdapter(getFragmentManager());
        mViewPager.setAdapter(StatusBarAdapter);
        mTabs.setViewPager(mViewPager);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.DONT_TRACK_ME_BRO;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    class StatusBarAdapter extends FragmentPagerAdapter {
        String titles[] = getTitles();
        private Fragment frags[] = new Fragment[titles.length];

        public StatusBarAdapter(FragmentManager fm) {
            super(fm);
            frags[0] = new AppSidebar();
            frags[1] = new BatteryStyles();
            frags[2] = new ClockStyles();
            frags[3] = new GestureAnywhere();
            frags[4] = new HeadsUp();
            frags[5] = new LockScreen();
            frags[6] = new NavigationBar();
            frags[7] = new NotificationDrawer();
            frags[8] = new PieControl();
            frags[9] = new PowerMenu();
            frags[10] = new RecentPanel();
            frags[11] = new StatusBar();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return frags[position];
        }

        @Override
        public int getCount() {
            return frags.length;
        }
    }

    private String[] getTitles() {
        String titleString[];
        titleString = new String[]{
        	    getString(R.string.app_sidebar_title),
        	    getString(R.string.battery_styles_title),
                getString(R.string.clock_styles_title),
                getString(R.string.gesture_anywhere_title),
                getString(R.string.heads_up_title),
                getString(R.string.lock_screen_title),
                getString(R.string.navigation_bar_title),
                getString(R.string.notification_drawer_title),
                getString(R.string.pie_control_title),
                getString(R.string.power_menu_title),
                getString(R.string.recent_panel_title),
                getString(R.string.status_bar_title)};
        return titleString;
    }
}
