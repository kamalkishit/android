package com.humanize.android.data;

/**
 * Created by Kamal on 9/17/15.
 */

import java.sql.Time;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class User {

    private String id;

    private String userId;

    private String emailId;

    private String password;

    private List<String> typeOfArticles;

    private List<String> categories;

    private List<String> recommended;

    private List<String> bookmarked;

    private List<String> created;

    private boolean isConfigured;

    private long paperConfigurationDate;

    private long createdDate;

    private long lastModifiedDate;

    public User() {
        this.typeOfArticles = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.recommended = new ArrayList<>();
        this.bookmarked = new ArrayList<>();
        this.created = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getTypeOfArticles() {
        return typeOfArticles;
    }

    public void setTypeOfArticles(List<String> typeOfArticles) {
        this.typeOfArticles = typeOfArticles;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getRecommended() {
        return recommended;
    }

    public void setRecommended(List<String> recommended) {
        this.recommended = recommended;
    }

    public List<String> getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(List<String> bookmarked) {
        this.bookmarked = bookmarked;
    }

    public List<String> getCreated() {
        return created;
    }

    public void setCreated(List<String> created) {
        this.created = created;
    }

    public boolean getIsConfigured() {
        return isConfigured;
    }

    public void setIsConfigured(boolean isConfigured) {
        this.isConfigured = isConfigured;
    }

    public long getPaperConfigurationDate() {
        return paperConfigurationDate;
    }

    public void setPaperConfigurationDate(long paperConfigurationDate) {
        this.paperConfigurationDate = paperConfigurationDate;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}

