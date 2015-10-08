package com.humanize.android.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.google.gson.Gson;
import com.humanize.android.AlarmReceiver;
import com.humanize.android.HttpResponseCallback;
import com.humanize.android.R;
import com.humanize.android.data.Contents;
import com.humanize.android.data.User;
import com.humanize.android.service.BookmarkService;
import com.humanize.android.service.LikeService;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.humanize.android.util.SharedPreferencesStorage;

import java.util.Calendar;

public class AppLauncherActivity extends AppCompatActivity {

    SharedPreferencesStorage sharedPreferencesStorage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launcher);

        initialize();
        //JobScheduler.schedulePaper();
        //createAlarm();
        startNextActivity();
    }

    private void initialize() {
        sharedPreferencesStorage = SharedPreferencesStorage.getInstance();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int px = Math.round(32 * (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)); // TBD: hardcoding of 32 dp for side margin
        int imageWidth = metrics.widthPixels - px;
        int imageHeight = (imageWidth*Config.ASPECT_RATIO_WIDTH)/Config.ASPECT_RATIO_HEIGHT;
        Config.IMAGE_WIDTH = imageWidth;
        Config.IMAGE_HEIGHT = imageHeight;
        System.out.println(imageHeight);
        System.out.println(imageWidth);
    }

    private void startNextActivity() {
        boolean isLoggedIn = sharedPreferencesStorage.getBoolean(Config.IS_LOGGED_IN);

        if (isLoggedIn && ApplicationState.getUser() != null) {
            startCardActivity();
        } else if (isLoggedIn){
            getUserdata();
        } else {
            startLoginSignupActivity();
        }
    }

    private void startCardActivity() {
        if (CardActivity.contents != null) {
            Intent intent = new Intent(getApplicationContext(), CardActivity.class);
            startActivity(intent);
        } else {
            getContents();
        }
    }

    private void getUserdata() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getUserdata(Config.GET_USERDATA_URL, new HttpResponseCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println(" user data success");
                ApplicationState.setUser(new Gson().fromJson(response, User.class));
                startCardActivity();
            }

            @Override
            public void onFailure(String errorMsg) {
                System.out.println("failure");
                failure(errorMsg);
            }
        });
    }

    private void startLoginSignupActivity() {
        Intent intent = new Intent(getApplicationContext(), LaunchScreenActivity.class);
        startActivity(intent);
    }

    private void getContents() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getContents(new HttpResponseCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("success");
                success(response);
            }

            @Override
            public void onFailure(String errorMsg) {
                System.out.println("failure");
                failure(errorMsg);
            }
        });
    }

    private void success(String response) {
        System.out.println(response);
        try {
            Contents contents = new Gson().fromJson(response, Contents.class);
            sharedPreferencesStorage.putString(Config.JSON_CONTENTS, response);
            CardActivity.contents = contents;

            Intent intent = new Intent(getApplicationContext(), CardActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void failure(String response) {
        System.out.println("failure");
        System.out.println(response);
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
        sharedPreferencesStorage.putString(Config.USER_DATA_JSON, new Gson().toJson(ApplicationState.getUser()));
        sharedPreferencesStorage.putString(Config.JSON_CONTENTS, new Gson().toJson(CardActivity.contents));
        sharedPreferencesStorage.putString(Config.JSON_LIKES, new Gson().toJson(LikeService.getInstance().getLikes()));
        sharedPreferencesStorage.putString(Config.JSON_BOOKMARKS, new Gson().toJson(BookmarkService.getInstance().getBookmarks()));
        updateUserData();
        updateContentsData();
        super.onDestroy();
    }

    private void updateUserData() {
        String userdataJson = new Gson().toJson(ApplicationState.getUser());

        if (userdataJson != null) {
            HttpUtil.getInstance().updateUser(Config.USER_UPDATE_URL, userdataJson, new HttpResponseCallback() {
                @Override
                public void onSuccess(String response) {
                    System.out.println("updated successfully");
                }

                @Override
                public void onFailure(String errorMsg) {
                    System.out.println("update failure");
                }
            });
        }
    }

    private void updateContentsData() {

    }

}
