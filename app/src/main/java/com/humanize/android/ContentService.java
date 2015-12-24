package com.humanize.android;

import android.util.Log;

import com.humanize.android.content.data.Content;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kamal on 9/15/15.
 */
public class ContentService {

    private static String TAG = ContentService.class.getSimpleName();

    public ContentService() {
    }

    public void incrRecommendedCount(Content content) {
        content.setRecommendedCount(content.getRecommendedCount() + 1);
        incrRecommendedCount(content.getId());
    }

    public void decrRecommendedCount(Content content) {
        content.setRecommendedCount(content.getRecommendedCount() - 1);
        decrRecommendedCount(content.getId());
    }

    public void incrSharedCount(Content content) {
        content.setSharedCount(content.getSharedCount() + 1);
        incrSharedCount(content.getId());
    }

    public void incrViewedCount(Content content) {
        content.setViewedCount(content.getViewedCount() + 1);
        incrViewedCount(content.getId());
    }

    private void incrRecommendedCount(String contentId) {
        HttpUtil.getInstance().updateRecommendationCount(Config.UPDATE_RECOMMENDATION_COUNT, contentId, true, new UpdateRecommendationCountCallback());
    }

    private void decrRecommendedCount(String contentId) {
        HttpUtil.getInstance().updateRecommendationCount(Config.UPDATE_RECOMMENDATION_COUNT, contentId, false, new UpdateRecommendationCountCallback());
    }

    private void incrViewedCount(String contentId) {
        HttpUtil.getInstance().incrViewCount(Config.INCR_VIEW_COUNT, contentId, new IncrViewCountCallback());
    }

    public void incrSharedCount(String contentId) {
        HttpUtil.getInstance().incrViewCount(Config.INCR_SHARED_COUNT, contentId, new IncrSharedCountCallback());
    }

    private class UpdateRecommendationCountCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
            } else {
            }
        }
    }

    private class IncrSharedCountCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
            } else {
            }
        }
    }

    private class IncrViewCountCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
            } else {
            }
        }
    }
}
