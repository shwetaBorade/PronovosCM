package com.pronovoscm.utils.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

import com.pronovoscm.R;

public class CustomProgressBar extends Dialog {

    public static CustomProgressBar customProgressBar;

    public CustomProgressBar(Context context) {
        super(context);
        initDialog();
    }

    public static CustomProgressBar showDialog(Context context) {
        if (customProgressBar != null && customProgressBar.isShowing()) {
            customProgressBar.dismiss();
        }
        customProgressBar = new CustomProgressBar(context);
        customProgressBar.setCancelable(false);
        customProgressBar.show();
        return customProgressBar;
    }

    public static void dissMissDialog(Context context) {
        try {
            if (customProgressBar != null && customProgressBar.isShowing()) {
                customProgressBar.dismiss();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void initDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            this.setCancelable(false);
            this.setCanceledOnTouchOutside(false);
            setContentView(R.layout.custom_progress_bar);
            getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        }
    }

}