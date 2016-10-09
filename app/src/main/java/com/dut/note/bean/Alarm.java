package com.dut.note.bean;

import android.nfc.FormatException;

import com.blogspot.sontx.libex.DateTime;
import com.blogspot.sontx.libex.util.Convert;

import java.io.Serializable;
import java.util.StringTokenizer;

public class Alarm implements Serializable {
    private DateTime mDateTime;
    private boolean mLoop;

    public DateTime getDateTime() {
        return mDateTime;
    }

    public void setDateTime(DateTime mDateTime) {
        this.mDateTime = mDateTime;
    }

    public boolean isLoop() {
        return mLoop;
    }

    public void setLoop(boolean mLoop) {
        this.mLoop = mLoop;
    }

    public static Alarm parse(String str){
        if (str == null || str.length() == 0) {
            return null;
        }

        String s_dt = str.substring(0, str.indexOf('|'));
        String s_loop = str.substring(str.indexOf('|') + 1);
        int i_dt = Convert.tryParseInt(s_dt, 0);
        int i_loop = Convert.tryParseInt(s_loop, 0);
        Alarm alarm = new Alarm();
        alarm.mDateTime = DateTime.parse(i_dt);
        alarm.mLoop = i_loop != 0;
        return alarm;
    }

    public boolean isAlarm() {
        return (mDateTime.getSeconds() & 0x1) == 0x1;// 0001
    }

    public boolean isCalendar() {
        return (mDateTime.getSeconds() & 0x2) == 0x2;// 0010
    }

    public void clearAlarm() {
        mDateTime.setSeconds(mDateTime.getSeconds() & 0x2);
    }

    public void clearCalendar() {
        mDateTime.setSeconds(mDateTime.getSeconds() & 0x1);
    }

    @Override
    public String toString() {
        return String.format("%d|%d", com.blogspot.sontx.libex.util.Convert.dateTimeToInteger(mDateTime), mLoop ? 1 : 0);
    }
}
