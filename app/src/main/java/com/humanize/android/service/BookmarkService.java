package com.humanize.android.service;

import com.humanize.android.content.data.Content;
import com.humanize.android.content.data.Contents;

/**
 * Created by Kamal on 10/3/15.
 */
public class BookmarkService {

    private Contents bookmarks;

    private static BookmarkService bookmarkService = null;

    private BookmarkService() {
        bookmarks = new Contents();
    }

    public static BookmarkService getInstance() {
        if (bookmarkService == null) {
            bookmarkService = new BookmarkService();
        }

        return bookmarkService;
    }

    public void addContent(Content content) {
        bookmarks.addContent(content);
    }

    public void removeContent(Content content) {
        bookmarks.removeContent(content);
    }

    public Contents getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(Contents bookmarks) {
        this.bookmarks = bookmarks;
    }
}
