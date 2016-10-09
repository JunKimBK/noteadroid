package com.dut.note.AlarmClock;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.blogspot.sontx.libex.util.Convert;
import com.dut.note.R;
import com.dut.note.bean.Note;
import com.dut.note.ui.MainActivity;
import com.dut.note.ui.PrevNoteActivity;

/**
 * Created by VoTung246 on 21/10/2015.
 */
public class Receiver extends WakefulBroadcastReceiver{
    public static final String KEY_NOTE = "key_note";

    public void onReceive(Context context,Intent intent) {
        //this will sound the alarm tone
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();

        Bundle bundle = intent.getExtras();

        Intent alarmIntent = new Intent("android.intent.action.MAIN");
        alarmIntent.setClass(context, PrevNoteActivity .class);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmIntent.putExtras(bundle);

        context.startActivity(alarmIntent);
    }
}
