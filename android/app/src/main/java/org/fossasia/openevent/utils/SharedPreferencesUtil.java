package org.fossasia.openevent.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.fossasia.openevent.OpenEventApp;

public class SharedPreferencesUtil {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    static {
        sharedPreferences = OpenEventApp.getAppContext().getSharedPreferences(ConstantStrings.FOSS_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //Add more methods if needed

    public static void putInt(String key, int value) {
        editor.putInt(key, value).apply();
    }

    public static int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void putLong(String key, long value) {
        editor.putLong(key, value).apply();
    }

    public static long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static void putString(String key, String value) {
        editor.putString(key, value).apply();
    }

    public static String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void remove(String key) {
        editor.remove(key).apply();
    }
}
