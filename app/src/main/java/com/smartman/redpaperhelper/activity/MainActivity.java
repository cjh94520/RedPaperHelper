package com.smartman.redpaperhelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
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
import com.xiaomi.market.sdk.XiaomiUpdateAgent;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private TextView start_text;
    private RelativeLayout startButton;
    private ImageView setView;
    private TextView helpView;
    private TextView recordView;
    private ServiceAlertDialog dialog;
    private ImageView catView;
    private ImageView bottomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //小米自动更新sdk
        XiaomiUpdateAgent.update(this);

        setContentView(R.layout.activity_main);
        bottomView = (ImageView) findViewById(R.id.bottom);

        startButton = (RelativeLayout) findViewById(R.id.start);
        startButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    bottomView.setBackgroundResource(R.drawable.bottom_press);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    bottomView.setBackgroundResource(R.drawable.bottom_normal);
                }
                return false;
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

        setView = (ImageView) findViewById(R.id.setting);
        setView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        helpView = (TextView) findViewById(R.id.help);
        helpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog = new ServiceAlertDialog(MainActivity.this);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        recordView = (TextView) findViewById(R.id.record);
        recordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, RecordActivity.class);
                startActivity(intent);
            }
        });

        start_text = (TextView) findViewById(R.id.start_text);

        setView = (ImageView) findViewById(R.id.setting);
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.setting_anim);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        setView.startAnimation(operatingAnim);

        catView = (ImageView) findViewById(R.id.cat);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isOnService = AccessibilityServiceUtil.isAccessibilitySettingsOn(getApplicationContext());
        if (!isOnService) {
            start_text.setText("启动服务");
            start_text.setEnabled(true);
            AnimationDrawable animationDrawable = (AnimationDrawable) catView.getDrawable();
            animationDrawable.stop();
        } else {
            start_text.setText("运行中");
            start_text.setEnabled(false);
            AnimationDrawable animationDrawable = (AnimationDrawable) catView.getDrawable();
            animationDrawable.start();
        }
        Boolean isFirstIn = PrefsUtil.loadPrefBoolean("FIRST_IN", true);
        if (isFirstIn) {
            dialog = new ServiceAlertDialog(MainActivity.this);
            PrefsUtil.savePrefBoolean("FIRST_IN", false);
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

}
