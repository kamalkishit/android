package com.humanize.android.data;

import com.humanize.android.config.Config;
import com.humanize.android.config.StringConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kamal on 1/15/16.
 */
public class User {

    private List<String> categories;
    private List<String> bookmarks;
    private PaperTime paperTime;
    private boolean paperNotification;
    private boolean notification;

    public User() {
        categories = new ArrayList<>();
        bookmarks = new ArrayList<>();
        paperTime = new PaperTime(Config.PAPER_HOUR, Config.PAPER_MINUTE);
        paperNotification = true;
        notification = true;

        categories.add(StringConstants.ACHIEVERS);
        categories.add(StringConstants.BEAUTIFUL);
        categories.add(StringConstants.CHANGEMAKERS);
        categories.add(StringConstants.EDUCATION);
        categories.add(StringConstants.EMPOWERMENT);
        categories.add(StringConstants.ENVIRONMENT);
        categories.add(StringConstants.GOVERNANCE);
        categories.add(StringConstants.HEALTH);
        categories.add(StringConstants.HUMANITY);
        categories.add(StringConstants.INSPIRING);
        categories.add(StringConstants.KINDNESS);
        categories.add(StringConstants.LAW_AND_JUSTICE);
        categories.add(StringConstants.REAL_HEROES);
        categories.add(StringConstants.SCIENCE_AND_TECH);
        categories.add(StringConstants.SMILE);
        categories.add(StringConstants.SPORTS);
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(List<String> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public PaperTime getPaperTime() {
        return paperTime;
    }

    public void setPaperTime(PaperTime paperTime) {
        this.paperTime = paperTime;
    }

    public boolean getPaperNotification() {
        return paperNotification;
    }

    public void setPaperNotification(boolean paperNotification) {
        this.paperNotification = paperNotification;
    }

    public boolean getNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }
}
