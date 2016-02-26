package com.humanize.android.service;

import com.humanize.android.data.GuestUser;
import com.humanize.android.helper.ApplicationState;

/**
 * Created by kamal on 1/20/16.
 */
public class UserServiceImpl implements  UserService {

    private GuestUser guestUser;

    private static final String TAG = UserServiceImpl.class.getSimpleName();

    public UserServiceImpl() {
        guestUser = ApplicationState.getGuestUser();
    }

    public boolean isBookmarked(String contentId) {
        return guestUser.getBookmarks().contains(contentId);
    }

    public void bookmark(String contentId) {
        if (!guestUser.getBookmarks().contains(contentId)) {
            guestUser.getBookmarks().add(0, contentId);
        }
    }

    public void unbookmark(String contentId) {
        if (guestUser.getBookmarks().contains(contentId)) {
            guestUser.getBookmarks().remove(contentId);
        }
    }

    public boolean isUpvoted(String contentId) {
        return guestUser.getUpvotes().contains(contentId);
    }

    public void upvote(String contentId) {
        if (!guestUser.getUpvotes().contains(contentId)) {
            guestUser.getUpvotes().add(0, contentId);
        }
    }

    public void downvote(String contentId) {
        if (guestUser.getUpvotes().contains(contentId)) {
            guestUser.getUpvotes().remove(contentId);
        }
    }
}
