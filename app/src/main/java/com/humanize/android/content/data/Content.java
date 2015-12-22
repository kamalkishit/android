package com.humanize.android.content.data;

import java.util.ArrayList;

/**
 * Created by Kamal on 9/17/15.
 */

public class Content {

    private String id;

    private String contentId;

    private String url;

    private String userId;

    private String title;

    private String description;

    private String imageURL;

    private String originalImageURL;

    private String source;

    private String type;

    private String category;

    private ArrayList<String> subCategories;

    private String videoURL;

    private int recommendedCount;

    private int viewedCount;

    private int sharedCount;

    private boolean isVerified;

    private boolean isDeleted;

    private long createdDate;

    private long lastModifiedDate;

    public Content() {
        this.subCategories = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getOriginalImageURL() {
        return originalImageURL;
    }

    public void setOriginalImageURL(String originalImageURL) {
        this.originalImageURL = originalImageURL;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<String> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<String> subCategories) {
        this.subCategories = subCategories;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public int getRecommendedCount() {
        return recommendedCount;
    }

    public void setRecommendedCount(int recommendedCount) {
        this.recommendedCount = recommendedCount;
    }

    public int getViewedCount() {
        return viewedCount;
    }

    public void setViewedCount(int viewsCount) {
        this.viewedCount = viewsCount;
    }

    public int getSharedCount() {
        return sharedCount;
    }

    public void setSharedCount(int sharedCount) {
        this.sharedCount = sharedCount;
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
