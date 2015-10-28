package com.smartman.redpaperhelper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jiahui.chen on 2015/10/26.
 */
public final class PrefsUtil {
    private static SharedPreferences pref = null;
    private static SharedPreferences.Editor editor = null;

    public PrefsUtil(Context context) {
    }

    public static void init(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
    }

    public static SharedPreferences getPref() {
        return pref;
    }

    public static SharedPreferences.Editor getEditor() {
        return editor;
    }


    public static String loadPrefString(String key, String defaultValue) {
        return pref.getString(key, defaultValue);
    }

    public static void savePrefString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public static int loadPrefInt(String key, int defaultValue) {
        return pref.getInt(key, defaultValue);
    }

    public static void savePrefInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public static long loadPrefLong(String key, long defaultValue) {
        return pref.getLong(key, defaultValue);
    }

    public static void savePrefLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public static float loadPrefFloat(String key, float defaultValue) {
        return pref.getFloat(key, defaultValue);
    }

    public static void savePrefFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public static boolean loadPrefBoolean(String key, boolean defaultValue) {
        return pref.getBoolean(key, defaultValue);
    }

    public static void savePrefBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }
}
