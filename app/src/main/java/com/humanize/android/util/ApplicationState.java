package com.humanize.android.util;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;

import com.humanize.android.JsonParser;
import com.humanize.android.OkHttp3Downloader;
import com.humanize.android.activity.CardActivity;
import com.humanize.android.common.Constants;
import com.humanize.android.data.Contents;
import com.humanize.android.data.User;
import com.humanize.android.service.SharedPreferencesService;

import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Kamal on 7/7/15.
 */
public class ApplicationState extends Application{

    private static Context context = null;
    private static User user = null;

    public void onCreate(){
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());

        initialize();
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

    private void initialize() {
        ApplicationState.context = getApplicationContext();
        user = new User();

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        // total pixel width of screen - margin on both side for content card
        int sidePixels = Math.round(Constants.SMALL_MARGIN * 2 * (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        int imageWidth = metrics.widthPixels - sidePixels;
        int imageHeight = (imageWidth*Config.ASPECT_RATIO_WIDTH)/Config.ASPECT_RATIO_HEIGHT;
        Config.IMAGE_WIDTH = imageWidth;
        Config.IMAGE_HEIGHT = imageHeight;
        Config.SCREEN_WIDTH = metrics.widthPixels;
        Config.SCREEN_HEIGHT = metrics.heightPixels;
        Config.NAV_DRAWER_WIDTH = (imageWidth*4)/5;

        JsonParser jsonParser = new GsonParserImpl();

        try {
            if (SharedPreferencesService.getInstance().getString(Config.JSON_CONTENTS) != null) {
                CardActivity.contents = jsonParser.fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_CONTENTS), Contents.class);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        // for disk caching in picasso
        // http://stackoverflow.com/questions/23978828/how-do-i-use-disk-caching-in-picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this));
        Picasso picasso = builder.build();
        Picasso.setSingletonInstance(picasso);
    }
}
