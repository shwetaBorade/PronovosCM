package com.pronovoscm.utils;

import android.content.Context;
import android.content.SharedPreferences;


/*Class id used to store the app data in share perfrence*/
public class SharedPref {

    public static final String APP_PROCESS_ID = "app_process_id";
    private static final String PERFERENCE_NAME = "pronovoscm";
    public static String SESSION_DETAILS = "session_details";
    public static String LOGIN_SUCCESS_OR_NOT_PREF = "check_login";
    public static String FIRST_LAUNCH_PREF = "check_first_launch";

    public static String SYNC_OLD_FILES_REQUIRED = "SYNC_OLD_FILES_REQUIRED";
    public static String SYNC_OLD_FILES_RUNNING = "SYNC_OLD_FILES_RUNNING";
    public static String LOGIN_SUCCESS_VALUE = "1";
    static Context mContext;
    private static SharedPreferences _sPrefs = null;
    private static SharedPreferences.Editor _editor = null;
    private static SharedPref _instance = null;


    public SharedPref() {
    }

    public SharedPref(Context context) {
        _sPrefs = context.getSharedPreferences(PERFERENCE_NAME,
                Context.MODE_PRIVATE);
    }


    public static SharedPref getInstance(Context context) {
        mContext = context;
        if (_instance == null) {
            _instance = new SharedPref();
        }

        _sPrefs = context.getSharedPreferences(PERFERENCE_NAME, Context.MODE_PRIVATE);
        _editor = _sPrefs.edit();
        return _instance;
    }


    public static void setInstance(SharedPref instance) {
        _instance = instance;

    }

    public static void clearSharedPref(Context context) {
        try {
            if (_editor != null) {
                _editor.clear();
                _editor.commit();
            }
        } catch (Exception e) {
        }
    }

    public String readPrefs(String pref_name) {
        return _sPrefs.getString(pref_name, "");

    }

    public String readPrefs(String pref_name, String defaultVaule) {
        return _sPrefs.getString(pref_name, defaultVaule);
    }

    public void writePrefs(String pref_name, String pref_val) {
        _editor.putString(pref_name, pref_val);
        _editor.apply();
    }

    public void clearPrefs() {
        _editor.clear();
        _editor.apply();
    }

    public boolean readBooleanPrefs(String pref_name) {
        return _sPrefs.getBoolean(pref_name, false);
    }

    public boolean readBooleanPrefs(String pref_name, boolean value) {
        return _sPrefs.getBoolean(pref_name, value);
    }

    public int readIntegerPrefs(String pref_name) {
        return _sPrefs.getInt(pref_name, -1);
    }

    public int readIntegerPrefs(String pref_name, int defaultValue) {
        return _sPrefs.getInt(pref_name, defaultValue);
    }

    public long readLongPrefs(String pref_name, long defaultValue) {
        return _sPrefs.getLong(pref_name, defaultValue);
    }

    public void writeBooleanPrefs(String pref_name, boolean pref_val) {
        _editor.putBoolean(pref_name, pref_val);
        _editor.apply();
    }

    public void writeIntegerPref(String pref_name, int pref_val) {
        _editor.putInt(pref_name, pref_val);
        _editor.apply();
    }

    public void writeLongPref(String pref_name, long pref_val) {
        _editor.putLong(pref_name, pref_val);
        _editor.apply();
    }


    public void removePref(String pref_name) {
        _editor.remove(pref_name);
        _editor.apply();
    }


}
