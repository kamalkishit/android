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

    private static ContentService contentService = null;
    private static String TAG = ContentService.class.getSimpleName();
    private Map<String, Content> contentMap;

    public ContentService() {
        this.contentMap = new HashMap<>();
    }

    public static ContentService getInstance() {
        if (contentService == null) {
            contentService = new ContentService();
        }

        return contentService;
    }

    public void incrRecommendationCount(String contentId) {
        HttpUtil.getInstance().updateRecommendationCount(Config.UPDATE_RECOMMENDATION_COUNT, contentId, true, new UpdateRecommendationCountCallback());
    }

    public void decrRecommendationCount(String contentId) {
        HttpUtil.getInstance().updateRecommendationCount(Config.UPDATE_RECOMMENDATION_COUNT, contentId, false, new UpdateRecommendationCountCallback());
    }

    public void incrViewCount(String contentId) {
        HttpUtil.getInstance().incrViewCount(Config.INCR_VIEW_COUNT, contentId, new IncrViewCountCallback());
    }

    public void incrSharedCount(String contentId) {
        HttpUtil.getInstance().incrViewCount(Config.INCR_SHARED_COUNT, contentId, new IncrSharedCountCallback());
    }

    public void createContent(Content content) {

    }

    public Map<String, Content> getContentMap() {
        return contentMap;
    }

    public void setContentMap(Map<String, Content> contentMap) {
        this.contentMap = contentMap;
    }

    public void incrementLikeCount(String id) {
        Content content = contentMap.get(id);
        if (content != null) {
            content.setRecommendedCount(content.getRecommendedCount() + 1);
        } else {
            content = new Content();
            content.setId(id);
            content.setRecommendedCount(1);
            contentMap.put(id, content);
        }
    }

    public void decrementLikeCount(String id) {
        Content content = contentMap.get(id);
        if (content != null) {
            content.setRecommendedCount(content.getRecommendedCount() - 1);
        } else {
            content = new Content();
            content.setId(id);
            content.setRecommendedCount(-1);
            contentMap.put(id, content);
        }
    }

    public void incrementShareCount(String id) {
        Content content = contentMap.get(id);
        if (content != null) {
            content.setSharedCount(content.getSharedCount() + 1);
        } else {
            content = new Content();
            content.setId(id);
            content.setSharedCount(1);
            contentMap.put(id, content);
        }
    }

    public void incrementViewCount(String id) {
        Content content = contentMap.get(id);
        if (content != null) {
            content.setViewedCount(content.getViewedCount() + 1);
        } else {
            content = new Content();
            content.setId(id);
            content.setViewedCount(1);
            contentMap.put(id, content);
        }
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
