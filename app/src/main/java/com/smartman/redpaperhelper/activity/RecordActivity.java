package com.smartman.redpaperhelper.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;

import com.baidu.mobstat.StatService;
import com.smartman.redpaperhelper.R;
import com.smartman.redpaperhelper.adapter.StatusExpandAdapter;
import com.smartman.redpaperhelper.entity.RedPaper;
import com.smartman.redpaperhelper.entity.RedPaperItem;
import com.smartman.redpaperhelper.xutils.DbUtils;
import com.smartman.redpaperhelper.xutils.exception.DbException;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by jiahui.chen on 2015/10/28.
 */
public class RecordActivity extends Activity {

    private ExpandableListView expandlistView;
    private StatusExpandAdapter statusAdapter;
    private Context context;
    private DbUtils db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_record);
        db = DbUtils.create(this);
        context = this;
        expandlistView = (ExpandableListView) findViewById(R.id.expandlist);
                try {
            initExpandListView();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void importDatabases(String person ,double money)
    {
        Date date = new Date();
        DateFormat df = DateFormat.getDateInstance(); //变成日期
        String id = df.format(date);
        RedPaper redPaper = new RedPaper();
        redPaper.setDate(id);

        RedPaper testPaper ;
        try {
            testPaper = db.findById(RedPaper.class,date);
            if( testPaper == null)
            {
                db.save(redPaper);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        RedPaperItem redPaperItem = new RedPaperItem();
        redPaperItem.setMoney(money);
        redPaperItem.setPerson(person);
        redPaperItem.parent = redPaper;

        try {
            db.saveBindingId(redPaperItem);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化可拓展列表
     */
    private void initExpandListView() throws DbException {
        List<RedPaper> list = getListData();
        if( list == null )
        {
            return;
        }
        statusAdapter = new StatusExpandAdapter(context, list);
        expandlistView.setAdapter(statusAdapter);
        expandlistView.setGroupIndicator(null); // 去掉默认带的箭头
        expandlistView.setSelection(0);// 设置默认选中项

        // 遍历所有group,将所有项设置成默认展开
        int groupCount = expandlistView.getCount();
        for (int i = 0; i < groupCount; i++) {
            expandlistView.expandGroup(i);
        }

        expandlistView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // TODO Auto-generated method stub
                return true;
            }
        });
    }

    private List<RedPaper> getListData() throws DbException {
        List<RedPaper> groupList;
        groupList = db.findAll(RedPaper.class);
        return groupList;
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
}