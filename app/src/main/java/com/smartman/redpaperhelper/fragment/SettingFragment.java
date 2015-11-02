package com.smartman.redpaperhelper.fragment;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;

import com.smartman.redpaperhelper.R;
import com.smartman.redpaperhelper.activity.MainActivity;
import com.smartman.redpaperhelper.application.MyApplication;
import com.smartman.redpaperhelper.utils.AccessibilityServiceUtil;
import com.smartman.redpaperhelper.utils.PrefsUtil;

/**
 * Created by jiahui.chen on 2015/10/27.
 */
public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "SettingFragment";
    private EditTextPreference editTextPreference;
    private SwitchPreference switchPreference;
    private Boolean notGoSetting = false;
    KeyguardManager.KeyguardLock keyguardLock;
    PowerManager.WakeLock m_wklk;
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
    public void onResume() {
        super.onResume();
        boolean isOnService = AccessibilityServiceUtil.isAccessibilitySettingsOn(getActivity());
        switchPreference = (SwitchPreference) findPreference("robpaper");
        boolean isRob = PrefsUtil.getPref().getBoolean("robpaper", false);
        if( ! (isOnService == isRob)  )
        {
            notGoSetting  = true;
            switchPreference.setChecked(isOnService);
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
                if( notGoSetting == false) {
                    //跳到辅助功能
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                }
                else {
                    notGoSetting = false;
                }
                break;
            case "lock_open":
                if( PrefsUtil.loadPrefBoolean("lock_open",false) ) {
                    if( MainActivity.mInstance !=null )
                    {
                        MainActivity.mInstance.setLock();
                    }
                    else
                    {
                        setLock();
                    }
                }
                else {
                    if( MainActivity.mInstance !=null )
                    {
                        MainActivity.mInstance.releaseLock();
                    }
                    else
                    {
                        releaseLock();
                    }
                }
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

    public void setLock()
    {

        //解除锁屏
        KeyguardManager keyguardManager = (KeyguardManager)getActivity().getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("MyKeyguardLock");
        keyguardLock.disableKeyguard();

        Log.i(TAG, "setLock");
        //保持常亮
        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        m_wklk = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,"cn");
        m_wklk.acquire();
    }

    public void releaseLock()
    {
        Log.i(TAG,"releaseLock");
        m_wklk.release();
        keyguardLock.reenableKeyguard();
    }
}
