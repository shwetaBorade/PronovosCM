package com.pronovoscm.utils.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.widget.Button;

import com.pronovoscm.BuildConfig;
import com.pronovoscm.R;

public class MessageDialog {
    static AlertDialog alertDialog;

    /**
     * ALert to show message
     *
     * @param context
     * @param message
     * @param positiveButtonText
     */
    public void showMessageAlert(final Context context, String message, String positiveButtonText) {
        try {
            if (alertDialog == null || !alertDialog.isShowing()) {
                alertDialog = new AlertDialog.Builder(context).create();
            }
//            alertDialog.setTitle(context.getString(R.string.message));
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveButtonText, (dialog, which) -> {
                alertDialog.dismiss();

            });
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.setCancelable(false);
                if (BuildConfig.DEBUG) {
//                    alertDialog.show();
                }
            }
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } catch (Exception e) {
        }

    }
}
