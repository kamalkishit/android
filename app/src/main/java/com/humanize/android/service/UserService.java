package com.humanize.android.service;

import android.util.Log;

import com.humanize.android.activity.BookmarksActivity;
import com.humanize.android.common.Constants;
import com.humanize.android.data.Content;
import com.humanize.android.data.User;
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
public interface UserService {

    void recommend(Content content);
    void recommend(String contentId);
    void bookmark(Content content);
    void bookmark(String contentId);
    List<String> getBookmarkIds();
    List<String> getRecommendationsIds();
    List<String> getMoreBookmarkIds(String bookmarkId);
    List<String> getMoreRecommendationsIds(String recommendationsId);
    List<String> getNewBookmarkIds(String bookmarkId);
    List<String> getNewRecommendationsIds(String recommendationsId);
    boolean isBookmarked(String contentId);
    boolean isRecommended(String contentId);
}
