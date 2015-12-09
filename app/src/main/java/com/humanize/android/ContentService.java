package com.humanize.android;

import com.humanize.android.content.data.Content;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kamal on 9/15/15.
 */
public class ContentService {
    private Map<String, Content> contentMap;
    private static ContentService contentService = null;

    public void createContent(Content content) {


    }

    private ContentService() {
        this.contentMap = new HashMap<String, Content>();
    }

    public static ContentService getInstance() {
        if (contentService == null) {
            contentService = new ContentService();
        }

        return contentService;
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
            content.setLikesCount(content.getLikesCount() + 1);
        } else {
            content = new Content();
            content.setId(id);
            content.setLikesCount(1);
            contentMap.put(id, content);
        }
    }

    public void decrementLikeCount(String id) {
        Content content = contentMap.get(id);
        if (content != null) {
            content.setLikesCount(content.getLikesCount() - 1);
        } else {
            content = new Content();
            content.setId(id);
            content.setLikesCount(-1);
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
            content.setViewsCount(content.getViewsCount() + 1);
        } else {
            content = new Content();
            content.setId(id);
            content.setViewsCount(1);
            contentMap.put(id, content);
        }
    }
}
