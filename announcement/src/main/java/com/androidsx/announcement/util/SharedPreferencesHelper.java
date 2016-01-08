package com.androidsx.announcement.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper methods to work witht shared preferences.
 */
public class SharedPreferencesHelper {
    public static final String PREFS_NAME = "Announcement";

    private SharedPreferencesHelper() {
        // Non-Instantiable
    }

    // GENERIC
    public static void removeValue(Context context, String key) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.remove(key);
        editor.commit();
    }

    public static boolean containsValue(Context context, String key) {
        return getPreferences(context).contains(key);
    }

    // SETTERS
    public static void saveIntValue(Context context, String key, int value) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }
    
    public static void incrementIntValue(Context context, String key) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(key, getIntValue(context, key) + 1);
        editor.commit();
    }

    public static void saveLongValue(Context context, String key, long value) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void saveBooleanValue(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void saveStringValue(Context context, String key, String value) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    // GETTERS
    public static int getIntValue(Context context, String key) {
        return getIntValue(context, key, 0);
    }
    
    public static int getIntValue(Context context, String key, int defaultValue) {
        return getPreferences(context).getInt(key, defaultValue);
    }

    public static long getLongValue(Context context, String key) {
        return getPreferences(context).getLong(key, 0);
    }

    public static long getLongValue(Context context, String key, long defaultValue) {
        return getPreferences(context).getLong(key, defaultValue);
    }

    public static boolean getBooleanValue(Context context, String key) {
        return getPreferences(context).getBoolean(key, false);
    }

    public static boolean getBooleanValue(Context context, String key, boolean defaultValue) {
        return getPreferences(context).getBoolean(key, defaultValue);
    }

    public static String getStringValue(Context context, String key) {
        return getStringValue(context, key, "");
    }

    public static String getStringValue(Context context, String key, String defaultValue) {
        return getPreferences(context).getString(key, defaultValue);
    }

    // INTERNAL
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(SharedPreferencesHelper.PREFS_NAME, 0);
    }
}
