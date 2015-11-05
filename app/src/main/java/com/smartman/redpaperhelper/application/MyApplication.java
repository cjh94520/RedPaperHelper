package com.smartman.redpaperhelper.application;

import android.app.Application;
import android.app.KeyguardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import com.smartman.redpaperhelper.utils.PrefsUtil;

/**
 * Created by jiahui.chen on 2015/10/26.
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        PrefsUtil.init(this);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
