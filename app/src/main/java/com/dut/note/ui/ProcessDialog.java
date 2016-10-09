package com.dut.note.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import com.dut.note.R;

/**
 * Copyright by NE 2015.
 * Created by noem on 08/12/2015.
 */
public class ProcessDialog {
    private Dialog dialog;
    private TextView textView;

    public ProcessDialog(Context context, String text) {
        dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_process_dialog);
        textView = (TextView) dialog.findViewById(R.id.process_dialog_tv_text);
        setText(text);
    }

    public ProcessDialog(Context context) {
        this(context, "Processing...");
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void show() {
        dialog.show();
    }

    public void hide() {
        dialog.hide();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
