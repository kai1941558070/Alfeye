package com.alfeye.facedemo.utils;

import android.app.AlertDialog;
import android.content.Context;

import com.facelib.LicenseST.DialogUtil;

/**
 * Created by DELL on 2018/9/18.
 */

public class AlertError {
    private AlertDialog dialog;

    public void showDialog(Context context, String title, String message) {
        dialog = DialogUtil.createAlertDialog(context, title, message, null);
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
