package com.smartman.redpaperhelper.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;

import com.baidu.mobstat.StatService;
import com.smartman.redpaperhelper.fragment.SettingFragment;
import com.smartman.redpaperhelper.R;
import com.smartman.redpaperhelper.utils.AccessibilityServiceUtil;
import com.smartman.redpaperhelper.utils.PrefsUtil;

public class SettingActivity extends Activity {
    PowerManager.WakeLock m_wklk;
    private static final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //设置action bar
        ActionBar actionBar = getActionBar();
        actionBar.show();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.setting);
        actionBar.setDisplayShowHomeEnabled(false);

        //导入SettingFragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SettingFragment settingFragment = new SettingFragment();
        fragmentTransaction.replace(R.id.context, settingFragment);
        fragmentTransaction.commit();

        //设置switch-抢红包
       // setSwitch();
    }

    public void setLock()
    {

//        //解除锁屏
//        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        final KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("MyKeyguardLock");
//        keyguardLock.disableKeyguard();

        //保持常亮
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        m_wklk = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,"cn");
        m_wklk.acquire();
    }

    public void releaseLock()
    {
        m_wklk.release();
    }



    private void setSwitch()
    {
        boolean isOnService = AccessibilityServiceUtil.isAccessibilitySettingsOn(getApplicationContext());
        if (!isOnService) {
            PrefsUtil.savePrefBoolean("robpaper", false);
        } else {
            PrefsUtil.savePrefBoolean("robpaper",true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开启百度统计
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //关闭百度统计
        StatService.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case android.R.id.home :
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
