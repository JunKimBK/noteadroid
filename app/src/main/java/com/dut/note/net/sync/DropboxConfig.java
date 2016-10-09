package com.dut.note.net.sync;

import android.content.Context;

import com.dropbox.sync.android.DbxAccountManager;

public final class DropboxConfig {
    private final static String APP_KEY = "hk6c5oto5rj4ui9";
    private final static String APP_SECRET = "eskdlbr6omaq7wi";

    public static DbxAccountManager getAccountManager(Context context){
        return DbxAccountManager.getInstance(context.getApplicationContext(), APP_KEY, APP_SECRET);
    }
}
