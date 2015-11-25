package com.humanize.android;

import com.humanize.android.content.data.Content;
import com.humanize.android.data.User;
import com.humanize.android.service.BookmarkService;
import com.humanize.android.service.LikeService;

/**
 * Created by Kamal on 9/13/15.
 */
public class UserService {

    private User user;

    public UserService(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void like(Content content) {
        if (user.getLikes().contains(content.getId())) {
            user.getLikes().remove(content.getId());
            LikeService.getInstance().removeContent(content);
        } else {
            user.getLikes().add(content.getId());
            LikeService.getInstance().addContent(content);
        }
    }

    public boolean isLiked(String id) {
        if (user.getLikes().contains(id)) {
            return true;
        }

        return false;
    }

    public boolean isBookmarked(String id) {
        if (user.getBookmarks().contains(id)) {
            return true;
        }

        return false;
    }

    public void bookmark(Content content) {
        if (user.getBookmarks().contains(content.getId())) {
            user.getBookmarks().remove(content.getId());
            BookmarkService.getInstance().removeContent(content);
        } else {
            user.getBookmarks().add(content.getId());
            BookmarkService.getInstance().addContent(content);
        }
    }
}
