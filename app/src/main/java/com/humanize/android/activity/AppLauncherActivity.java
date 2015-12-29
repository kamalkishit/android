package com.humanize.android.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.humanize.android.AlarmReceiver;
import com.humanize.android.JsonParser;
import com.humanize.android.R;
import com.humanize.android.common.Constants;
import com.humanize.android.common.StringConstants;
import com.humanize.android.content.data.Contents;
import com.humanize.android.data.User;
import com.humanize.android.helper.ActivityLauncher;
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
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AppLauncherActivity extends AppCompatActivity {

    @Bind(R.id.relativeLayout) RelativeLayout relativeLayout;

    ActivityLauncher activityLauncher;

    private static final String TAG = AppLauncherActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launcher);

        ButterKnife.bind(this);

        initialize();
        //JobScheduler.schedulePaper();
        //createAlarm();
        startNextActivity();
    }

    private void initialize() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        activityLauncher = new ActivityLauncher();
    }

    private void startNextActivity() {
        boolean isLoggedIn = SharedPreferencesService.getInstance().getBoolean(Config.IS_LOGGED_IN);

        if (isLoggedIn && ApplicationState.getUser() != null) {
            startCardActivity();
        } else if (isLoggedIn){
            getUserdata();
        } else {
            startLoginActivity();
        }
    }

    private void startLoginActivity() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                activityLauncher.startLoginActivity();
                finish();
            }
        }, Constants.SPLASH_SCREEN_DELAY_TIME);
    }

    private void startCardActivity() {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    activityLauncher.startCardActivity(relativeLayout);
                    finish();
                }
            }, Constants.SPLASH_SCREEN_DELAY_TIME);
    }

    private void getUserdata() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getUserdata(Config.USER_DATA_URL, new UserDataCallback());
    }

    private void createAlarm() {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 9);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 2, alarmIntent);
    }

    public void onDestroy() {
        try {
            SharedPreferencesService.getInstance().putString(Config.JSON_USER_DATA, new JsonParser().toJson(ApplicationState.getUser()));
            //SharedPreferencesService.getInstance().putString(Config.JSON_CONTENTS, new JsonParser().toJson(CardActivity.contents));
            SharedPreferencesService.getInstance().putString(Config.JSON_RECOMMENDED_CONTENTS, new JsonParser().toJson(LikeService.getInstance().getLikes()));
            SharedPreferencesService.getInstance().putString(Config.JSON_BOOKMARKED_CONTENTS, new JsonParser().toJson(BookmarkService.getInstance().getBookmarks()));
        } catch (Exception exception) {

        }

        super.onDestroy();
    }

    private class UserDataCallback implements Callback {
        @Override
        public void onFailure(Request request, IOException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(relativeLayout, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(relativeLayout, StringConstants.FAILURE_STR, Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ApplicationState.setUser(new JsonParser().fromJson(responseStr, User.class));
                            startCardActivity();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}
