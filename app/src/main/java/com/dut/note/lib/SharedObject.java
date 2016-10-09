package com.dut.note.lib;


import android.util.Log;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Copyright by NE 2015.
 * Created by noem on 17/11/2015.
 */
public final class SharedObject {
    private static final String LOG_TAG = "APP_DEBUG";
    private static SharedObject instance = null;
    private Dictionary<String, Object> dictionary;

    public static SharedObject getInstance() {
        if (instance == null)
            instance = new SharedObject();
        return instance;
    }

    public Object pop(String key) {
        Object obj = get(key);
        remove(key);
        return obj;
    }

    public Object get(String key) {
        return dictionary.get(key);
    }

    public void set(String key, Object obj) {
        if (obj == null)
            return;
        dictionary.put(key, obj);
    }

    public void remove(String key) {
        dictionary.remove(key);
    }

    private SharedObject() {
        dictionary = new Hashtable<>();
    }
}
