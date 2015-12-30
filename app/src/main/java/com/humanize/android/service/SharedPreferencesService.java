package com.humanize.android.service;

import android.content.SharedPreferences;

import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;

/**
 * Created by Kamal on 7/20/15.
 */
public class SharedPreferencesService {

    private static SharedPreferencesService sharedPreferencesInstance = null;
    private SharedPreferences sharedPreferences;

    private SharedPreferencesService() {
        sharedPreferences = ApplicationState.getAppContext().getSharedPreferences(Config.SHARED_PREFERENCES, 0);
    }

    public static SharedPreferencesService getInstance() {
        if (sharedPreferencesInstance == null) {
            sharedPreferencesInstance = new SharedPreferencesService();
        }

        return sharedPreferencesInstance;
    }

    public void putString(String key, String value) {
        if (value != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    public void delete(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
}