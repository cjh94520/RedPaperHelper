package com.smartman.redpaperhelper.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartman.redpaperhelper.R;


/**
 * Created by Administrator on 2015/11/7.
 */
public class Counter extends LinearLayout {
    public TextView numText;
    public TextView totalText;

    public Counter(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.count_layout, this);
        numText = (TextView)findViewById(R.id.num);
        totalText = (TextView)findViewById(R.id.total);

    }

    public Counter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
