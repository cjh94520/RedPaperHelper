package com.smartman.redpaperhelper.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.smartman.redpaperhelper.R;
import com.smartman.redpaperhelper.adapter.StatusExpandAdapter;
import com.smartman.redpaperhelper.entity.RedPaper;
import com.smartman.redpaperhelper.entity.RedPaperItem;
import com.smartman.redpaperhelper.ui.Counter;
import com.smartman.redpaperhelper.utils.SystemBarUtil;
import com.smartman.redpaperhelper.xutils.DbUtils;
import com.smartman.redpaperhelper.xutils.db.sqlite.Selector;
import com.smartman.redpaperhelper.xutils.db.table.DbModel;
import com.smartman.redpaperhelper.xutils.exception.DbException;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by jiahui.chen on 2015/10/28.
 */
public class RecordActivity extends Activity {

    private static final String TAG = "RecordActivity";
    private ExpandableListView expandlistView;
    private StatusExpandAdapter statusAdapter;
    private Context context;
    private DbUtils db;

    private TextView numView;
    private TextView totalView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

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
        actionBar.setTitle(R.string.record);
        actionBar.setDisplayShowHomeEnabled(false);

        //导入数据
        db = DbUtils.create(this);
        context = this;
        expandlistView = (ExpandableListView) findViewById(R.id.expandlist);
        try {
            initExpandListView();
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化可拓展列表
     */
    private void initExpandListView() throws DbException {
        List<RedPaper> list = getListData();
        if (list == null) {
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

       // Counter counter = new Counter(this);
        //setdata(counter);
        //expandlistView.addFooterView(counter);

        setTotal();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setdata(Counter counter) {
        try {
            List<DbModel> data =
            db.findDbModelAll(Selector.from(RedPaperItem.class).select("sum(money)", "count(money)"));
            counter.numText.setText(data.get(0).getString("count(money)"));
            counter.totalText.setText(data.get(0).getString("sum(money)"));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void setTotal()
    {
        numView = (TextView)findViewById(R.id.num);
        totalView = (TextView)findViewById(R.id.total);
        try {
            List<DbModel> data =
                    db.findDbModelAll(Selector.from(RedPaperItem.class).select("sum(money)", "count(money)"));
            numView.setText(data.get(0).getString("count(money)"));
            totalView.setText(data.get(0).getString("sum(money)"));
        } catch (DbException e) {
            e.printStackTrace();
        }

    }
}