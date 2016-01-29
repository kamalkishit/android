package com.humanize.android.service;

import android.util.Log;

import com.humanize.android.data.Content;

/**
 * Created by Kamal on 9/15/15.
 */
public interface ContentService {

    void incrSharedCount(Content content);
    void incrViewedCount(Content content);
}
