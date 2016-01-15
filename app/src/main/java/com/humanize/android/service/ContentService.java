package com.humanize.android.service;

import android.util.Log;

import com.humanize.android.data.Content;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;

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
