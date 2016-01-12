package com.cchat.common.base.service.base.android;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by SEAMAN on 2015/8/19.
 */
public class SharePreferenceUtil {
    public static synchronized void writeSharePreferences(ContextWrapper contextWrapper, String name, String key, Object value) {
        SharedPreferences.Editor editor = contextWrapper.getSharedPreferences(name, Context.MODE_MULTI_PROCESS).edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        }
        editor.apply();
    }

    public static synchronized Map<String, ?> getAllKeyValueSharePreferences(ContextWrapper contextWrapper, String name) {
        SharedPreferences preferences = contextWrapper.getSharedPreferences(name, Context.MODE_MULTI_PROCESS);
        Map<String, ?> all = preferences.getAll();
        return all;
    }
}
