package com.smartman.redpaperhelper.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.provider.Settings;

import com.smartman.redpaperhelper.R;
import com.smartman.redpaperhelper.utils.PrefsUtil;

/**
 * Created by jiahui.chen on 2015/10/27.
 */
public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "SettingFragment";
    private EditTextPreference editTextPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);
        initData();
    }

    private void initData() {
        if (PrefsUtil.loadPrefBoolean("reply_words", false)) {
            editTextPreference = (EditTextPreference) findPreference("thanks_words");
            editTextPreference.setEnabled(true);
            editTextPreference.setTitle(editTextPreference.getText());
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        PrefsUtil.getPref().registerOnSharedPreferenceChangeListener(this);
        initData();
    }

    @Override
    public void onStop() {
        super.onStop();
        PrefsUtil.getPref().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "robpaper":
                //跳到辅助功能
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                break;

            case "reply_money":
                break;

            case "reply_person":
                break;

            case "reply_words":
                if (PrefsUtil.getPref().getBoolean("reply_words", false)) {
                    editTextPreference = (EditTextPreference) findPreference("thanks_words");
                    editTextPreference.setEnabled(true);
                } else {
                    editTextPreference = (EditTextPreference) findPreference("thanks_words");
                    editTextPreference.setEnabled(false);
                }
                break;

            case "thanks_words":
                editTextPreference = (EditTextPreference) findPreference("thanks_words");
                editTextPreference.setTitle(editTextPreference.getText());
                break;
            default:
                break;
        }
    }
}
