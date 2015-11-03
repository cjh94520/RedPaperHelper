package com.smartman.redpaperhelper.activity;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.smartman.redpaperhelper.R;
import com.smartman.redpaperhelper.ui.ServiceAlertDialog;
import com.smartman.redpaperhelper.utils.AccessibilityServiceUtil;
import com.smartman.redpaperhelper.utils.PrefsUtil;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private TextView start_text;
    private RelativeLayout startButton;
    private ImageView setView;
    private TextView helpView;
    private TextView recordView;
    private ServiceAlertDialog dialog;
    private ImageView catView;

    public static MainActivity mInstance;
    KeyguardManager.KeyguardLock keyguardLock;
    PowerManager.WakeLock m_wklk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        setContentView(R.layout.activity_main);
        startButton = (RelativeLayout) findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });

        setView = (ImageView) findViewById(R.id.setting);
        setView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

        helpView = (TextView) findViewById(R.id.help);
        helpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ServiceAlertDialog(MainActivity.this);
            }
        });

        recordView = (TextView) findViewById(R.id.record);
        recordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,RecordActivity.class);
                startActivity(intent);
            }
        });

        start_text = (TextView)findViewById(R.id.start_text);

        setView = (ImageView)findViewById(R.id.setting);
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.setting_anim);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        setView.startAnimation(operatingAnim);

        catView = (ImageView)findViewById(R.id.test);

        setLockOrNot();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(catView!=null)
        {
            AnimationDrawable animationDrawable = (AnimationDrawable)catView.getDrawable();
            animationDrawable.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isOnService = AccessibilityServiceUtil.isAccessibilitySettingsOn(getApplicationContext());
        if (!isOnService) {
            start_text.setText("启动服务");
            start_text.setEnabled(true);
        } else {
            start_text.setText("运行中");
            start_text.setEnabled(false);
        }
        Boolean isFirstIn = PrefsUtil.loadPrefBoolean("FIRST_IN", true);
        if(isFirstIn)
        {
            dialog = new ServiceAlertDialog(MainActivity.this);
            PrefsUtil.savePrefBoolean("FIRST_IN",false);
        }

        //开启百度统计
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //关闭百度统计
        StatService.onPause(this);
    }

    private void setLockOrNot()
    {
        Boolean isOpen = PrefsUtil.loadPrefBoolean("lock_open", false);
        if(isOpen)
        {

            setLock();
        }
    }

    public void setLock()
    {

        //解除锁屏
        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("MyKeyguardLock");
        keyguardLock.disableKeyguard();

        Log.i(TAG, "setLock");
        //保持常亮
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
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
