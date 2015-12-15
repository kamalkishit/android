package com.humanize.android.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humanize.android.AlarmReceiver;
import com.humanize.android.NewLoginActivity;
import com.humanize.android.R;
import com.humanize.android.authentication.activity.LoginActivity;
import com.humanize.android.common.Constants;
import com.humanize.android.common.StringConstants;
import com.humanize.android.content.data.Contents;
import com.humanize.android.data.User;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.service.BookmarkService;
import com.humanize.android.service.LikeService;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.humanize.android.service.SharedPreferencesService;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AppLauncherActivity extends AppCompatActivity {

    @Bind(R.id.title) TextView title;

    private static final String TAG = AppLauncherActivity.class.getSimpleName();

    SharedPreferencesService sharedPreferencesService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); */
        setContentView(R.layout.activity_app_launcher);

        ButterKnife.bind(this);

        initialize();
        //JobScheduler.schedulePaper();
        //createAlarm();
        startNextActivity();
    }

    private void initialize() {
        sharedPreferencesService = SharedPreferencesService.getInstance();
    }

    private void startLoginActivity() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(getApplicationContext(), NewLoginActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(AppLauncherActivity.this, (View)title, "loginTransition");
                startActivity(intent, options.toBundle());
                //ActivityLauncher.startNewLoginActivity();
                finish();
            }
        }, Constants.SPLASH_SCREEN_DELAY_TIME);
    }

    private void startNextActivity() {
        boolean isLoggedIn = sharedPreferencesService.getBoolean(Config.IS_LOGGED_IN);

        if (isLoggedIn && ApplicationState.getUser() != null) {
            startCardActivity();
        } else if (isLoggedIn){
            getUserdata();
        } else {
            startLoginActivity();
        }
    }

    private void startCardActivity() {
        if (CardActivity.contents != null) {
            ActivityLauncher.startCardActivity();
        } else {
            getContents();
        }
    }

    private void getUserdata() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getUserdata(Config.USER_DATA_URL, new UserDataCallback());
    }

    private void startLoginSignupActivity() {
        Intent intent = new Intent(getApplicationContext(), LaunchScreenActivity.class);
        startActivity(intent);
    }

    private void getContents() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getContents(new ContentCallback());
    }

    private void success(String response) {
        System.out.println(response);
        try {
            Contents contents = new Gson().fromJson(response, Contents.class);
            sharedPreferencesService.putString(Config.JSON_CONTENTS, response);
            CardActivity.contents = contents;

            ActivityLauncher.startCardActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void failure(String response) {
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
        sharedPreferencesService.putString(Config.USER_DATA_JSON, new Gson().toJson(ApplicationState.getUser()));
        sharedPreferencesService.putString(Config.JSON_CONTENTS, new Gson().toJson(CardActivity.contents));
        sharedPreferencesService.putString(Config.JSON_LIKES, new Gson().toJson(LikeService.getInstance().getLikes()));
        sharedPreferencesService.putString(Config.JSON_BOOKMARKS, new Gson().toJson(BookmarkService.getInstance().getBookmarks()));
        //updateUserData();
        updateContentsData();
        super.onDestroy();
    }

    private void updateUserData() {
        String userdataJson = new Gson().toJson(ApplicationState.getUser());

        if (userdataJson != null) {
            HttpUtil.getInstance().updateUser(Config.USER_UPDATE_URL, userdataJson, new UserUpdationCallback());
        }
    }

    private void updateContentsData() {

    }

    private class ContentCallback implements Callback {
        @Override
        public void onFailure(Request request, IOException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), StringConstants.NETWORK_CONNECTION_ERROR_STR, Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), StringConstants.FAILURE_STR, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                            success(responseStr);
                    }
                });
            }
        }
    }

    private class UserDataCallback implements Callback {
        @Override
        public void onFailure(Request request, IOException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), StringConstants.NETWORK_CONNECTION_ERROR_STR, Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), StringConstants.FAILURE_STR, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ApplicationState.setUser(new Gson().fromJson(responseStr, User.class));
                        startCardActivity();
                        success(responseStr);
                    }
                });
            }
        }
    }

    private class UserUpdationCallback implements Callback {
        @Override
        public void onFailure(Request request, IOException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), StringConstants.NETWORK_CONNECTION_ERROR_STR, Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), StringConstants.FAILURE_STR, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String responseStr = response.body().string().toString();
                            success(responseStr);
                        } catch (IOException exception) {
                            Log.e(TAG, exception.toString());
                        }
                    }
                });
            }
        }
    }
}
