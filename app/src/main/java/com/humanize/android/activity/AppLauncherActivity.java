package com.humanize.android.activity;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.humanize.android.R;
import com.humanize.android.config.Config;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.helper.ApplicationState;
import com.humanize.android.service.GsonParserServiceImpl;
import com.humanize.android.service.JsonParserService;
import com.humanize.android.service.SharedPreferencesService;

import butterknife.ButterKnife;

public class AppLauncherActivity extends AppCompatActivity {

    private ActivityLauncher activityLauncher;
    private JsonParserService jsonParserService;

    private static final String TAG = AppLauncherActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launcher);

        ButterKnife.bind(this);

        initialize();
        startNextActivity();
    }

    public void onDestroy() {
        try {
            SharedPreferencesService.getInstance().putString(Config.JSON_USER_DATA, jsonParserService.toJson(ApplicationState.getUser()));
        } catch (Exception exception) {

        }

        super.onDestroy();
    }

    private void initialize() {
        jsonParserService = new GsonParserServiceImpl();
        activityLauncher = new ActivityLauncher();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void startNextActivity() {
            startCardActivity();
    }

    private void startCardActivity() {
        activityLauncher.startCardActivity();
    }
}
