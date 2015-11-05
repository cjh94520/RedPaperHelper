package com.smartman.redpaperhelper.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.MenuItem;

import com.baidu.mobstat.StatService;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.smartman.redpaperhelper.R;
import com.smartman.redpaperhelper.fragment.SettingFragment;
import com.smartman.redpaperhelper.utils.SystemBarUtil;

public class SettingActivity extends Activity {
    PowerManager.WakeLock m_wklk;
    private static final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(this, true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            // 使用颜色资源
            tintManager.setStatusBarTintResource(R.color.red);
        }

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
