package com.smartman.redpaperhelper.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.smartman.redpaperhelper.activity.RecordActivity;
import com.smartman.redpaperhelper.entity.RedPaper;
import com.smartman.redpaperhelper.entity.RedPaperItem;
import com.smartman.redpaperhelper.utils.PrefsUtil;
import com.smartman.redpaperhelper.xutils.DbUtils;
import com.smartman.redpaperhelper.xutils.exception.DbException;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by JiaHui on 2015/10/24.
 */
public class RobPaperService extends AccessibilityService {

    public static final String Tag = "RobPaperService";
    public ClipboardManager clipboard;
    public static boolean isNotFromMoneyDetail = true;
    public static boolean isFromNotification = false;
    private DbUtils db;
    private String thanksString = "";

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
                            gotoWeCharUI(event);
                            isFromNotification = true;
                        }
                    }
                }
                break;

            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:    //进入微信界面
                if( isFromNotification ) {
                    String className = event.getClassName().toString();
                    if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                        if (isNotFromMoneyDetail) {
                            //先停顿再抢
                            getPacket();
                        } else {
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
                pendingIntent.send(); //点击通知栏信息
//                TimerTask timerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        getPacket();
//                    }
//                };
//                Timer timer = new Timer();
//                timer.schedule(timerTask, 500);
                getPacket();

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
            int size = list.size();
            AccessibilityNodeInfo info = list.get(size - 1);
            if (info != null && info.getParent() != null) {
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    private void openPacket() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("拆红包");
            if (list != null && list.size() != 0) {
                list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                List<AccessibilityNodeInfo> closeInfo = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ayn");
                closeInfo.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                PressHomeKey();
            }
        }
    }

    private void handleThanksWords() {
        AccessibilityNodeInfo PaperDetailInfo = getRootInActiveWindow();
        //抢到谁的红包
        List<AccessibilityNodeInfo> personInfo = PaperDetailInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aw4");
        if (PrefsUtil.loadPrefBoolean("reply_person", false)) {
            int end_index = personInfo.get(0).getText().toString().indexOf("的红包",0);

            thanksString += "@" + personInfo.get(0).getText().toString().substring(0,end_index) ;
        }
        //多大的红包
        List<AccessibilityNodeInfo> moneyInfo = PaperDetailInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aw8");
        if (PrefsUtil.loadPrefBoolean("reply_money", false)) {
            thanksString += ",谢谢你" + moneyInfo.get(0).getText().toString() + "的红包。";
        }
        //自定义感谢语
        if (PrefsUtil.loadPrefBoolean("reply_words", false)) {
            thanksString += PrefsUtil.loadPrefString("thanks_words", "");
        }
        isNotFromMoneyDetail = false;
        List<AccessibilityNodeInfo> backInfo = PaperDetailInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/fc");
        backInfo.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        backInfo.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

        //导入数据库
        importDatabases(personInfo.get(0).getText().toString(), Double.valueOf(moneyInfo.get(0).getText().toString()));
    }

    private void importDatabases(String person, double money) {
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
            }
            else
            {
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
            gotoRecordActivity();
            return;
        }
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
                ClipData clip = ClipData.newPlainText("label", thanksString);
                clipboard.setPrimaryClip(clip);
                info.performAction(AccessibilityNodeInfo.ACTION_PASTE);

                //发送
                List<AccessibilityNodeInfo> sendButton = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/uu");
                sendButton.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);

                //重新置空
                thanksString  = "";

                gotoRecordActivity();
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

    private void gotoRecordActivity()
    {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(getApplicationContext(), RecordActivity.class);
        startActivity(intent);
    }
}
