package com.dut.note;

import android.app.Application;
import android.content.Context;

import com.dut.note.dao.IDatabase;
import com.dut.note.net.sync.ISync;

public final class App extends Application {
    private static Context context;
    private static ISync mSyncMgr;
    private static IDatabase mDbMgr;
    public static final String DB_NAME = "note.db";

    public static void setSyncMgr(final ISync syncMgr) {
        mSyncMgr = syncMgr;
    }

    public static void setDbMgr(final IDatabase dbMgr) {
        mDbMgr = dbMgr;
    }

    public void onCreate(){
        super.onCreate();
        App.context = getApplicationContext();
        mSyncMgr = null;
    }

    public static IDatabase getDbManager() {
        return mDbMgr;
    }

    public static ISync getSyncManager() {
        return mSyncMgr;
    }

    public static Context getAppContext() {
        return App.context;
    }

    public static String getAppName() {
        return "Note";
    }
}
