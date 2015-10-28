package com.smartman.redpaperhelper.entity;

import com.smartman.redpaperhelper.xutils.db.annotation.Column;
import com.smartman.redpaperhelper.xutils.db.annotation.Foreign;

import java.io.Serializable;

/**
 * Created by jiahui.chen on 2015/10/28.
 */
public class RedPaperItem implements Serializable{
    private static final long serialVersionUID = 624810233861305996L;

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Foreign(column = "parentId", foreign = "date")
    public RedPaper parent;

    @Column(column = "money")
    private double money;

    @Column(column = "person")
    private String person;

    public double getMoney() {
        return money;
    }

    public String getPerson() {
        return person;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setPerson(String person) {
        this.person = person;
    }
}
