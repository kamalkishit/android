package com.humanize.android.data;

import com.humanize.android.config.StringConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kamal on 1/15/16.
 */
public class User {

    private List<String> categories;
    private List<String> bookmarks;

    public User() {
        categories = new ArrayList<>();
        bookmarks = new ArrayList<>();

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
}
