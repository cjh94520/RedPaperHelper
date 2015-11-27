package com.smartman.redpaperhelper.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.smartman.redpaperhelper.R;
import com.smartman.redpaperhelper.utils.SystemBarUtil;

/**
 * Created by Administrator on 2015/11/7.
 */
public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(this, true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            // 使用颜色资源
            tintManager.setStatusBarTintResource(R.color.red);
        }
    }
}
