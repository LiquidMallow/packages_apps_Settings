/*
 *  Copyright (C) 2015 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.android.settings.mallow.ui;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.widget.Toast;

import com.android.settings.R;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.mallow.preference.SeekBarPreference;

public class LockScreen extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String TAG = "LockScreen";

    private static final String KEY_WALLPAPER_SET = "lockscreen_wallpaper_set";
    private static final String KEY_WALLPAPER_CLEAR = "lockscreen_wallpaper_clear";
	private static final String LOCKSCREEN_ALPHA = "lockscreen_alpha";
    private static final String LOCKSCREEN_SECURITY_ALPHA = "lockscreen_security_alpha";
	private static final String PREF_LS_BOUNCER = "lockscreen_bouncer";
	private static final String LOCKSCREEN_MAX_NOTIF_CONFIG = "lockscreen_max_notif_cofig";

    public static final int IMAGE_PICK = 1;

    private Preference mSetWallpaper;
    private Preference mClearWallpaper;
	private SeekBarPreference mLsAlpha;
    private SeekBarPreference mLsSecurityAlpha;
	private ListPreference mLsBouncer;
	private SeekBarPreference mMaxKeyguardNotifConfig;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lock_screen);

        mSetWallpaper = (Preference) findPreference(KEY_WALLPAPER_SET);
        mClearWallpaper = (Preference) findPreference(KEY_WALLPAPER_CLEAR);

		mLsBouncer = (ListPreference) findPreference(PREF_LS_BOUNCER);
        mLsBouncer.setOnPreferenceChangeListener(this);
        int lockbouncer = Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.LOCKSCREEN_BOUNCER, 0);
        mLsBouncer.setValue(String.valueOf(lockbouncer));
        updateBouncerSummary(lockbouncer);
		
		mLsAlpha = (SeekBarPreference) findPreference(LOCKSCREEN_ALPHA);
        float alpha = Settings.System.getFloat(getContentResolver(),
                Settings.System.LOCKSCREEN_ALPHA, 0.45f);
        mLsAlpha.setValue((int)(100 * alpha));
        mLsAlpha.setOnPreferenceChangeListener(this);

        mLsSecurityAlpha = (SeekBarPreference) findPreference(LOCKSCREEN_SECURITY_ALPHA);
        float alpha2 = Settings.System.getFloat(getContentResolver(),
                Settings.System.LOCKSCREEN_SECURITY_ALPHA, 0.75f);
        mLsSecurityAlpha.setValue((int)(100 * alpha2));
        mLsSecurityAlpha.setOnPreferenceChangeListener(this);
        
        mMaxKeyguardNotifConfig = (SeekBarPreference) findPreference(LOCKSCREEN_MAX_NOTIF_CONFIG);
        int kgconf = Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_MAX_NOTIF_CONFIG, 5);
        mMaxKeyguardNotifConfig.setValue(kgconf);
        mMaxKeyguardNotifConfig.setOnPreferenceChangeListener(this);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.DONT_TRACK_ME_BRO;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mSetWallpaper) {
            setKeyguardWallpaper();
            return true;
        } else if (preference == mClearWallpaper) {
            clearKeyguardWallpaper();
            Toast.makeText(getView().getContext(),
            getString(R.string.clear_lockscreen_wallpaper), Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

	@Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mLsBouncer) {
            int lockbouncer = Integer.valueOf((String) newValue);
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.LOCKSCREEN_BOUNCER, lockbouncer);
            updateBouncerSummary(lockbouncer);
            return true;
		} else if (preference == mLsAlpha) {
            int alpha = (Integer) newValue;
            Settings.System.putFloat(getContentResolver(),
                    Settings.System.LOCKSCREEN_ALPHA, alpha / 100.0f);
            return true;
        } else if (preference == mLsSecurityAlpha) {
            int alpha2 = (Integer) newValue;
            Settings.System.putFloat(getContentResolver(),
                    Settings.System.LOCKSCREEN_SECURITY_ALPHA, alpha2 / 100.0f);
            return true;
        } else if (preference == mMaxKeyguardNotifConfig) {
            int kgconf = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_MAX_NOTIF_CONFIG, kgconf);
            return true;
        }
        return false;
    }

	private void updateBouncerSummary(int value) {
        Resources res = getResources();
 
        if (value == 0) {
            // stock bouncer
            mLsBouncer.setSummary(res.getString(R.string.ls_bouncer_on_summary));
        } else if (value == 1) {
            // bypass bouncer
            mLsBouncer.setSummary(res.getString(R.string.ls_bouncer_off_summary));
        } else {
            String type = null;
            switch (value) {
                case 2:
                    type = res.getString(R.string.ls_bouncer_dismissable);
                    break;
                case 3:
                    type = res.getString(R.string.ls_bouncer_persistent);
                    break;
                case 4:
                    type = res.getString(R.string.ls_bouncer_all);
                    break;
            }
            // Remove title capitalized formatting
            type = type.toLowerCase();
            mLsBouncer.setSummary(res.getString(R.string.ls_bouncer_summary, type));
        }
    }

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                Intent intent = new Intent();
                intent.setClassName("com.android.wallpapercropper", "com.android.wallpapercropper.WallpaperCropActivity");
                intent.putExtra("keyguardMode", "1");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    private void setKeyguardWallpaper() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK);
    }

    private void clearKeyguardWallpaper() {
        WallpaperManager wallpaperManager = null;
        wallpaperManager = WallpaperManager.getInstance(getActivity());
        wallpaperManager.clearKeyguardWallpaper();
    }
}
