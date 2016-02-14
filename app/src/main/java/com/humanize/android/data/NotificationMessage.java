package com.humanize.android.data;

import java.util.Map;

/**
 * Created by kamal on 1/31/16.
 */
public class NotificationMessage {

    private String title;
    private String body;
    private String imageUrl;
    private String contentUrl;
    private NotificationMessageType type;
    private Map<String,String> notificationActionMap;

    public NotificationMessageType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setType(NotificationMessageType type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public Map<String, String> getNotificationActionMap() {
        return notificationActionMap;
    }

    public void setNotificationActionMap(Map<String, String> notificationActionMap) {
        this.notificationActionMap = notificationActionMap;
    }
}

