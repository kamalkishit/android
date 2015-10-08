package com.humanize.android.util;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.google.gson.Gson;
import com.humanize.android.HttpResponseCallback;
import com.humanize.android.activity.CardActivity;
import com.humanize.android.data.Contents;
import com.humanize.android.data.User;
import com.humanize.android.service.BookmarkService;
import com.humanize.android.service.LikeService;
import com.humanize.android.service.PaperService;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Kamal on 7/7/15.
 */
public class ApplicationState extends Application{

    private static Context context;
    private static User user = null;

    public void onCreate(){
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());

        initialize();
    }

    private void initialize() {
        ApplicationState.context = getApplicationContext();
        if (SharedPreferencesStorage.getInstance().getString(Config.USER_DATA_JSON) != null) {
            ApplicationState.user = new Gson().fromJson(SharedPreferencesStorage.getInstance().getString(Config.USER_DATA_JSON), User.class);
        }

        if (SharedPreferencesStorage.getInstance().getString(Config.JSON_CONTENTS) != null) {
            CardActivity.contents = new Gson().fromJson(SharedPreferencesStorage.getInstance().getString(Config.JSON_CONTENTS), Contents.class);
        }

        if (SharedPreferencesStorage.getInstance().getString(Config.JSON_BOOKMARKS) != null) {
            Contents bookmarks = new Gson().fromJson(SharedPreferencesStorage.getInstance().getString(Config.JSON_BOOKMARKS), Contents.class);
            BookmarkService bookmarkService = BookmarkService.getInstance();
            bookmarkService.setBookmarks(bookmarks);
        }

        if (SharedPreferencesStorage.getInstance().getString(Config.JSON_LIKES) != null) {
            Contents likes = new Gson().fromJson(SharedPreferencesStorage.getInstance().getString(Config.JSON_LIKES), Contents.class);
            LikeService likeService = LikeService.getInstance();
            likeService.setLikes(likes);
        }

        if (SharedPreferencesStorage.getInstance().getString(Config.JSON_PAPER) != null) {
            Contents paper = new Gson().fromJson(SharedPreferencesStorage.getInstance().getString(Config.JSON_PAPER), Contents.class);
            PaperService paperService = PaperService.getInstance();
            paperService.setPaper(paper);
        } else {
            getPaper();
        }

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
                    SharedPreferencesStorage.getInstance().putString(Config.JSON_PAPER, response);
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
