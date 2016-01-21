package com.humanize.android.service;

import com.humanize.android.data.Content;
import com.humanize.android.data.User;
import com.humanize.android.helper.ApplicationState;

/**
 * Created by kamal on 1/20/16.
 */
public class UserServiceImpl implements  UserService {

    private User user;

    public UserServiceImpl() {
        user = ApplicationState.getUser();
    }

    public boolean isBookmarked(String contentId) {
        return user.getBookmarks().contains(contentId);
    }

    public void bookmark(Content content) {
        if (user.getBookmarks().contains(content.getId())) {
            user.getBookmarks().remove(content.getId());
        } else {
            user.getBookmarks().add(0, content.getId());
        }
    }

    public void bookmark(String contentId) {
        if (user.getBookmarks().contains(contentId)) {
            user.getBookmarks().remove(contentId);
        } else {
            user.getBookmarks().add(0, contentId);
        }
    }
}
