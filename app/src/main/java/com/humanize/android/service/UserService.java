package com.humanize.android.service;

/**
 * Created by kamal on 1/20/16.
 */
public interface UserService {

    boolean isBookmarked(String contentId);
    void bookmark(String contentId);
    boolean isUpvoted(String contentId);
    void upvote(String contentId);
}
