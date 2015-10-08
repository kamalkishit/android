package com.humanize.android.service;

/**
 * Created by Kamal on 10/3/15.
 */

import com.humanize.android.data.Content;
import com.humanize.android.data.Contents;

public class LikeService {

    private Contents likes;

    private static LikeService likeService = null;

    private LikeService() {
        likes = new Contents();
    }

    public static LikeService getInstance() {
        if (likeService == null) {
            likeService = new LikeService();
        }

        return likeService;
    }

    public void addContent(Content content) {
        likes.addContent(content);
    }

    public void removeContent(Content content) {
        likes.removeContent(content);
    }

    public Contents getLikes() {
        return likes;
    }

    public void setLikes(Contents likes) {
        this.likes = likes;
    }
}

