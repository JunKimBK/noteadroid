package com.dut.note.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.nfc.FormatException;
import android.nfc.Tag;
import android.util.Log;

import com.blogspot.sontx.libex.util.Convert;
import com.dut.note.bean.Alarm;
import com.dut.note.bean.Check;
import com.dut.note.bean.Note;
import com.dut.note.lib.Converter;
import com.dut.note.net.sync.FileCache;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SQLiteDB implements IDatabase {
    private static final String TAG = "SQLiteDB";
    SQLiteDatabase db;
    private static int _ID_COL_ID = 0;
    private static int TITLE_COL_ID = 1;
    private static int TEXT_COL_ID = 2;
    private static int CHECKS_COL_ID = 3;
    private static int IMAGES_COL_ID = 4;
    private static int SOUNDS_COL_ID = 5;
    private static int ALARM_COL_ID = 6;
    private static int TAGS_COL_ID = 7;
    private static int CREATED_COL_ID = 8;
    private FileCache fileCache;

    public SQLiteDB(FileCache fileCache) {
        if (!fileCache.init())
            Log.e("SQLDB", "file cache do not initialized");
        this.fileCache = fileCache;
    }

    private void openDB() {
        db = SQLiteDatabase.openDatabase(fileCache.getPath(), null, 0);
    }

    private void closeDB() {
        if (db != null) {
            db.close();
            fileCache.setModified();
            fileCache.updateToService();
        }
    }

    @Override
    public void insertNote(Note note) {
        openDB();

        // create query statement
        String sql;

        sql = "INSERT INTO 'main'.'tbNote' (" + "\n"
                + "'Title','Text','Checks','Images','Sounds','Alarm','Tags','Created'" + "\n"
                + ") VALUES (" + "\n"
                + Converter.toParam(note.getTitle()) + ","
                + Converter.toParam(note.getText()) + ","
                + Converter.toParam(note.getChecks()) + ","
                + Converter.toParam(note.getImages()) + ","
                + Converter.toParam(note.getSounds()) + ","
                + Converter.toParam(note.getAlarm()) + ","
                + Converter.toParam(Converter.listStringToString(note.getTags())) + ","
                + Converter.toParam(Convert.dateTimeToInteger(note.getCreated())) + "\n"
                + ")";

        // write log query
        Log.d(TAG, "sql = " + sql);

        // query
        db.execSQL(sql);

        sql = "SELECT * FROM 'main'.'tbNote' ORDER BY _id DESC LIMIT 1";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        if (!c.isAfterLast()) {
            int id = c.getInt(_ID_COL_ID);
            note.set_id(id);
        }

        // write result
        Log.d(TAG, "insert note completed!");

        /* update table tbTags */
        List<String> listTags = note.getTags();
        if (listTags != null) {
            for (String tag : listTags) {
                addTag(tag);
            }
        }

        closeDB();
    }

    @Override
    public void updateNote(Note note) {
        openDB();

        // create query
        String sql;
        sql = "UPDATE 'tbNote' "
                + "SET "
                + "Title=" + Converter.toParam(note.getTitle()) + ","
                + "Text=" + Converter.toParam(note.getText()) + ","
                + "Checks=" + Converter.toParam(note.getChecks()) + ","
                + "Images=" + Converter.toParam(note.getImages()) + ","
                + "Sounds=" + Converter.toParam(note.getSounds()) + ","
                + "Alarm=" + Converter.toParam(note.getAlarm()) + ","
                + "Tags=" + Converter.toParam(note.getTags()) + " "
                + "WHERE _id=" + note.get_id();

        // save log query statement
        Log.d(TAG, "sql = " + sql);

        // query
        db.execSQL(sql);

        // write log result
        Log.d(TAG, "update successful");

        /* update table tbTags */
        List<String> listTags = note.getTags();
        if (listTags != null) {
            for (String tag : listTags) {
                addTag(tag);
            }
        }

        closeDB();
    }

    @Override
    public void removeNote(int noteId) {
        openDB();

        // create delete query statement
        String sql = "DELETE FROM 'main'.'tbNote' WHERE _id=" + noteId;

        // log delete query statement
        Log.d(TAG, "sql = " + sql);

        // execute query
        db.execSQL(sql);

        // log result
        Log.d(TAG, "Delete note successful!");

        closeDB();
    }

    @Override
    public void removeNotes(List<Integer> noteIds) {
        openDB();

        // create delete query statement
        String sql;
        int numOfIds = noteIds.size();

        // if numOfIds == 0 then no note removed
        if (numOfIds == 0) {
            return;
        }

        // if numOfIds > 0
        sql = "DELETE FROM 'main'.'tbNote' WHERE "
                + "_id=" + noteIds.get(0);
        for (int i = 1; i < numOfIds; ++i) {
            sql += " OR _id=" + (int)noteIds.get(i);
        }

        // log delete query
        Log.d(TAG, "sql = " + sql);

        // execute query
        db.execSQL(sql);

        // write log result
        Log.d(TAG, "delete Notes successful!");

        closeDB();
    }

    private List<Note> getListNotesByTag(String tag) {
        openDB();
        String sql;
        if (tag == null || tag.trim().length() == 0) {
            sql = "SELECT * FROM 'main'.'tbNote'";
        } else {
            sql = "SELECT * FROM tbNote WHERE Tags LIKE " + Converter.toCondition(tag);
        }
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        List<Note> listNote = new ArrayList<>();
        Note note;
        List<Check> listChecks;
        List<String> listStrChecks;
        while (!c.isAfterLast()) {
            note = new Note();
            listChecks = new ArrayList<>();
            listStrChecks = Converter.stringToList(c.getString(CHECKS_COL_ID));
            if (listStrChecks != null) {
                for (String strCheck : listStrChecks
                        ) {
                    listChecks.add(Check.parse(strCheck));
                }
            }

            note.set_id(c.getInt(_ID_COL_ID));
            note.setTitle(c.getString(TITLE_COL_ID));
            note.setText(c.getString(TEXT_COL_ID));
            note.setChecks(listChecks);
            note.setImages(Converter.stringToList(c.getString(IMAGES_COL_ID)));
            note.setSounds(Converter.stringToList(c.getString(SOUNDS_COL_ID)));
            note.setAlarm(Alarm.parse(c.getString(ALARM_COL_ID)));
            note.setTags(Converter.stringToList(c.getString(TAGS_COL_ID)));
            note.setCreated(Convert.integerToDateTime(c.getInt(CREATED_COL_ID)));

            listNote.add(0, note);
            c.moveToNext();
        }
        closeDB();
        return listNote.size() > 0 ? listNote : null;
    }

    private List<Note> getListNotesByTitleOrText(String keyword) {
        openDB();
        String sql;
        if (keyword == null || keyword.trim().length() == 0) {
            sql = "SELECT * FROM 'main'.'tbNote'";
        } else {
            sql = "SELECT * FROM tbNote WHERE "
                    + "Title LIKE " + Converter.toCondition(keyword) + " OR "
                    + "Text LIKE " + Converter.toCondition(keyword);
        }
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        List<Note> listNote = new ArrayList<>();
        Note note;
        List<Check> listChecks;
        List<String> listStrChecks;
        while (!c.isAfterLast()) {
            note = new Note();
            listChecks = new ArrayList<>();
            listStrChecks = Converter.stringToList(c.getString(CHECKS_COL_ID));
            if (listStrChecks != null) {
                for (String strCheck : listStrChecks
                        ) {
                    listChecks.add(0, Check.parse(strCheck));
                }
            }

            note.set_id(c.getInt(_ID_COL_ID));
            note.setTitle(c.getString(TITLE_COL_ID));
            note.setText(c.getString(TEXT_COL_ID));
            note.setChecks(listChecks);
            note.setImages(Converter.stringToList(c.getString(IMAGES_COL_ID)));
            note.setSounds(Converter.stringToList(c.getString(SOUNDS_COL_ID)));
            note.setAlarm(Alarm.parse(c.getString(ALARM_COL_ID)));
            note.setTags(Converter.stringToList(c.getString(TAGS_COL_ID)));
            note.setCreated(Convert.integerToDateTime(c.getInt(CREATED_COL_ID)));

            listNote.add(note);
            c.moveToNext();
        }
        closeDB();
        return listNote.size() > 0 ? listNote : null;
    }

    @Override
    public List<Note> getListNotes(String keyword) {
        List<Note> listNotes = getListNotesByTag(keyword);
        if (listNotes != null && listNotes.size() > 0) {
            return listNotes;
        }
        return getListNotesByTitleOrText(keyword);
    }

    /****************************************************/
    /* for tags */

    private boolean isExistTag(String tag) {
        boolean isExist = false;
        String sql = "SELECT * FROM 'tbTags' WHERE tag LIKE " + Converter.toCondition(tag);
        Log.d(TAG, sql);
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        if (c.isAfterLast() == false) {
            isExist = true;
            Log.d(TAG, "exist tag " + tag);
        }
        return isExist;
    }

    private void addTag(String tag) {
        if (!isExistTag(tag)) {
            String sql = "INSERT INTO tbTags (tag) VALUES ("+Converter.toParam(tag)+")";
            db.execSQL(sql);
            Log.d(TAG, "added new tag:" + tag);
        }
    }

    @Override
    public List<String> getAllTags() {
        openDB();
        Cursor c = db.rawQuery("SELECT tag FROM tbTags", null);
        c.moveToFirst();
        List<String> listTags = new ArrayList<>();
        while (c.isAfterLast() == false) {
            listTags.add(c.getString(0));
            c.moveToNext();
        }
        closeDB();
        return listTags.size() > 0 ? listTags : null;
    }

    /* for config */

    private void setConfig(String key, String value) {
        openDB();
        String sql = "UPDATE tbConfig SET value=" + Converter.toParam(value)
                + " WHERE key=" + Converter.toParam(key);
        db.execSQL(sql);
        closeDB();
    }

    private String getConfig(String key) {
        openDB();
        Cursor c = db.rawQuery("SELECT value FROM 'tbConfig' WHERE key=?", new String[]{key});
        c.moveToFirst();
        String value = null;
        if (c.isAfterLast() == false) {
            value = c.getString(0);
        }
        closeDB();
        return value;
    }

    public void addAccount(String userName, String password) {
        setConfig("userName", userName);
        setConfig("password", password);
    }

    public boolean checkLogin(String userName, String password) {
        boolean matchUserName = userName.equals(getConfig("userName"));
        boolean matchPassword = password.equals(getConfig("password"));
        return matchUserName && matchPassword;
    }

    public void changePassword(String userName, String oldPassword, String newPassword) {
        if (checkLogin(userName, oldPassword)) {
            setConfig("password", newPassword);
        }
    }

    public void setDoneColor(String color) {
        setConfig("doneColor", color);
    }

    public String getDoneColor() {
        return getConfig("doneColor");
    }

    public void setRemainColor(String color) {
        setConfig("remainColor", color);
    }

    public String getRemainColor() {
        return getConfig("remainColor");
    }

    public void addHistory(String newHistory) {
        // check exist
        List<String> listHistory = getHistory();
        boolean exist = false;
        for (String i : listHistory) {
            if (newHistory.equals(i)) {
                exist = true;
                break;
            }
        }
        // add
        if (!exist) {
            listHistory.add(newHistory);
        }
        // save
        setConfig("history", Convert.objectToString(listHistory));
    }

    public List<String> getHistory() {
        return (List<String>) Convert.stringToObject(getConfig("history"));
    }

}
