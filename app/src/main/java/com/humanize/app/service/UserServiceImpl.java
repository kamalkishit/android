package com.humanize.app.service;

import com.humanize.app.data.Content;
import com.humanize.app.data.User;
import com.humanize.app.helper.ApplicationState;

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
