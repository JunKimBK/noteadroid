package com.dut.note.lib.alarm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.blogspot.sontx.libex.util.Convert;
import com.dut.note.AlarmClock.Service;
import com.dut.note.bean.Note;

/**
 * Copyright by NE 2015.
 * Created by noem on 22/12/2015.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    public static final String KEY_NOTE = "key_note";

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();
        Toast.makeText(context, "oh shititttttttttttt", Toast.LENGTH_LONG).show();
//
//        String data = intent.getStringExtra(KEY_NOTE);
//        Note note = (Note) Convert.stringToObject(data);
//        Toast.makeText(context, note.getTitle(), Toast.LENGTH_LONG).show();
    }
}
