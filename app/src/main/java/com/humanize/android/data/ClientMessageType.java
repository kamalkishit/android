package com.humanize.android.data;

/**
 * Created by kamal on 1/31/16.
 */
public enum ClientMessageType {

    CONTENT("content");

    String type;

    public String getType() {
        return type;
    }

    ClientMessageType(String type){

        this.type = type;
    }
}
