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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.SystemProperties;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.preference.SlimSeekBarPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
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
    private static final String PREF_VOLUME_DIALOG_STROKE = "volume_dialog_stroke";
    private static final String PREF_VOLUME_DIALOG_STROKE_COLOR = "volume_dialog_stroke_color";
    private static final String PREF_VOLUME_DIALOG_STROKE_THICKNESS = "volume_dialog_stroke_thickness";
    private static final String PREF_VOLUME_DIALOG_CORNER_RADIUS = "volume_dialog_corner_radius";
    private static final String KEY_VOLUME_DIALOG_TIMEOUT = "volume_dialog_timeout";

    private SwitchPreference mCameraSounds;
    private SwitchPreference mVolumeRockerWake;
    private SwitchPreference mVolBtnMusicCtrl;
    private SwitchPreference mSafeHeadsetVolume;
    private ListPreference mAnnoyingNotifications;
    private SeekBarPreferenceCHOS mVolumeDialogAlpha;
    private ListPreference mVolumeDialogStroke;
    private ColorPickerPreference mVolumeDialogStrokeColor;
    private SeekBarPreferenceCHOS mVolumeDialogStrokeThickness;
    private SeekBarPreferenceCHOS mVolumeDialogCornerRadius;
    private SlimSeekBarPreference mVolumeDialogTimeout;

    static final int DEFAULT_VOLUME_DIALOG_STROKE_COLOR = 0xFF80CBC4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.sound_settings);

        // Camera shutter sound
        mCameraSounds = (SwitchPreference) findPreference(KEY_CAMERA_SOUNDS);
        mCameraSounds.setChecked(SystemProperties.getBoolean(PROP_CAMERA_SOUND, true));
        mCameraSounds.setOnPreferenceChangeListener(this);

        // Volume rocker wake
        mVolumeRockerWake = (SwitchPreference) findPreference(VOLUME_ROCKER_WAKE);
        mVolumeRockerWake.setOnPreferenceChangeListener(this);
        int volumeRockerWake = Settings.System.getInt(getContentResolver(),
                VOLUME_ROCKER_WAKE, 0);
        mVolumeRockerWake.setChecked(volumeRockerWake != 0);

        // Volume button music control
        mVolBtnMusicCtrl = (SwitchPreference) findPreference(KEY_VOLBTN_MUSIC_CTRL);
        mVolBtnMusicCtrl.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.VOLBTN_MUSIC_CONTROLS, 0) != 0);
        mVolBtnMusicCtrl.setOnPreferenceChangeListener(this);

        try {
            if (Settings.System.getInt(getContentResolver(),
                    Settings.System.VOLUME_ROCKER_WAKE) == 1) {
                mVolBtnMusicCtrl.setEnabled(false);
                mVolBtnMusicCtrl.setSummary(R.string.volume_button_toggle_info);
            }
        } catch (SettingNotFoundException e) {
        }

        // Safe headset volume
        mSafeHeadsetVolume = (SwitchPreference) findPreference(KEY_SAFE_HEADSET_VOLUME);
        mSafeHeadsetVolume.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.SAFE_HEADSET_VOLUME, 1) != 0);
        mSafeHeadsetVolume.setOnPreferenceChangeListener(this);

        // Mute annoying notifications
        mAnnoyingNotifications = (ListPreference) findPreference(PREF_LESS_NOTIFICATION_SOUNDS);
        int notificationThreshold = Settings.System.getInt(getContentResolver(),
                Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD, 0);
        mAnnoyingNotifications.setValue(Integer.toString(notificationThreshold));
        mAnnoyingNotifications.setOnPreferenceChangeListener(this);

	    // Volume dialog alpha
        mVolumeDialogAlpha =
                (SeekBarPreferenceCHOS) findPreference(PREF_TRANSPARENT_VOLUME_DIALOG);
        int volumeDialogAlpha = Settings.System.getInt(getContentResolver(),
                Settings.System.TRANSPARENT_VOLUME_DIALOG, 255);
        mVolumeDialogAlpha.setValue(volumeDialogAlpha / 1);
        mVolumeDialogAlpha.setOnPreferenceChangeListener(this);

	    // Volume dialog stroke
        mVolumeDialogStroke = (ListPreference) findPreference(PREF_VOLUME_DIALOG_STROKE);
        int volumeDialogStroke = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.VOLUME_DIALOG_STROKE, 1, UserHandle.USER_CURRENT);
        mVolumeDialogStroke.setValue(String.valueOf(volumeDialogStroke));
        mVolumeDialogStroke.setSummary(mVolumeDialogStroke.getEntry());
        mVolumeDialogStroke.setOnPreferenceChangeListener(this);

        // Volume dialog stroke color
        mVolumeDialogStrokeColor =
                (ColorPickerPreference) findPreference(PREF_VOLUME_DIALOG_STROKE_COLOR);
        mVolumeDialogStrokeColor.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_DIALOG_STROKE_COLOR, DEFAULT_VOLUME_DIALOG_STROKE_COLOR);
        String hexColor = String.format("#%08x", (0xFF80CBC4 & intColor));
        mVolumeDialogStrokeColor.setSummary(hexColor);
        mVolumeDialogStrokeColor.setNewPreviewColor(intColor);

        // Volume dialog stroke thickness
        mVolumeDialogStrokeThickness =
                (SeekBarPreferenceCHOS) findPreference(PREF_VOLUME_DIALOG_STROKE_THICKNESS);
        int volumeDialogStrokeThickness = Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_DIALOG_STROKE_THICKNESS, 4);
        mVolumeDialogStrokeThickness.setValue(volumeDialogStrokeThickness / 1);
        mVolumeDialogStrokeThickness.setOnPreferenceChangeListener(this);

	    // Volume dialog corner radius
        mVolumeDialogCornerRadius =
                (SeekBarPreferenceCHOS) findPreference(PREF_VOLUME_DIALOG_CORNER_RADIUS);
        int volumeDialogCornerRadius = Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_DIALOG_CORNER_RADIUS, 10);
        mVolumeDialogCornerRadius.setValue(volumeDialogCornerRadius / 1);
        mVolumeDialogCornerRadius.setOnPreferenceChangeListener(this);

        VolumeDialogSettingsDisabler(volumeDialogStroke);

        // Volume dialog timeout seekbar
        mVolumeDialogTimeout = 
                (SlimSeekBarPreference) findPreference(KEY_VOLUME_DIALOG_TIMEOUT);
        mVolumeDialogTimeout.setDefault(3000);
        mVolumeDialogTimeout.isMilliseconds(true);
        mVolumeDialogTimeout.setInterval(1);
        mVolumeDialogTimeout.minimumValue(100);
        mVolumeDialogTimeout.multiplyValue(100);
        mVolumeDialogTimeout.setOnPreferenceChangeListener(this);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.DONT_TRACK_ME_BRO;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateState();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        if (preference == mVolumeRockerWake) {
            boolean value = (Boolean) objValue;
                Settings.System.putInt(getContentResolver(),
                    VOLUME_ROCKER_WAKE, value ? 1 : 0);
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
	    } else if (preference == mVolumeDialogStroke) {
            int volumeDialogStroke = Integer.parseInt((String) objValue);
            int index = mVolumeDialogStroke.findIndexOfValue((String) objValue);
                Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.VOLUME_DIALOG_STROKE,
                    volumeDialogStroke, UserHandle.USER_CURRENT);
            mVolumeDialogStroke.setSummary(mVolumeDialogStroke.getEntries()[index]);
            VolumeDialogSettingsDisabler(volumeDialogStroke);
            return true;
        } else if (preference == mVolumeDialogStrokeColor) {
            String hex = ColorPickerPreference.convertToARGB(
            Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_DIALOG_STROKE_COLOR, intHex);
            return true;
        } else if (preference == mVolumeDialogStrokeThickness) {
            int val = (Integer) objValue;
                Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_DIALOG_STROKE_THICKNESS, val * 1);
            return true;
	    } else if (preference == mVolumeDialogCornerRadius) {
            int val = (Integer) objValue;
                Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_DIALOG_CORNER_RADIUS, val * 1);
            return true;
        } else if (preference == mVolumeDialogTimeout) {
            int volumeDialogTimeout = Integer.valueOf((String) objValue);
                 Settings.System.putInt(getContentResolver(),
                     Settings.System.VOLUME_DIALOG_TIMEOUT, volumeDialogTimeout);
        }
        return false;
    }

    private void VolumeDialogSettingsDisabler(int volumeDialogStroke) {
        if (volumeDialogStroke == 0) {
            mVolumeDialogStrokeColor.setEnabled(false);
            mVolumeDialogStrokeThickness.setEnabled(false);
        } else if (volumeDialogStroke == 1) {
            mVolumeDialogStrokeColor.setEnabled(false);
            mVolumeDialogStrokeThickness.setEnabled(true);
        } else {
            mVolumeDialogStrokeColor.setEnabled(true);
            mVolumeDialogStrokeThickness.setEnabled(true);
        }
    }

    private void updateState() {
        final Activity activity = getActivity();

        if (mVolumeDialogTimeout != null) {
            final int volumeDialogTimeout = Settings.System.getInt(getContentResolver(),
                    Settings.System.VOLUME_DIALOG_TIMEOUT, 3000);
            // minimum 100 is 1 interval of the 100 multiplier
            mVolumeDialogTimeout.setInitValue((volumeDialogTimeout / 100) - 1);
        }
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
