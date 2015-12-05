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

    private String tempPassword;

    private String invitationCode;

    private List<String> typeOfArticles;

    private List<String> categories;

    private Time paperTime;

    private Set<String> likes;

    private Set<String> bookmarks;

    private Set<String> contentsCreated;

    private long createdDate;

    private long lastModifiedDate;

    public User() {
        this.typeOfArticles = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.likes = new LinkedHashSet<>();
        this.bookmarks = new LinkedHashSet<>();
        this.contentsCreated = new LinkedHashSet<>();
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

    public String getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
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

    public Time getPaperTime() {
        return paperTime;
    }

    public void setPaperTime(Time paperTime) {
        this.paperTime = paperTime;
    }

    public Set<String> getLikes() {
        return likes;
    }

    public void setLikes(Set<String> likes) {
        this.likes = likes;
    }

    public Set<String> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(Set<String> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public Set<String> getContentsCreated() {
        return contentsCreated;
    }

    public void setContentsCreated(Set<String> contentsCreated) {
        this.contentsCreated = contentsCreated;
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

