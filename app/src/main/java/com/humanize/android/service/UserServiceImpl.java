package com.humanize.android.service;

import com.humanize.android.data.Content;
import com.humanize.android.data.User;
import com.humanize.android.helper.ApplicationState;

/**
 * Created by kamal on 1/20/16.
 */
public class UserServiceImpl implements  UserService {

    private User user;

    private static final String TAG = UserServiceImpl.class.getSimpleName();

    public UserServiceImpl() {
        user = ApplicationState.getUser();
    }

    public boolean isBookmarked(String contentId) {
        return user.getBookmarks().contains(contentId);
    }

    public void bookmark(String contentId) {
        if (!user.getBookmarks().contains(contentId)) {
            user.getBookmarks().add(0, contentId);
        }
    }

    public void unbookmark(String contentId) {
        if (user.getBookmarks().contains(contentId)) {
            user.getBookmarks().remove(contentId);
        }
    }

    public boolean isUpvoted(String contentId) {
        return user.getUpvotes().contains(contentId);
    }

    public void upvote(String contentId) {
        if (!user.getUpvotes().contains(contentId)) {
            user.getUpvotes().add(0, contentId);
        }
    }

    public void downvote(String contentId) {
        if (user.getUpvotes().contains(contentId)) {
            user.getUpvotes().remove(contentId);
        }
    }
}
