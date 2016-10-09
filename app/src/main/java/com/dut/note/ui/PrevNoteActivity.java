package com.dut.note.ui;

import android.app.Dialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.sontx.libex.util.Convert;
import com.dut.note.AlarmClock.Receiver;
import com.dut.note.App;
import com.dut.note.R;
import com.dut.note.bean.Check;
import com.dut.note.bean.Note;
import com.dut.note.dao.SQLiteDB;
import com.dut.note.lib.alarm.AlarmMaker;
import com.dut.note.net.sync.DropboxSync;
import com.dut.note.net.sync.FileCache;
import com.dut.note.ui.view.ImageStreamView;

import java.util.ArrayList;
import java.util.List;

public class PrevNoteActivity extends DropboxSync implements View.OnClickListener {
    private Dialog dialog;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDialog(0);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_prev_note);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        Bundle bundle = getIntent().getExtras();

        String data = bundle.getString(Receiver.KEY_NOTE, "");
        note = (Note) Convert.stringToObject(data);

        if (note != null) {
            // title
            TextView tvTitle = (TextView) dialog.findViewById(R.id.dialog_prev_note_tv_title);
            tvTitle.setText(note.getTitle());

            // content
            if (note.getText() != null && note.getText().length() > 0) {
                TextView tvContent = (TextView) dialog.findViewById(R.id.dialog_prev_note_tv_content);
                tvContent.setText(note.getText());
                tvContent.setVisibility(View.VISIBLE);
            }

            // checks
            if (note.getChecks() != null && note.getChecks().size() > 0) {
                TextView tvChecks = (TextView) dialog.findViewById(R.id.dialog_prev_note_tv_remain);
                List<Check> checks = note.getChecks();
                List<String> remains = new ArrayList<>();
                for (Check check : checks) {
                    if (!check.isChecked())
                        remains.add(check.getText());
                }
                if (remains.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < remains.size(); i++) {
                        builder.append(String.format("%d. ", i + 1));
                        builder.append(remains.get(i));
                        if (i < remains.size() - 1)
                            builder.append("\n");
                    }
                    tvChecks.setText(builder.toString());
                } else {
                    tvChecks.setText("Everything Done!");
                }
                tvChecks.setVisibility(View.VISIBLE);
            }

            // images
            if (note.getImages() != null && note.getImages().size() > 0) {
                ImageStreamView ivImage = (ImageStreamView) dialog.findViewById(R.id.dialog_prev_note_iv_image);
                String imageName = note.getImages().get(0);
                ivImage.setSyncImageResource(imageName, this);
                ivImage.setVisibility(View.VISIBLE);
            }

            // buttons
            Button btnClose = (Button) dialog.findViewById(R.id.dialog_prev_note_btn_close);
            btnClose.setOnClickListener(this);
            Button btnDismiss = (Button) dialog.findViewById(R.id.dialog_prev_note_btn_dismiss);
            btnDismiss.setOnClickListener(this);
        }

        return dialog;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_prev_note_btn_close) {
        } else if (v.getId() == R.id.dialog_prev_note_btn_dismiss) {
            AlarmMaker maker = new AlarmMaker(this);
            maker.cancelAlarm(note);
            App.setSyncMgr(this);
            FileCache fileCache = new FileCache(this, App.DB_NAME);
            SQLiteDB db = new SQLiteDB(fileCache);
            App.setDbMgr(db);
            note.getAlarm().clearAlarm();
            db.updateNote(note);
            Toast.makeText(PrevNoteActivity.this, "Alarm was cancelled!", Toast.LENGTH_SHORT).show();
            fileCache.delete();
        }
        dialog.dismiss();
        finish();
    }
}
