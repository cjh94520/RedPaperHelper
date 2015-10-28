package com.smartman.redpaperhelper.entity;

import com.smartman.redpaperhelper.xutils.db.annotation.Column;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jiahui.chen on 2015/10/27.
 */
public class RedPaper implements Serializable{
    private static final long serialVersionUID = 6721872944896314037L;

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(column = "date")
    private Date date;

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

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
