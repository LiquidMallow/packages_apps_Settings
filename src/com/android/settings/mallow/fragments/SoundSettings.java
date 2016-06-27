/*
 * Copyright (C) 2015 MallowRom
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

package com.android.settings.mallow.fragments;

import com.android.internal.logging.MetricsLogger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.mallow.preference.SeekBarPreferenceCHOS;

public class SoundSettings extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String TAG = "SoundSettings";

    private static final int DLG_SAFE_HEADSET_VOLUME = 0;

    private static final String VOLUME_ROCKER_WAKE = "volume_rocker_wake";
    private static final String KEY_VOLBTN_MUSIC_CTRL = "volbtn_music_controls";
    private static final String KEY_CAMERA_SOUNDS = "camera_sounds";
    private static final String PROP_CAMERA_SOUND = "persist.sys.camera-sound";
    private static final String KEY_SAFE_HEADSET_VOLUME = "safe_headset_volume";
    private static final String PREF_LESS_NOTIFICATION_SOUNDS = "less_notification_sounds";
    private static final String PREF_TRANSPARENT_VOLUME_DIALOG = "transparent_volume_dialog";

    private SwitchPreference mCameraSounds;
    private SwitchPreference mVolumeRockerWake;
    private SwitchPreference mVolBtnMusicCtrl;
    private SwitchPreference mSafeHeadsetVolume;
    private ListPreference mAnnoyingNotifications;
    private SeekBarPreferenceCHOS mVolumeDialogAlpha;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.sound_settings);

        // camera shutter sound
        mCameraSounds = (SwitchPreference) findPreference(KEY_CAMERA_SOUNDS);
        mCameraSounds.setChecked(SystemProperties.getBoolean(PROP_CAMERA_SOUND, true));
        mCameraSounds.setOnPreferenceChangeListener(this);

        // volume rocker wake
        mVolumeRockerWake = (SwitchPreference) findPreference(VOLUME_ROCKER_WAKE);
        mVolumeRockerWake.setOnPreferenceChangeListener(this);
        int volumeRockerWake = Settings.System.getInt(getContentResolver(),
                VOLUME_ROCKER_WAKE, 0);
        mVolumeRockerWake.setChecked(volumeRockerWake != 0);

        // volume music control
        mVolBtnMusicCtrl = (SwitchPreference) findPreference(KEY_VOLBTN_MUSIC_CTRL);
        mVolBtnMusicCtrl.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.VOLBTN_MUSIC_CONTROLS, 1) != 0);
        mVolBtnMusicCtrl.setOnPreferenceChangeListener(this);
        try {
            if (Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.VOLUME_ROCKER_WAKE) == 1) {
                mVolBtnMusicCtrl.setEnabled(false);
                mVolBtnMusicCtrl.setSummary(R.string.volume_button_toggle_info);
            }
        } catch (SettingNotFoundException e) {
        }

        mSafeHeadsetVolume = (SwitchPreference) findPreference(KEY_SAFE_HEADSET_VOLUME);
        mSafeHeadsetVolume.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.SAFE_HEADSET_VOLUME, 1) != 0);
        mSafeHeadsetVolume.setOnPreferenceChangeListener(this);

        mAnnoyingNotifications = (ListPreference) findPreference(PREF_LESS_NOTIFICATION_SOUNDS);
        int notificationThreshold = Settings.System.getInt(getContentResolver(),
                Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD, 0);
        mAnnoyingNotifications.setValue(Integer.toString(notificationThreshold));
        mAnnoyingNotifications.setOnPreferenceChangeListener(this);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.DONT_TRACK_ME_BRO;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        if (preference == mVolumeRockerWake) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(), VOLUME_ROCKER_WAKE,
                    value ? 1 : 0);
            return true;
        } else if (preference == mVolBtnMusicCtrl) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLBTN_MUSIC_CONTROLS,
                    (Boolean) objValue ? 1 : 0);
            return true;
        } else if (KEY_CAMERA_SOUNDS.equals(key)) {
            final String value = ((Boolean) objValue) ? "1" : "0";
            SystemProperties.set(PROP_CAMERA_SOUND, value);
            return true;
        } else if (KEY_SAFE_HEADSET_VOLUME.equals(key)) {
            if ((Boolean) objValue) {
                Settings.System.putInt(getContentResolver(),
                    Settings.System.SAFE_HEADSET_VOLUME, 1);
            } else {
                showDialogInner(DLG_SAFE_HEADSET_VOLUME);
            }
            return true;
        } else if (PREF_LESS_NOTIFICATION_SOUNDS.equals(key)) {
            final int val = Integer.valueOf((String) objValue);
            Settings.System.putInt(getContentResolver(),
                Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD, val);
            return true;
        } else if (preference == mVolumeDialogAlpha) {
            int alpha = (Integer) objValue;
            Settings.System.putInt(getContentResolver(),
                Settings.System.TRANSPARENT_VOLUME_DIALOG, alpha * 1);
            return true;  
        }
        return false;
    }

    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        SoundSettings getOwner() {
            return (SoundSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_SAFE_HEADSET_VOLUME:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.attention)
                    .setMessage(R.string.safe_headset_volume_warning_dialog_text)
                    .setPositiveButton(R.string.dlg_ok,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().getContentResolver(),
                                    Settings.System.SAFE_HEADSET_VOLUME, 0);

                        }
                    })
                    .setNegativeButton(R.string.dlg_cancel,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_SAFE_HEADSET_VOLUME:
                    getOwner().mSafeHeadsetVolume.setChecked(true);
                    break;
            }
        }
    }
}
