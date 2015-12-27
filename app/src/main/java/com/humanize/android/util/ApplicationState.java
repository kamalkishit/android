package com.humanize.android.util;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.google.gson.Gson;

import com.humanize.android.HttpResponseCallback;
import com.humanize.android.JsonParser;
import com.humanize.android.activity.CardActivity;
import com.humanize.android.common.Constants;
import com.humanize.android.content.data.Contents;
import com.humanize.android.data.User;
import com.humanize.android.service.BookmarkService;
import com.humanize.android.service.LikeService;
import com.humanize.android.service.PaperService;
import com.humanize.android.service.SharedPreferencesService;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Kamal on 7/7/15.
 */
public class ApplicationState extends Application{

    public static Context context = null;
    public static User user = null;

    public void onCreate(){
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());

        initialize();
    }

    private void initialize() {
        ApplicationState.context = getApplicationContext();

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        // total pixel width of screen - margin on both side for content card
        int sidePixels = Math.round(Constants.MEDIUM_MARGIN * 2 * (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        int imageWidth = metrics.widthPixels - sidePixels;
        int imageHeight = (imageWidth*Config.ASPECT_RATIO_WIDTH)/Config.ASPECT_RATIO_HEIGHT;
        Config.IMAGE_WIDTH = imageWidth;
        Config.IMAGE_HEIGHT = imageHeight;

        JsonParser jsonParser = new JsonParser();

        try {
            if (SharedPreferencesService.getInstance().getString(Config.JSON_USER_DATA) != null) {
                ApplicationState.user = jsonParser.fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_USER_DATA), User.class);
            }

            if (SharedPreferencesService.getInstance().getString(Config.JSON_CONTENTS) != null) {
                CardActivity.contents = jsonParser.fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_CONTENTS), Contents.class);
            }

            if (SharedPreferencesService.getInstance().getString(Config.JSON_BOOKMARKED_CONTENTS) != null) {
                Contents bookmarks = jsonParser.fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_BOOKMARKED_CONTENTS), Contents.class);
                BookmarkService bookmarkService = BookmarkService.getInstance();
                bookmarkService.setBookmarks(bookmarks);
            }

            if (SharedPreferencesService.getInstance().getString(Config.JSON_RECOMMENDED_CONTENTS) != null) {
                Contents likes = jsonParser.fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_RECOMMENDED_CONTENTS), Contents.class);
                LikeService likeService = LikeService.getInstance();
                likeService.setLikes(likes);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        /*if (SharedPreferencesStorage.getInstance().getString(Config.JSON_PAPER) != null) {
            Contents paper = new Gson().fromJson(SharedPreferencesStorage.getInstance().getString(Config.JSON_PAPER), Contents.class);
            PaperService paperService = PaperService.getInstance();
            paperService.setPaper(paper);
        } else {
            getPaper();
        }*/

        // for disk caching in picasso
        // http://stackoverflow.com/questions/23978828/how-do-i-use-disk-caching-in-picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso picasso = builder.build();
        //picasso.setIndicatorsEnabled(true);
        //picasso.setLoggingEnabled(true);
        Picasso.setSingletonInstance(picasso);
    }

    private void getPaper() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getPaper(new HttpResponseCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    SharedPreferencesService.getInstance().putString(Config.JSON_PAPER, response);
                    Contents paper = new Gson().fromJson(response, Contents.class);
                    PaperService paperService = PaperService.getInstance();
                    paperService.setPaper(paper);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                System.out.println("failure");
            }
        });
    }

    public static void setUser(User user) {
        ApplicationState.user = user;
    }

    public static User getUser() {
        return ApplicationState.user;
    }

    public static Context getAppContext() {
        return ApplicationState.context;
    }
}
