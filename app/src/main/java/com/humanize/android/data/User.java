package com.humanize.android.data;

import com.humanize.android.config.Config;
import com.humanize.android.config.StringConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kamal on 2/24/16.
 */
public class User {

    private String emailId;
    private String firstName;
    private String lastName;
    private String originalCity;
    private String currentCity;

    private List<String> categories;
    private List<String> bookmarks;
    private List<String> upvotes;
    private PaperTime paperTime;
    private boolean notification;

    public User() {
        categories = new ArrayList<>();
        bookmarks = new ArrayList<>();
        upvotes = new ArrayList<>();
        paperTime = new PaperTime(Config.PAPER_HOUR, Config.PAPER_MINUTE);
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

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
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

    public List<String> getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(List<String> upvotes) {
        this.upvotes = upvotes;
    }

    public PaperTime getPaperTime() {
        return paperTime;
    }

    public void setPaperTime(PaperTime paperTime) {
        this.paperTime = paperTime;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public String getOriginalCity() {
        return originalCity;
    }

    public void setOriginalCity(String originalCity) {
        this.originalCity = originalCity;
    }
}
