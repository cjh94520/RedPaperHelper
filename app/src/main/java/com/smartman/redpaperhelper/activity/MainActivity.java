package com.smartman.redpaperhelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartman.redpaperhelper.R;
import com.smartman.redpaperhelper.entity.RedPaper;
import com.smartman.redpaperhelper.ui.ServiceAlertDialog;
import com.smartman.redpaperhelper.utils.AccessibilityServiceUtil;
import com.smartman.redpaperhelper.utils.PrefsUtil;
import com.smartman.redpaperhelper.xutils.DbUtils;
import com.smartman.redpaperhelper.xutils.exception.DbException;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private Button startButton;
    private ImageView setView;
    private TextView helpView;
    private ServiceAlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.start);
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
                intent.setClass(getApplicationContext(),SettingActivity.class);
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



        //测试xutils.db
        DbUtils db = DbUtils.create(this);

        RedPaper redPaper = new RedPaper();

        try {
            db.dropTable(RedPaper.class);
        } catch (DbException e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
        }

        redPaper.setMoney(1.3);
        redPaper.setPerson("Li");
        Date date = new Date();
        DateFormat df1 = DateFormat.getDateInstance();//日期格式，精确到日
        Log.i(TAG, df1.format(date));
        redPaper.setDate(date);
        try {
            db.save(redPaper);
        } catch (DbException e) {
            e.printStackTrace();
            Log.i(TAG,e.getMessage());
        }

        try {
            RedPaper other = db.findById(RedPaper.class,date);
           // Log.i(TAG,other.getPerson());
        } catch (DbException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isOnService = AccessibilityServiceUtil.isAccessibilitySettingsOn(getApplicationContext());
        if (!isOnService) {
            startButton.setText("启动服务");
            startButton.setEnabled(true);
        } else {
            startButton.setText("服务正在运行");
            startButton.setEnabled(false);
        }
        Boolean isFirstIn = PrefsUtil.loadPrefBoolean("FIRST_IN", true);
        if(isFirstIn)
        {
            dialog = new ServiceAlertDialog(MainActivity.this);
            PrefsUtil.savePrefBoolean("FIRST_IN",false);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }


}
