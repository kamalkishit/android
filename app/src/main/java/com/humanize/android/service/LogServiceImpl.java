package com.humanize.android.service;

import com.humanize.android.BuildConfig;

/**
 * Created by kamal on 1/30/16.
 */
public class LogServiceImpl implements LogService {

    public void i(String tag, String string) {
        if (BuildConfig.DEBUG) {
            android.util.Log.i(tag, string);
        }
    }

    public void e(String tag, String string) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(tag, string);
        }
    }

    public void d(String tag, String string) {
        if (BuildConfig.DEBUG) {
            android.util.Log.d(tag, string);
        }
    }

    public void v(String tag, String string) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(tag, string);
        }
    }

    public void w(String tag, String string) {
        if (BuildConfig.DEBUG) {
            android.util.Log.w(tag, string);
        }
    }
}
