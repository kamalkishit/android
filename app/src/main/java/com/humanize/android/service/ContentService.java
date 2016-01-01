package com.humanize.android.service;

import android.util.Log;

import com.humanize.android.data.Content;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by Kamal on 9/15/15.
 */
public interface ContentService {

    void incrRecommendedCount(Content content);
    void decrRecommendedCount(Content content);
    void incrSharedCount(Content content);
    void incrViewedCount(Content content);
}
