package com.humanize.android.service;

/**
 * Created by kamal on 1/30/16.
 */
public interface LogService {

    void i(String tag, String string);
    void e(String tag, String string);
    void d(String tag, String string);
    void v(String tag, String string);
    void w(String tag, String string);
}
