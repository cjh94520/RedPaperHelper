package com.smartman.redpaperhelper.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.smartman.redpaperhelper.utils.PrefsUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JiaHui on 2015/10/24.
 */
public class RobPaperService extends AccessibilityService {

    public static final String Tag = "RobPaperService";
    public ClipboardManager clipboard;
    public static boolean isNotFromMoneyDetail = true;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) {
            return;
        }
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:  //收到通知栏消息
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    for (CharSequence t : texts) {
                        String text = String.valueOf(t);
                        if (text.contains("[微信红包]"))  //通知栏某通知有信息包含[微信红包]
                        {
                            Log.i(Tag, "检测到通知栏，进入UI");
                            gotoWeCharUI(event);
                        }
                    }
                }
                break;

            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:    //进入微信界面
                String className = event.getClassName().toString();
                Log.i(Tag, "当前的ClassName" + className);
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                    if (isNotFromMoneyDetail) {
                        //先停顿再抢
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                getPacket();
                            }
                        };
                        Timer timer = new Timer();
                        timer.schedule(timerTask, 500);
                    } else {
                        replyThanksWords();
                    }
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                    //开始打开红包
                    openPacket();
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                    handleThanksWords();
                }
                break;
        }

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Tag, "onCreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(Tag, "onStartCommand");
        return super.onStartCommand(intent, START_STICKY, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(Tag, Tag + "is onDestory");
        super.onDestroy();
    }

    private void gotoWeCharUI(AccessibilityEvent event) {
        if (event.getParcelableData() != null
                && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            PendingIntent pendingIntent = notification.contentIntent;
            try {
                pendingIntent.send(); //点击通知栏信息
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        getPacket();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(timerTask, 500);

            } catch (PendingIntent.CanceledException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void getPacket() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        //使对话框失去焦点，否则点击事件无效，待测试
        List<AccessibilityNodeInfo> editList = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/uo");
        if (editList.size() != 0) {
            AccessibilityNodeInfo editText = editList.get(0);
            //失去焦点
            editText.performAction(AccessibilityNodeInfo.ACTION_CLEAR_FOCUS);
        }

        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText("领取红包");
        if (list != null && list.size() != 0) {
            Log.i(Tag, list.size() + "");
            Log.i(Tag, list.get(0).getClassName().toString());
            int size = list.size();
            AccessibilityNodeInfo info = list.get(size - 1);
            if (info != null && info.getParent() != null) {
                Log.i(Tag, list.get(0).getParent().getClassName().toString());
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    private void openPacket() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("拆红包");
            if(list!=null&&list.size()!=0) {
                list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            else
            {
                List<AccessibilityNodeInfo> closeInfo = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ayn");
                closeInfo.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                PressHomeKey();
            }
        }
    }

    private void handleThanksWords() {
        AccessibilityNodeInfo PaperDetailInfo = getRootInActiveWindow();
        String thanksString = "";
        if (PrefsUtil.loadPrefBoolean("reply_person", false)) {
            List<AccessibilityNodeInfo> personInfo = PaperDetailInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aw4");
            thanksString += personInfo.get(0).getText();
        }
        if (PrefsUtil.loadPrefBoolean("reply_money", false)) {
            List<AccessibilityNodeInfo> moneyInfo = PaperDetailInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aw8");
            thanksString += moneyInfo.get(0).getText();
        }
        if (PrefsUtil.loadPrefBoolean("reply_words", false)) {
            thanksString += PrefsUtil.loadPrefString("thanks_words", "");
        }
        if (thanksString.equals("")) {
            //模拟Home键
            PressHomeKey();
        } else {
            List<AccessibilityNodeInfo> backInfo = PaperDetailInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/fc");
            backInfo.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private void replyThanksWords() {
        try {
            List<AccessibilityNodeInfo> editList = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/uo");
            if (editList.size() != 0) {
                AccessibilityNodeInfo info = editList.get(0);

                //获取焦点
                info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);

                CharSequence tempString = info.getText();
                if (tempString != null) {
                    //先将输入框内容清空
                    Bundle arguments = new Bundle();
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, 0);
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, info.getText().length());
                    info.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, arguments);
                    info.performAction(AccessibilityNodeInfo.ACTION_CUT);
                }
                //将自定义的内容复制到输入框
                clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", "谢谢红包");
                clipboard.setPrimaryClip(clip);
                info.performAction(AccessibilityNodeInfo.ACTION_PASTE);

                //发送
                List<AccessibilityNodeInfo> sendButton = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/uu");
                sendButton.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void PressHomeKey()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
        intent.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(intent);
    }
}
