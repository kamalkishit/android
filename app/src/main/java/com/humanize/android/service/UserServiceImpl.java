package com.humanize.android.service;

import android.util.Log;

import com.humanize.android.common.Constants;
import com.humanize.android.data.Content;
import com.humanize.android.data.User;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by kamal on 1/1/16.
 */
public class UserServiceImpl implements UserService {

    private User user;
    private ContentService contentService;

    private static String TAG = UserServiceImpl.class.getSimpleName();

    public UserServiceImpl() {
        user = ApplicationState.getUser();
        contentService = new ContentServiceImpl();
    }

    public void recommend(Content content) {
        if (user.getRecommended().contains(content.getId())) {
            user.getRecommended().remove(content.getId());
            unrecommendContent(content);
            contentService.decrRecommendedCount(content);
        } else {
            user.getRecommended().add(0, content.getId());
            recommendContent(content);
            contentService.incrRecommendedCount(content);
        }
    }

    public void recommend(String contentId) {
        if (user.getRecommended().contains(contentId)) {
            user.getRecommended().remove(contentId);
        } else {
            user.getRecommended().add(0, contentId);
        }
    }

    private void undoRecommend(Content content) {
        if (user.getRecommended().contains(content.getId())) {
            user.getRecommended().remove(content.getId());
            content.setRecommendedCount(content.getRecommendedCount() - 1);
        } else {
            user.getRecommended().add(0, content.getId());
            content.setRecommendedCount(content.getRecommendedCount() + 1);
        }
    }

    public void bookmark(Content content) {
        if (user.getBookmarked().contains(content.getId())) {
            user.getBookmarked().remove(content.getId());
            unbookmarkContent(content);
        } else {
            user.getBookmarked().add(0, content.getId());
            bookmarkContent(content);
        }
    }

    public void bookmark(String contentId) {
        if (user.getBookmarked().contains(contentId)) {
            user.getBookmarked().remove(contentId);
        } else {
            user.getBookmarked().add(0, contentId);
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
            if (user.getBookmarked().indexOf(bookmarkId) + 1 + Constants.DEFAULT_CONTENTS_SIZE > user.getBookmarked().size()) {
                return user.getBookmarked().subList(user.getBookmarked().indexOf(bookmarkId) + 1, user.getBookmarked().indexOf(bookmarkId) + 1 + Constants.DEFAULT_CONTENTS_SIZE);
            } else {
                return user.getBookmarked().subList(user.getBookmarked().indexOf(bookmarkId) + 1, user.getBookmarked().size() - 1);
            }
        }

        return null;
    }

    public List<String> getMoreRecommendationsIds(String recommendationsId) {
        if (user.getRecommended().size() > 0 && user.getRecommended().contains(recommendationsId)) {
            if (user.getRecommended().indexOf(recommendationsId) + 1 + Constants.DEFAULT_CONTENTS_SIZE > user.getRecommended().size()) {
                return user.getRecommended().subList(user.getRecommended().indexOf(recommendationsId) + 1, user.getRecommended().indexOf(recommendationsId) + 1 + Constants.DEFAULT_CONTENTS_SIZE);
            } else {
                return user.getRecommended().subList(user.getRecommended().indexOf(recommendationsId) + 1, user.getRecommended().size() - 1);
            }
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

    private void recommendContent(Content content) {
        HttpUtil.getInstance().recommendContentForUser(Config.USER_CONTENT_RECOMMEND_URL, user.getId(), content.getId(), true, new RecommendContentCallback(content));
    }

    private void unrecommendContent(Content content) {
        HttpUtil.getInstance().recommendContentForUser(Config.USER_CONTENT_RECOMMEND_URL, user.getId(), content.getId(), false, new RecommendContentCallback(content));
    }

    private void bookmarkContent(Content content) {
        HttpUtil.getInstance().bookmarkContentForUser(Config.USER_CONTENT_BOOKMARK_URL, user.getId(), content.getId(), true, new BookmarkContentCallback(content));
    }

    private void unbookmarkContent(Content content) {
        HttpUtil.getInstance().bookmarkContentForUser(Config.USER_CONTENT_BOOKMARK_URL, user.getId(), content.getId(), false, new BookmarkContentCallback(content));
    }

    private class RecommendContentCallback implements Callback {

        private Content content;

        public RecommendContentCallback(Content content) {
            this.content = content;
        }
        @Override
        public void onFailure(Call call, IOException exception) {
            undoRecommend(content);
            Log.e(TAG, exception.toString());
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            if (!response.isSuccessful()) {
                undoRecommend(content);
            } else {
            }
        }
    }

    private class BookmarkContentCallback implements Callback {

        private Content content;

        public BookmarkContentCallback(Content content) {
            this.content = content;
        }

        @Override
        public void onFailure(Call call, IOException exception) {
            bookmark(content.getId());
            Log.e(TAG, exception.toString());
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            if (!response.isSuccessful()) {
                bookmark(content.getId());
            } else {
            }
        }
    }
}

