package com.dut.note.lib.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.blogspot.sontx.libex.DateTime;
import com.blogspot.sontx.libex.util.Convert;
import com.dut.note.AlarmClock.Receiver;
import com.dut.note.bean.Note;

import java.util.Calendar;

/**
 * Copyright by NE 2015.
 * Created by noem on 22/12/2015.
 */
public final class AlarmMaker {
    private Context context;

    public AlarmMaker(Context context) {
        this.context = context;
    }

    public void setAlarm(Note note) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, note.getAlarm().getDateTime().getHours());
        calendar.set(Calendar.MINUTE, note.getAlarm().getDateTime().getMinutes());

        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Receiver.class);
        Bundle bundle = new Bundle();
        bundle.putString(Receiver.KEY_NOTE, Convert.objectToString(note));
        intent.putExtras(bundle);
        PendingIntent pi = PendingIntent.getBroadcast(context, note.get_id(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 12 * 60 * 60 * 1000, pi);
        Log.d("ALARM", "set alarm at " + note.getAlarm().getDateTime().toTimeString());
    }

    public void cancelAlarm(Note note) {
        Intent intent = new Intent(context, Receiver.class);
        Bundle bundle = new Bundle();
        bundle.putString(Receiver.KEY_NOTE, Convert.objectToString(note));
        intent.putExtras(bundle);
        PendingIntent pi = PendingIntent.getBroadcast(context, note.get_id(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
        Log.d("ALARM", "cancel alarm at " + note.getAlarm().getDateTime().toTimeString());
    }

}
