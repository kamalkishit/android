package com.humanize.android.helper;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.gson.Gson;
import com.humanize.android.activity.AboutUsActivity;
import com.humanize.android.activity.AppLauncherActivity;
import com.humanize.android.activity.BookmarksActivity;
import com.humanize.android.activity.CardActivity;
import com.humanize.android.activity.ContactUsActivity;
import com.humanize.android.activity.LoginActivity;
import com.humanize.android.activity.PaperActivity;
import com.humanize.android.activity.PrivacyActivity;
import com.humanize.android.activity.RecommendArticleActivity;
import com.humanize.android.activity.RecommendationsActivity;
import com.humanize.android.activity.ResetPasswordActivity;
import com.humanize.android.activity.UsageActivity;
import com.humanize.android.common.StringConstants;
import com.humanize.android.content.data.Contents;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by kamal on 12/9/15.
 */
public class ActivityLauncher {

    public void startAboutUsActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), AboutUsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startUsageActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), UsageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startPrivacyActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), PrivacyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startAppLauncherActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), AppLauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startResetPasswordActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), ResetPasswordActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startCardActivity(View view) {
        if (CardActivity.contents != null) {
            Intent intent = new Intent(ApplicationState.getAppContext(), CardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ApplicationState.getAppContext().startActivity(intent);
        } else {
            getContents(view);
        }
    }

    public void startRecommendAnArticleActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), RecommendArticleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startPaperActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), PaperActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startRecommendedActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), RecommendationsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startBookmarksActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), BookmarksActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startLoginActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startLoginActivityWithClearStack() {
        Intent intent = new Intent(ApplicationState.getAppContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startContactUsActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), ContactUsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    private void getContents(View view) {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getContents(new ContentCallback(view));
    }

    private void success(View view, String response) {
        System.out.println(response);
        try {
            Contents contents = new Gson().fromJson(response, Contents.class);
            SharedPreferencesService.getInstance().putString(Config.JSON_CONTENTS, response);
            CardActivity.contents = contents;

            startCardActivity(view);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private class ContentCallback implements Callback {

        private View view;
        public ContentCallback(View view) {
            this.view = view;
        }
        @Override
        public void onFailure(Request request, IOException exception) {
            exception.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Snackbar snackbar = Snackbar.make(view, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar snackbar = Snackbar.make(view, StringConstants.FAILURE_STR, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        success(view, responseStr);
                    }
                });
            }
        }
    }



}
