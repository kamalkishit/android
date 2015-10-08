package com.humanize.android;

/**
 * Created by Kamal on 8/10/15.
 */
public class NavigationDrawer {
    private boolean showNotify;
    private String title;

    public NavigationDrawer() {

    }

    public NavigationDrawer(boolean showNotify, String title) {
        this.showNotify = showNotify;
        this.title = title;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}