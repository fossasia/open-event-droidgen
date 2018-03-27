package org.fossasia.openevent.common.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager

import org.fossasia.openevent.OpenEventApp

object SharedPreferencesUtil {
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(OpenEventApp.getAppContext())
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    //Add more methods if needed
    @JvmStatic
    fun putInt(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    @JvmStatic
    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    @JvmStatic
    fun putLong(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    @JvmStatic
    fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    @JvmStatic
    fun putString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    @JvmStatic
    fun getString(key: String, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    @JvmStatic
    fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    @JvmStatic
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    @JvmStatic
    fun remove(key: String?) {
        editor.remove(key).apply()
    }
}
