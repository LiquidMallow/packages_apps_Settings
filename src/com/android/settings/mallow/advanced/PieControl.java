/*
 * Copyright (C) 2010-2015 ParanoidAndroid Project
 * Portions Copyright (C) 2015 Fusion & Cyanidel Project
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
import android.provider.Settings;

import com.android.settings.R;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.SettingsPreferenceFragment;

public class PieControl extends SettingsPreferenceFragment
       implements OnPreferenceChangeListener {

    private static final String TAG = "PieControl";

    private static final String PA_PIE_SIZE = "pa_pie_size";
    private static final String PA_PIE_GRAVITY = "pa_pie_gravity";
    private static final String PA_PIE_MODE = "pa_pie_mode";
    private static final String PA_PIE_ANGLE = "pa_pie_angle";
    private static final String PA_PIE_GAP = "pa_pie_gap";

    private ListPreference mPieSize;
    private ListPreference mPieGravity;
    private ListPreference mPieMode;
    private ListPreference mPieAngle;
    private ListPreference mPieGap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pie_control);

        mPieSize = (ListPreference) findPreference(PA_PIE_SIZE);
            float pieSize = Settings.System.getFloat(getContentResolver(),
                    Settings.System.PA_PIE_SIZE, 1.0f);
            mPieSize.setValue(String.valueOf(pieSize));
        mPieSize.setOnPreferenceChangeListener(this);

        mPieGravity = (ListPreference) findPreference(PA_PIE_GRAVITY);
        int pieGravity = Settings.System.getInt(getContentResolver(),
                Settings.System.PA_PIE_GRAVITY, 2);
        mPieGravity.setValue(String.valueOf(pieGravity));
        mPieGravity.setOnPreferenceChangeListener(this);

        mPieMode = (ListPreference) findPreference(PA_PIE_MODE);
        int pieMode = Settings.System.getInt(getContentResolver(),
                Settings.System.PA_PIE_MODE, 2);
        mPieMode.setValue(String.valueOf(pieMode));
        mPieMode.setOnPreferenceChangeListener(this);

        mPieGap = (ListPreference) findPreference(PA_PIE_GAP);
        int pieGap = Settings.System.getInt(getContentResolver(),
                Settings.System.PA_PIE_GAP, 2);
        mPieGap.setValue(String.valueOf(pieGap));
        mPieGap.setOnPreferenceChangeListener(this);

        mPieAngle = (ListPreference) findPreference(PA_PIE_ANGLE);
        int pieAngle = Settings.System.getInt(getContentResolver(),
                Settings.System.PA_PIE_ANGLE, 12);
        mPieAngle.setValue(String.valueOf(pieAngle));
        mPieAngle.setOnPreferenceChangeListener(this);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.DONT_TRACK_ME_BRO;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPieSize) {
            float pieSize = Float.valueOf((String) newValue);
            Settings.System.putFloat(getContentResolver(),
                    Settings.System.PA_PIE_SIZE, pieSize);
            return true;
        } else if (preference == mPieGravity) {
            int pieGravity = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PA_PIE_GRAVITY, pieGravity);
            return true;
        } else if (preference == mPieMode) {
            int pieMode = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PA_PIE_MODE, pieMode);
            return true;
        } else if (preference == mPieAngle) {
            int pieAngle = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PA_PIE_ANGLE, pieAngle);
            return true;
        } else if (preference == mPieGap) {
            int pieGap = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PA_PIE_GAP, pieGap);
            return true;
        }
        return false;
    }
}
