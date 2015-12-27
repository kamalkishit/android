package com.humanize.android;

import android.util.Log;

import com.humanize.android.common.Constants;
import com.humanize.android.content.data.Content;
import com.humanize.android.content.data.Contents;
import com.humanize.android.data.User;
import com.humanize.android.service.BookmarkService;
import com.humanize.android.service.LikeService;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

/**
 * Created by Kamal on 9/13/15.
 */
public class UserService {

    private User user;
    private ContentService contentService;

    private static String TAG = UserService.class.getSimpleName();

    public UserService() {
        user = ApplicationState.getUser();
        contentService = new ContentService();
    }

    public void recommend(Content content) {
        if (user.getRecommended().contains(content.getId())) {
            user.getRecommended().remove(content.getId());
            unrecommendContent(content.getId());
            contentService.decrRecommendedCount(content);
            updateRecommendedJson(content, false);
        } else {
            user.getRecommended().add(0, content.getId());
            recommendContent(content.getId());
            contentService.incrRecommendedCount(content);
            updateRecommendedJson(content, true);
        }
    }

    public void recommend(String contentId) {
        if (user.getRecommended().contains(contentId)) {
            user.getRecommended().remove(contentId);
            unrecommendContent(contentId);
        } else {
            user.getRecommended().add(0, contentId);
            recommendContent(contentId);
        }
    }

    public void bookmark(Content content) {
        if (user.getBookmarked().contains(content.getId())) {
            user.getBookmarked().remove(content.getId());
            unbookmarkContent(content.getId());
            updateBookmarksJson(content, false);
        } else {
            user.getBookmarked().add(0, content.getId());
            bookmarkContent(content.getId());
            updateBookmarksJson(content, true);
        }
    }

    public void bookmark(String contentId) {
        if (user.getBookmarked().contains(contentId)) {
            user.getBookmarked().remove(contentId);
            unbookmarkContent(contentId);
        } else {
            user.getBookmarked().add(0, contentId);
            bookmarkContent(contentId);
        }
    }

    public List<String> getBookmarkIds() {
        if (user.getBookmarked().size() > 0) {
            if (user.getBookmarked().size() >= Constants.DEFAULT_CONTENTS_SIZE) {
                return user.getBookmarked().subList(0, Constants.DEFAULT_CONTENTS_SIZE);
            } else {
                return user.getBookmarked();
            }
        }

        return null;
    }

    public int getBookmarkIdsSize() {
        return user.getBookmarked().size();
    }

    public List<String> getRecommendationsIds() {
        if (user.getRecommended().size() > 0) {
            if (user.getRecommended().size() >= Constants.DEFAULT_CONTENTS_SIZE) {
                return user.getRecommended().subList(0, Constants.DEFAULT_CONTENTS_SIZE);
            } else {
                return user.getRecommended();
            }
        }

        return null;
    }

    public List<String> getMoreBookmarkIds(String bookmarkId) {
        if (user.getBookmarked().size() > 0 && user.getBookmarked().contains(bookmarkId)) {
            return user.getBookmarked().subList(user.getBookmarked().indexOf(bookmarkId) + 1, user.getBookmarked().indexOf(bookmarkId) + 1 + Constants.DEFAULT_CONTENTS_SIZE);
        }

        return null;
    }

    public List<String> getMoreRecommendationsIds(String recommendationsId) {
        if (user.getRecommended().size() > 0 && user.getRecommended().contains(recommendationsId)) {
            return user.getRecommended().subList(user.getRecommended().indexOf(recommendationsId) + 1, user.getRecommended().indexOf(recommendationsId) + 1 + Constants.DEFAULT_CONTENTS_SIZE);
        }

        return null;
    }

    public List<String> getNewBookmarkIds(String bookmarkId) {
        if (user.getBookmarked().size() > 0 && user.getBookmarked().contains(bookmarkId)) {
            int index = user.getBookmarked().indexOf(bookmarkId);

            if (index >= Constants.DEFAULT_CONTENTS_SIZE) {
                return user.getBookmarked().subList(0, Constants.DEFAULT_CONTENTS_SIZE);
            } else {
                return user.getBookmarked().subList(0, index);
            }
        }

        return null;
    }

    public List<String> getNewRecommendationsIds(String recommendationsId) {
        if (user.getRecommended().size() > 0 && user.getRecommended().contains(recommendationsId)) {
            int index = user.getRecommended().indexOf(recommendationsId);

            if (index >= Constants.DEFAULT_CONTENTS_SIZE) {
                return user.getRecommended().subList(0, Constants.DEFAULT_CONTENTS_SIZE);
            } else {
                return user.getRecommended().subList(0, index);
            }
        }

        return null;
    }

    public boolean isBookmarked(String contentId) {
        return user.getBookmarked().contains(contentId);
    }

    public boolean isRecommended(String contentId) {
        return user.getRecommended().contains(contentId);
    }

    private void recommendContent(String contentId) {
        HttpUtil.getInstance().recommendContentForUser(Config.USER_CONTENT_RECOMMEND_URL, user.getId(), contentId, true, new RecommendContentCallback());
    }

    private void unrecommendContent(String contentId) {
        HttpUtil.getInstance().recommendContentForUser(Config.USER_CONTENT_RECOMMEND_URL, user.getId(), contentId, false, new RecommendContentCallback());
    }

    private void bookmarkContent(String contentId) {
        HttpUtil.getInstance().bookmarkContentForUser(Config.USER_CONTENT_BOOKMARK_URL, user.getId(), contentId, true, new BookmarkContentCallback());
    }

    private void unbookmarkContent(String contentId) {
        HttpUtil.getInstance().bookmarkContentForUser(Config.USER_CONTENT_BOOKMARK_URL, user.getId(), contentId, false, new BookmarkContentCallback());
    }

    private void updateBookmarksJson(Content content, boolean add) {
        updateJson(Config.JSON_BOOKMARKED_CONTENTS, content, add);
    }

    private void updateRecommendedJson(Content content, boolean add) {
        updateJson(Config.JSON_RECOMMENDED_CONTENTS, content, add);
    }

    private void updateJson(String jsonKey, Content content, boolean add) {
        try {
            String json = SharedPreferencesService.getInstance().getString(jsonKey);
            Contents contents = new JsonParser().fromJson(json, Contents.class);

            if (add) {
                contents.addContent(content);
            } else {
                contents.removeContent(content);
            }

            json = new JsonParser().toJson(contents);
            SharedPreferencesService.getInstance().putString(jsonKey, json);
        } catch (Exception exception) {

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
