/*
 * Copyright (C) 2010-2015 ParanoidAndroid Project
 * Portions Copyright (C) 2015 Fusion & CyanideL Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.mallow.advanced;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.SettingsPreferenceFragment;

public class PieTargets extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String TAG = "PieTargets";

    private static final String PA_PIE_MENU = "pa_pie_menu";
    private static final String PA_PIE_LASTAPP = "pa_pie_lastapp";
    private static final String PA_PIE_KILLTASK = "pa_pie_killtask";
    private static final String PA_PIE_SCREENSHOT = "pa_pie_screenshot";

    private SwitchPreference mPieMenu;
    private SwitchPreference mPieLastApp;
    private SwitchPreference mPieKillTask;
    private SwitchPreference mPieScreenshot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pie_targets);

        mPieMenu = (SwitchPreference) findPreference(PA_PIE_MENU);
        mPieMenu.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.PA_PIE_MENU, 0) != 0);

        mPieLastApp = (SwitchPreference) findPreference(PA_PIE_LASTAPP);
        mPieLastApp.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.PA_PIE_LAST_APP, 0) != 0);

        mPieKillTask = (SwitchPreference) findPreference(PA_PIE_KILLTASK);
        mPieKillTask.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.PA_PIE_KILL_TASK, 0) != 0);

        mPieScreenshot = (SwitchPreference) findPreference(PA_PIE_SCREENSHOT);
        mPieScreenshot.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.PA_PIE_SCREENSHOT, 0) != 0);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.DONT_TRACK_ME_BRO;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mPieMenu) {
            value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PA_PIE_MENU,
                    value ? 1 : 0);
            return true;
        } else if (preference == mPieLastApp) {
            value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PA_PIE_LAST_APP,
                    value ? 1 : 0);
            return true;
        } else if (preference == mPieKillTask) {
            value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PA_PIE_KILL_TASK,
                    value ? 1 : 0);
            return true;
        } else if (preference == mPieScreenshot) {
            value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PA_PIE_SCREENSHOT,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }
}
