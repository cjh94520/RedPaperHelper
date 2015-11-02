package com.smartman.redpaperhelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.smartman.redpaperhelper.R;
import com.smartman.redpaperhelper.entity.RedPaper;
import com.smartman.redpaperhelper.entity.RedPaperItem;
import com.smartman.redpaperhelper.xutils.DbUtils;
import com.smartman.redpaperhelper.xutils.db.sqlite.Selector;
import com.smartman.redpaperhelper.xutils.exception.DbException;

import java.util.List;

/**
 * Created by jiahui.chen on 2015/10/28.
 */
public class StatusExpandAdapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater = null;
    private List<RedPaper> groupList;
    private DbUtils db;
    private Context context;

    /**
     * 构造方法
     *
     * @param context
     * @param group_list
     */
    public StatusExpandAdapter(Context context,
                               List<RedPaper> group_list) {
        this.context = context;
        db = DbUtils.create(context);
        this.groupList = group_list;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 返回一级Item总数
     */
    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return groupList.size();
    }

    /**
     * 返回二级Item总数
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        String date = groupList.get(groupPosition).getDate();
        List<RedPaperItem> childrenList;
        try {
            childrenList = db.findAll(Selector.from(RedPaperItem.class)
                    .where("parentId", "=", date));
            if (childrenList != null) {
                return childrenList.size();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取一级Item内容
     */
    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return groupList.get(groupPosition);
    }

    /**
     * 获取二级Item内容
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String date = groupList.get(groupPosition).getDate();
        List<RedPaperItem> childrenList;
        try {
            childrenList = db.findAll(Selector.from(RedPaperItem.class)
                    .where("parentId", "=", date));
            if (childrenList != null) {
                return childrenList.get(childPosition);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        GroupViewHolder holder = new GroupViewHolder();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.group_status_item, null);
        }
        holder.dateGroup = (TextView) convertView
                .findViewById(R.id.date);

        holder.dateGroup.setText(groupList.get(groupPosition).getDate());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder = null;
        RedPaperItem entity = (RedPaperItem) getChild(groupPosition,
                childPosition);
        if (convertView != null) {
            viewHolder = (ChildViewHolder) convertView.getTag();
        } else {
            viewHolder = new ChildViewHolder();
            convertView = inflater.inflate(R.layout.child_status_item, null);
            viewHolder.words = (TextView) convertView
                    .findViewById(R.id.words);
        }
        String thanks_words = "抢到" + entity.getPerson() + "的红包: " + entity.getMoney() + "元";
        viewHolder.words.setText(thanks_words);

        convertView.setTag(viewHolder);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return false;
    }

    private class GroupViewHolder {
        TextView dateGroup;
    }

    private class ChildViewHolder {
        public TextView words;
    }

}
