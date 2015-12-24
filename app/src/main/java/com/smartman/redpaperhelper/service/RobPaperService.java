package com.smartman.redpaperhelper.service;

import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.smartman.redpaperhelper.R;
import com.smartman.redpaperhelper.activity.RecordActivity;
import com.smartman.redpaperhelper.entity.RedPaper;
import com.smartman.redpaperhelper.entity.RedPaperItem;
import com.smartman.redpaperhelper.utils.PrefsUtil;
import com.smartman.redpaperhelper.xutils.DbUtils;
import com.smartman.redpaperhelper.xutils.exception.DbException;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JiaHui on 2015/10/24.
 */
public class RobPaperService extends AccessibilityService {

    public static final String TAG = "RobPaperService";
    public static final String words = "Hello world";
    public static final String words111 = "Hello world";
    public ClipboardManager clipboard;
    public boolean isNotFromMoneyDetail = true;
    public boolean isFromNotification = false;

    private DbUtils db;
    private String thanksString = "";

    KeyguardManager km;
    PowerManager pm;

    PowerManager.WakeLock wl;
    KeyguardManager.KeyguardLock kl;

    private AccessibilityNodeInfo target = null;

    private int clickNum;

    private int validNum;

    private int handleNum = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:  //收到通知栏消息
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    for (CharSequence t : texts) {
                        String text = String.valueOf(t);
                        if (text.contains("[微信红包]"))  //通知栏某通知有信息包含[微信红包]
                        {
                            pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                            //获取电源管理器对象
                            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
                            //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
                            wl.acquire();
                            //点亮屏幕

                            km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                            //得到锁管理器对象
                            boolean flag = km.isKeyguardLocked();
                            isFromNotification = true;
                            if (flag == true) {
                                openKeyGuard(event);
                            } else {
                                gotoWeCharUI(event);
                            }

                        }
                    }
                }
                break;

            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:    //进入微信界面
                if (isFromNotification) {
                    String className = event.getClassName().toString();
                    if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                        if (!isNotFromMoneyDetail) {
                            isNotFromMoneyDetail = true;
                            isFromNotification = false;
                            replyThanksWords();
                        }
                    } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                        //开始打开红包
                        openPacket();
                    } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                        handleThanksWords();
                    }
                }
                break;
        }

    }

    //关闭锁屏
    private void openKeyGuard(AccessibilityEvent tempEvent) {
        final AccessibilityEvent event = tempEvent;
        kl = km.newKeyguardLock("unLock");
        kl.disableKeyguard();
        gotoWeCharUI(event);

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = DbUtils.create(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, START_STICKY, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void gotoWeCharUI(AccessibilityEvent event) {
        if (event.getParcelableData() != null
                && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            PendingIntent pendingIntent = notification.contentIntent;
            try {
                validNum = 0;
                pendingIntent.send(); //点击通知栏信息
                if (km.isKeyguardLocked()) {
                    clickNum = 0;
                    Log.i(TAG, "getPacketWithLock");
                    getPacketWithLock();
                } else {
                    Log.i(TAG, "getPacketWithoutLock");
                    getPacketWithoutLock();
                }
            } catch (PendingIntent.CanceledException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void getPacketWithoutLock() {
        if (validNum++ > 40) {
            return;
        }
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    getPacketWithoutLock();
                }
            };
            Log.i(TAG, "timer1启动");
            Timer timer = new Timer(true);
            timer.schedule(task, 100);
            return;
        }

        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText("领取红包");
        if (list != null && list.size() != 0) {
            int size = list.size();
            AccessibilityNodeInfo info = list.get(size - 1);
            if (info != null && info.getParent() != null) {
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } else {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    getPacketWithoutLock();
                }
            };
            Log.i(TAG, "timer2启动");
            Timer timer = new Timer(true);
            timer.schedule(task, 100);
            return;
        }
    }

    private void getPacketWithLock() {
        if (validNum++ > 40) {
            return;
        }

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    getPacketWithLock();
                }
            };
            Timer timer = new Timer(true);
            Log.i(TAG, "timer1启动");
            timer.schedule(task, 100);
            return;
        }

        //这里写上锁屏状态进入总聊天界面的代码！！！！
        Log.i(TAG, String.valueOf(clickNum));
        if (clickNum == 0) {
            List<AccessibilityNodeInfo> tempList = rootNode.findAccessibilityNodeInfosByText("[微信红包]");
            if (tempList != null && tempList.size() != 0) {
                Log.i(TAG, "ACTION_CLICK1");
                tempList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                clickNum = 1;
                getPacketWithLock();
                return;
            } else {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        getPacketWithLock();
                    }
                };
                Timer timer = new Timer(true);
                timer.schedule(task, 100);
                Log.i(TAG, "timer2启动");
                return;
            }
        }

        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText("领取红包");
        if (list != null && list.size() != 0) {
            int size = list.size();
            AccessibilityNodeInfo info = list.get(size - 1);
            if (info != null && info.getParent() != null) {
                Log.i(TAG, "ACTION_CLICK12");
                clickNum = 0;
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } else {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    getPacketWithLock();
                }
            };
            Timer timer = new Timer(true);
            timer.schedule(task, 100);
            Log.i(TAG, "timer3启动");
            return;
        }
    }

    private void openPacket() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("拆红包");
            if (list != null && list.size() != 0) {
                list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                //模拟按下返回键
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PressHomeKey();
            }
        }
    }

    private void handleThanksWords() {
        AccessibilityNodeInfo PaperDetailInfo = getRootInActiveWindow();
        String person;
        String money;

        //抢到谁的红包
        List<AccessibilityNodeInfo> personInfo = PaperDetailInfo.findAccessibilityNodeInfosByText("的红包");
        if ( personInfo==null || personInfo.size() == 0) {
            handleNum++;
            if (handleNum <= 20) {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        handleThanksWords();
                    }
                };
                Timer timer = new Timer(true);
                timer.schedule(task, 100);
                Log.i(TAG, "handleThanksWords" + String.valueOf(handleNum) + "启动");
                return;
            } else {
                handleNum = 0;
                return;
            }
        }
        handleNum = 0;
        int end_index = personInfo.get(0).getText().toString().indexOf("的红包", 0);
        person = personInfo.get(0).getText().toString().substring(0, end_index);

        if (PrefsUtil.loadPrefBoolean("reply_person", false)) {
            thanksString += "@" + person;
        }
        //多大的红包
        List<AccessibilityNodeInfo> moneyInfo = PaperDetailInfo.findAccessibilityNodeInfosByText("已存入零钱");
        AccessibilityNodeInfo parent = moneyInfo.get(0).getParent();

        money = parent.getChild(2).getText().toString();

        if (PrefsUtil.loadPrefBoolean("reply_money", false)) {
            if (PrefsUtil.loadPrefBoolean("reply_person", false)) {
                thanksString += ",谢谢你" + money + "元的红包。";
            } else {
                thanksString += "谢谢你" + money + "元的红包。";
            }
        }

        //自定义感谢语
        if (PrefsUtil.loadPrefBoolean("reply_words", false)) {
            thanksString += PrefsUtil.loadPrefString("thanks_words", "");
        }

        isNotFromMoneyDetail = false;

        //准备后退
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);

        //发送通知
        sendNotification(person, money);

        //导入数据库
        importDatabases(person, Double.valueOf(money));
    }

    private void importDatabases(String person, double money) {
        if (db == null) {
            db = DbUtils.create(getApplicationContext());
        }
        Date date = new Date();
        DateFormat df = DateFormat.getDateInstance(); //变成日期
        String id = df.format(date);
        RedPaper redPaper = new RedPaper();
        redPaper.setDate(id);

        RedPaperItem redPaperItem = new RedPaperItem();
        redPaperItem.setMoney(money);
        redPaperItem.setPerson(person);

        RedPaper testPaper;
        try {
            testPaper = db.findById(RedPaper.class, id);
            if (testPaper == null) {
                db.save(redPaper);
                redPaperItem.parent = redPaper;
            } else {
                redPaperItem.parent = testPaper;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        try {
            db.saveBindingId(redPaperItem);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void replyThanksWords() {
        if (thanksString.equals("")) {
            Log.i(TAG, "跑进来了");
            PressHomeKey();
            wl.release();
            wl = null;
            if (kl != null) {
                kl.reenableKeyguard();
                kl = null;
            }
            return;
        }
        try {
            AccessibilityNodeInfo root = getRootInActiveWindow();
            findEditText(root);
            target.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            CharSequence tempString = target.getText();
            if (tempString != null) {
                //先将输入框内容清空
                Bundle arguments = new Bundle();
                arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, 0);
                arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, target.getText().length());
                target.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, arguments);
                target.performAction(AccessibilityNodeInfo.ACTION_CUT);
            }
            //将自定义的内容复制到输入框
            clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", thanksString);
            clipboard.setPrimaryClip(clip);
            target.performAction(AccessibilityNodeInfo.ACTION_PASTE);

            //发送
            List<AccessibilityNodeInfo> sendButton = getRootInActiveWindow().findAccessibilityNodeInfosByText("发送");
            sendButton.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);

            //重新置空
            thanksString = "";
            PressHomeKey();

            wl.release();
            wl = null;
            if (kl != null) {
                kl.reenableKeyguard();
                kl = null;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void PressHomeKey() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
        intent.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(intent);
    }


    private void sendNotification(String person, String money) {
        int requestCode = (int) System.currentTimeMillis();
        Intent intent = new Intent(this, RecordActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, 0);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("已经帮你偷来了红包")
                .setContentTitle("招积红包展现真正的技术")
                .setContentText("从" + person + "处偷来价值:" + money + "元的红包")
                .setContentIntent(pendingIntent)
                .setNumber(1)
                .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(requestCode, notification);
    }

    private void findEditText(AccessibilityNodeInfo root) {
        if (root.getClassName().equals("android.widget.EditText")) {
            target = root;
        } else {
            for (int i = 0; i < root.getChildCount(); i++) {
                if (root.getChildCount() != 0) {
                    findEditText(root.getChild(i));
                }
            }
        }
    }
}
