package com.smartman.redpaperhelper.entity;

import com.smartman.redpaperhelper.xutils.db.annotation.Finder;
import com.smartman.redpaperhelper.xutils.db.annotation.Id;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jiahui.chen on 2015/10/27.
 */
public class RedPaper implements Serializable{
    private static final long serialVersionUID = 6721872944896314037L;

    @Id
    private String date;

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    @Finder(valueColumn = "date", targetColumn = "parentId")
    private List<RedPaperItem> data;

    public List<RedPaperItem> getData() {
        return data;
    }

    public void setData(List<RedPaperItem> data) {
        this.data = data;
    }

}
