package com.smartman.redpaperhelper.application;

import android.app.Application;

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
}
