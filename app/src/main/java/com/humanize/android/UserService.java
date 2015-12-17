package com.humanize.android;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.humanize.android.activity.CardActivity;
import com.humanize.android.common.Constants;
import com.humanize.android.content.data.Content;
import com.humanize.android.content.data.Contents;
import com.humanize.android.data.User;
import com.humanize.android.service.BookmarkService;
import com.humanize.android.service.LikeService;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by Kamal on 9/13/15.
 */
public class UserService {

    private User user;

    private static String TAG = UserService.class.getSimpleName();

    public UserService() {

    }

    public void recommendContent(String contentId) {
        HttpUtil.getInstance().recommendContentForUser(Config.USER_CONTENT_RECOMMEND_URL, ApplicationState.getUser().getUserId(), contentId, true, new RecommendContentCallback());
    }

    public void unrecommendContent(String contentId) {
        HttpUtil.getInstance().recommendContentForUser(Config.USER_CONTENT_RECOMMEND_URL, ApplicationState.getUser().getUserId(), contentId, false, new RecommendContentCallback());
    }

    public void bookmarkContent(String contentId) {
        HttpUtil.getInstance().bookmarkContentForUser(Config.USER_CONTENT_BOOKMARK_URL, ApplicationState.getUser().getUserId(), contentId, true, new BookmarkContentCallback());
    }

    public void unbookmarkContent(String contentId) {
        HttpUtil.getInstance().bookmarkContentForUser(Config.USER_CONTENT_BOOKMARK_URL, ApplicationState.getUser().getUserId(), contentId, false, new BookmarkContentCallback());
    }

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

    private class RecommendContentCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
            } else {
            }
        }
    }

    private class BookmarkContentCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
            } else {
            }
        }
    }
}
