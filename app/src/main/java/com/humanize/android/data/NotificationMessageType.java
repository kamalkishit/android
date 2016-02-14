package com.humanize.android.data;

/**
 * Created by kamal on 1/31/16.
 */
public enum NotificationMessageType {

    CONTENT("content");

    String type;

    public String getType() {
        return type;
    }

    NotificationMessageType(String type){

        this.type = type;
    }
}
