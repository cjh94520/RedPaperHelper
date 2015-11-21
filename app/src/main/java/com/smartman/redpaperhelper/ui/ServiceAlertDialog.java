package com.smartman.redpaperhelper.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.smartman.redpaperhelper.R;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by jiahui.chen on 2015/10/27.
 */
public class ServiceAlertDialog {
    private Context mContext;
    private AlertDialog dialog;
    private TextView titleView;
    private Button startButton;

    public ServiceAlertDialog(Context context) {
        //
        mContext = context;
        dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog);

        startButton = (Button) window.findViewById(R.id.dialog_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                mContext.startActivity(intent);
                dismiss();
            }
        });


        //修改相关文字的颜色
        titleView = (TextView) window.findViewById(R.id.dialog_title);
        SpannableStringBuilder builder = new SpannableStringBuilder(titleView.getText().toString());
        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
        builder.setSpan(redSpan, 4, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleView.setText(builder);

    }

    public void dismiss() {
        dialog.dismiss();
    }
}
