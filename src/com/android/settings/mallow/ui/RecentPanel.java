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

package com.android.settings.mallow.ui;

import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.SettingsPreferenceFragment;

public class RecentPanel extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener  {

    private static final String TAG = "RecentPanel";

    private static final String SHOW_CLEAR_ALL_RECENTS = "show_clear_all_recents";
    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";
    private static final String IMMERSIVE_RECENTS = "immersive_recents";

    private SwitchPreference mRecentsClearAll;
    private ListPreference mRecentsClearAllLocation;
    private ListPreference mImmersiveRecents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.recent_panel);

        mRecentsClearAll = (SwitchPreference) findPreference(SHOW_CLEAR_ALL_RECENTS);
        mRecentsClearAll.setChecked(Settings.System.getIntForUser(getContentResolver(),
                Settings.System.SHOW_CLEAR_ALL_RECENTS, 1, UserHandle.USER_CURRENT) == 1);
        mRecentsClearAll.setOnPreferenceChangeListener(this);

        mRecentsClearAllLocation = (ListPreference) findPreference(RECENTS_CLEAR_ALL_LOCATION);
        int location = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3, UserHandle.USER_CURRENT);
        mRecentsClearAllLocation.setValue(String.valueOf(location));
        mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntry());
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this);

        mImmersiveRecents = (ListPreference) findPreference(IMMERSIVE_RECENTS);
        mImmersiveRecents.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.IMMERSIVE_RECENTS, 0)));
        mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
        mImmersiveRecents.setOnPreferenceChangeListener(this);	
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.DONT_TRACK_ME_BRO;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mRecentsClearAll) {
            boolean show = (Boolean) newValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.SHOW_CLEAR_ALL_RECENTS,
                    show ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mRecentsClearAllLocation) {
            int location = Integer.valueOf((String) newValue);
            int index = mRecentsClearAllLocation.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION,
                    location, UserHandle.USER_CURRENT);
            mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntries()[index]);
            return true;
        } else if (preference == mImmersiveRecents) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.IMMERSIVE_RECENTS,
                    Integer.valueOf((String) newValue));
            mImmersiveRecents.setValue(String.valueOf(newValue));
            mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
            return true;
        }
        return false;
    }
}
