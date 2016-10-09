package com.dut.note.AlarmClock;

/**
 * Created by VoTung246 on 26/10/2015.
 */
import android.app.AlarmManager;
import android.app.PendingIntent;

import com.blogspot.sontx.libex.DateTime;
import com.dut.note.bean.Alarm;
import com.dut.note.bean.Note;
import java.util.Calendar;
import java.io.Serializable;
import android.content.Context;
import android.content.Intent;

public class AlarmAlarm implements Serializable {
    DateTime dt = new DateTime();
    final public String ONE_TIME ="onetime";
    private Calendar alarmTime = Calendar.getInstance();
    public AlarmAlarm(){
    }
    //dong ho se lap lai sau 5 phut neu ko tat dong ho
    public void SetAlarm(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Receiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 5 minutes
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 5 , pi);
    }
    // huy am bao
    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, Receiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
    //@return date string with default format is dd/MM/yyyy
    public void getTimeUntilNextAlarmMsg(){
        //DateTime in lib
        dt.getEstimateDateTime();
    }
    //to be continue
}
