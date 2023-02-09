package com.example.sneakerfinder.helper;

import android.content.Context;
import android.content.DialogInterface;

import com.example.sneakerfinder.R;

import androidx.appcompat.app.AlertDialog;

public class UIHelper {
    public static void showAlertDialog(Context context, String title, String message, final DialogInterface.OnClickListener onClickListener) {
        if (context == null) return;
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.button_ok),
                (dialog, which) -> {
                    if (onClickListener != null) {
                        onClickListener.onClick(dialog, which);
                    }
                    dialog.dismiss();
                });
        alertDialog.show();
    }
}
