package com.humanize.android.helper;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;

import com.humanize.android.fragment.HomeFragment;
import com.humanize.android.fragment.TrendingFragment;
import com.humanize.android.service.GsonParserServiceImpl;
import com.humanize.android.service.JsonParserService;
import com.humanize.android.activity.HomeActivity;
import com.humanize.android.config.Constants;
import com.humanize.android.data.Contents;
import com.humanize.android.data.User;
import com.humanize.android.service.LogService;
import com.humanize.android.service.LogServiceImpl;
import com.humanize.android.service.RegistrationIntentServiceImpl;
import com.humanize.android.service.SharedPreferencesService;

import com.humanize.android.config.Config;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Kamal on 7/7/15.
 */
public class ApplicationState extends Application{

    private static Context context = null;
    private static User user = null;

    private static final String TAG = ApplicationState.class.getSimpleName();
    private static final LogService logService = new LogServiceImpl();

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

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        // total pixel width of screen - margin on both side for content card
        int sidePixels = Math.round(Constants.SMALL_MARGIN * 2 * (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        int imageWidth = metrics.widthPixels - sidePixels;
        int imageHeight = (imageWidth* Config.ASPECT_RATIO_WIDTH)/Config.ASPECT_RATIO_HEIGHT;
        Config.IMAGE_WIDTH = imageWidth;
        Config.IMAGE_HEIGHT = imageHeight;
        Config.SCREEN_WIDTH = metrics.widthPixels;
        Config.SCREEN_HEIGHT = metrics.heightPixels;
        Config.NAV_DRAWER_WIDTH = (imageWidth*4)/5;

        JsonParserService jsonParserService = new GsonParserServiceImpl();

        try {
            if (SharedPreferencesService.getInstance().getString(Config.JSON_HOME_CONTENTS) != null) {
                HomeFragment.contents = jsonParserService.fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_HOME_CONTENTS), Contents.class);
            }

            if (SharedPreferencesService.getInstance().getString(Config.JSON_TRENDING_CONTENTS) != null) {
                TrendingFragment.contents = jsonParserService.fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_TRENDING_CONTENTS), Contents.class);
            }

            if (SharedPreferencesService.getInstance().getString(Config.JSON_USER_DATA) != null) {
                user = jsonParserService.fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_USER_DATA), User.class);
            } else {
                user = new User();
            }
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }

        // for disk caching in picasso
        // http://stackoverflow.com/questions/23978828/how-do-i-use-disk-caching-in-picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this));
        Picasso picasso = builder.build();
        Picasso.setSingletonInstance(picasso);

        if (!SharedPreferencesService.getInstance().getBoolean(Config.TOKEN_SENT_TO_SERVER)) {
            Intent intent = new Intent(this, RegistrationIntentServiceImpl.class);
            startService(intent);
        }
    }
}
