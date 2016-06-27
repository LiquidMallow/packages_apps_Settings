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

package com.android.settings.mallow.statusbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.Spannable;
import android.text.TextUtils;
import android.widget.EditText;

import com.android.settings.R;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.mallow.preference.SeekBarPreferenceCHOS;

public class StatusBar extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBar";

    private static final String STATUS_BAR_CARRIER = "status_bar_carrier";
    private static final String CUSTOM_CARRIER_LABEL = "custom_carrier_label";
    private static final String CUSTOM_HEADER_IMAGE_SHADOW = "status_bar_custom_header_shadow";

    private ListPreference mShowCarrierLabel;
    private PreferenceScreen mCustomCarrierLabel;
    private SeekBarPreferenceCHOS mHeaderShadow;

    private String mCustomCarrierLabelText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.status_bar);

        mShowCarrierLabel = (ListPreference) findPreference(STATUS_BAR_CARRIER);
        int showCarrierLabel = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_CARRIER, 1);
        mShowCarrierLabel.setValue(String.valueOf(showCarrierLabel));
        mShowCarrierLabel.setSummary(mShowCarrierLabel.getEntry());
        mShowCarrierLabel.setOnPreferenceChangeListener(this);
        mCustomCarrierLabel = (PreferenceScreen) findPreference(CUSTOM_CARRIER_LABEL);

        updatepreferences();
        updateCustomLabelTextSummary();

        // Status bar custom header shadow
        mHeaderShadow = (SeekBarPreferenceCHOS) findPreference(CUSTOM_HEADER_IMAGE_SHADOW);
        int headerShadow = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CUSTOM_HEADER_SHADOW, 0);
        mHeaderShadow.setValue(headerShadow);
        mHeaderShadow.setOnPreferenceChangeListener(this);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.DONT_TRACK_ME_BRO;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatepreferences();
    }

    private void updatepreferences() {
        boolean carrierlabel = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_CARRIER, 0) !=0;

        if (carrierlabel) {
            mCustomCarrierLabel.setEnabled(true);
        } else {
            mCustomCarrierLabel.setEnabled(false);
        }
    }

    private void updateCustomLabelTextSummary() {
        mCustomCarrierLabelText = Settings.System.getString(getContentResolver(),
                Settings.System.CUSTOM_CARRIER_LABEL);

        if (TextUtils.isEmpty(mCustomCarrierLabelText)) {
            mCustomCarrierLabel.setSummary(R.string.custom_carrier_label_notset);
        } else {
            mCustomCarrierLabel.setSummary(mCustomCarrierLabelText);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
	    if (preference == mHeaderShadow) {
            int headerShadow = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_CUSTOM_HEADER_SHADOW, headerShadow);
            return true;
        } else if (preference == mShowCarrierLabel) {
            int showCarrierLabel = Integer.valueOf((String) newValue);
            int index = mShowCarrierLabel.findIndexOfValue((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_CARRIER, showCarrierLabel);
            mShowCarrierLabel.setSummary(mShowCarrierLabel.getEntries()[index]);
            updatepreferences();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            final Preference preference) {
        if (preference.getKey().equals(CUSTOM_CARRIER_LABEL)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.custom_carrier_label_title);
            alert.setMessage(R.string.custom_carrier_label_explain);

            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setText(TextUtils.isEmpty(mCustomCarrierLabelText) ? "" : mCustomCarrierLabelText);
            input.setSelection(input.getText().length());
            alert.setView(input);
            alert.setPositiveButton(getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = ((Spannable) input.getText()).toString().trim();
                            Settings.System.putString(getContentResolver(),
                                    Settings.System.CUSTOM_CARRIER_LABEL, value);
                            updateCustomLabelTextSummary();
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_CUSTOM_CARRIER_LABEL_CHANGED);
                            getActivity().sendBroadcast(i);
                }
            });
            alert.setNegativeButton(getString(android.R.string.cancel), null);
            alert.show();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
