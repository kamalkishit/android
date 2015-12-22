package com.humanize.android;

/**
 * Created by kamal on 12/18/15.
 */
public class SpinnerItem {

    String text;
    Integer imageId;

    public SpinnerItem(String text, Integer imageId) {
        this.text = text;
        this.imageId = imageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
}