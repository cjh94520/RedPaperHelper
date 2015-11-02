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

    private  static KeyguardManager.KeyguardLock keyguardLock;
    private  static PowerManager.WakeLock m_wklk;
    @Override
    public void onCreate() {
        super.onCreate();
        PrefsUtil.init(this);
    }

    public void setLock()
    {
        try {
            Log.i(TAG, "method setLock() is on");
//            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//            keyguardLock = keyguardManager.newKeyguardLock("MyKeyguardLock");
//            //保持常亮
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            m_wklk = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "cn");

            Boolean lock_open = PrefsUtil.loadPrefBoolean("lock_open", false);
            if (lock_open) {
                Log.i(TAG, "解除锁屏 保持常亮");
                //解除锁屏
                //keyguardLock.disableKeyguard();

                //保持常亮
                m_wklk.acquire();
            } else {
                Log.i(TAG, "锁屏 解除常亮");
              //  keyguardLock.disableKeyguard();
              //  keyguardLock.reenableKeyguard();
                m_wklk.release();
                keyguardLock = null;
                m_wklk = null;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        keyguardLock = null;
        m_wklk = null;
    }
}
