package com.humanize.android.activity;

import android.os.Bundle;
import android.os.Handler;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.humanize.android.R;
import com.humanize.android.common.Constants;
import com.humanize.android.helper.ActivityLauncher;

import butterknife.ButterKnife;

public class AppLauncherActivity extends AppCompatActivity {

    private ActivityLauncher activityLauncher;

    private static final String TAG = AppLauncherActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launcher);

        ButterKnife.bind(this);

        initialize();
        startNextActivity();
    }

    private void initialize() {
        activityLauncher = new ActivityLauncher();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void startNextActivity() {
            startCardActivity();
    }

    private void startCardActivity() {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    activityLauncher.startCardActivity();
                    finish();
                }
            }, Constants.SPLASH_SCREEN_DELAY_TIME);
    }
}
