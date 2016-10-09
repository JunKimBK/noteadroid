package com.dut.note.lib;

import android.util.Log;

import com.dut.note.Config;

public final class LogUtil {
    public static void d(String tag, String message) {
        if(Config.IS_DEBUG_MODE)
            Log.d(tag, message);
    }

    public static void d(String message) {
        d("NOTE", message);
    }
}
