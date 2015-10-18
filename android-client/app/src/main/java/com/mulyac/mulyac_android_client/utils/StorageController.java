package com.mulyac.mulyac_android_client.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StorageController {
    protected Context mContext;
    protected SharedPreferences mSharedPrefs;
    protected SharedPreferences.Editor mPrefEditor;

    public StorageController(Context context, String prefFileName) {
        mContext = context;
        mSharedPrefs = mContext.getSharedPreferences(prefFileName,
                Context.MODE_PRIVATE);
    }


    public synchronized void clearAll() {
        mPrefEditor = mSharedPrefs.edit();
        mPrefEditor.clear();
        mPrefEditor.commit();
    }

    public synchronized void remove(String key) {
        mPrefEditor = mSharedPrefs.edit();
        mPrefEditor.remove(key);
        mPrefEditor.commit();
    }

    public synchronized void set(String key, String value) {
        mPrefEditor = mSharedPrefs.edit();
        mPrefEditor.putString(key, value);
        mPrefEditor.commit();
    }

    @SuppressLint("NewApi")
    public synchronized void set(String key, Set<String> values) {
        mPrefEditor = mSharedPrefs.edit();
        mPrefEditor.putStringSet(key, values);
        mPrefEditor.commit();
    }

    public synchronized void set(String key, int value) {
        mPrefEditor = mSharedPrefs.edit();
        mPrefEditor.putInt(key, value);
        mPrefEditor.commit();
    }

    public synchronized void set(String key, boolean value) {
        mPrefEditor = mSharedPrefs.edit();
        mPrefEditor.putBoolean(key, value);
        mPrefEditor.commit();
    }

    public synchronized void set(String key, float value) {
        mPrefEditor = mSharedPrefs.edit();
        mPrefEditor.putFloat(key, value);
        mPrefEditor.commit();
    }

    public synchronized void set(String key, long value) {
        mPrefEditor = mSharedPrefs.edit();
        mPrefEditor.putLong(key, value);
        mPrefEditor.commit();
    }

    public synchronized Map<String, ?> getAll() {
        return mSharedPrefs.getAll();
    }

    public synchronized Set<String> getAllKeys() {
        Set<String> keySet = new HashSet<String>();
        Map<String, ?> entries = getAll();

        for (Map.Entry<String, ?> entry : entries.entrySet()) {
            keySet.add(entry.getKey());
        }

        return keySet;
    }

    public synchronized boolean get(String key, boolean defaultValue) {
        return mSharedPrefs.getBoolean(key, defaultValue);
    }

    public synchronized float get(String key, float defaultValue) {
        return mSharedPrefs.getFloat(key, defaultValue);
    }

    public synchronized int get(String key, int defaultValue) {
        return mSharedPrefs.getInt(key, defaultValue);
    }

    public synchronized long get(String key, long defaultValue) {
        return mSharedPrefs.getLong(key, defaultValue);
    }

    public synchronized String get(String key, String defaultValue) {
        return mSharedPrefs.getString(key, defaultValue);
    }

    @SuppressLint("NewApi")
    public synchronized Set<String> get(String key, Set<String> defaultValues) {
        return mSharedPrefs.getStringSet(key, defaultValues);
    }

    public synchronized boolean contains(String key) {
        return mSharedPrefs.contains(key);
    }
}