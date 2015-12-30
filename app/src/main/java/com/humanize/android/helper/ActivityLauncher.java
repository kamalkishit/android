package com.humanize.android.helper;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.humanize.android.JsonParser;
import com.humanize.android.UserService;
import com.humanize.android.activity.AboutUsActivity;
import com.humanize.android.activity.AppLauncherActivity;
import com.humanize.android.activity.BookmarksActivity;
import com.humanize.android.activity.CardActivity;
import com.humanize.android.activity.ContactUsActivity;
import com.humanize.android.activity.ForgotPasswordActivity;
import com.humanize.android.activity.LoginActivity;
import com.humanize.android.activity.PaperActivity;
import com.humanize.android.activity.PrivacyActivity;
import com.humanize.android.activity.RecommendArticleActivity;
import com.humanize.android.activity.RecommendationsActivity;
import com.humanize.android.activity.ResetPasswordActivity;
import com.humanize.android.activity.SelectCategoriesActivity;
import com.humanize.android.activity.SettingsActivity;
import com.humanize.android.activity.SignupActivity;
import com.humanize.android.activity.UsageActivity;
import com.humanize.android.utils.StringConstants;
import com.humanize.android.data.Contents;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.utils.ApplicationState;
import com.humanize.android.utils.Config;
import com.humanize.android.utils.HttpUtil;
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

    public void startForgotPasswordActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), ForgotPasswordActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startSignupActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), SignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startSelectCategoriesActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), SelectCategoriesActivity.class);
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
        Intent intent = new Intent(ApplicationState.getAppContext(), CardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startBookmarksActivity(View view) {
        Intent intent = new Intent(ApplicationState.getAppContext(), BookmarksActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startRecommendationsActivity(View view) {
        Intent intent = new Intent(ApplicationState.getAppContext(), RecommendationsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startRecommendAnArticleActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), RecommendArticleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startSettingsActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), SettingsActivity.class);
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

    private void getBookmarkedContents(View view) {
        UserService userService = new UserService();
        HttpUtil.getInstance().getBookmarkedContents(Config.BOOKMARK_FIND_URL, new UserService().getBookmarkIds(), new BookmarkCallback(view));
    }

    private void getRecommendedContents(View view) {
        UserService userService = new UserService();
        HttpUtil.getInstance().getBookmarkedContents(Config.BOOKMARK_FIND_URL, new UserService().getBookmarkIds(), new RecommendationsCallback(view));
    }

    private void recommendSuccess(View view, String response) {
        try {
            Contents contents = new JsonParser().fromJson(response, Contents.class);
            SharedPreferencesService.getInstance().putString(Config.JSON_RECOMMENDED_CONTENTS, response);
            RecommendationsActivity.contents = contents;

            startRecommendationsActivity(view);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void bookmarkSuccess(View view, String response) {
        try {
            Contents contents = new JsonParser().fromJson(response, Contents.class);
            SharedPreferencesService.getInstance().putString(Config.JSON_BOOKMARKED_CONTENTS, response);
            BookmarksActivity.contents = contents;

            startBookmarksActivity(view);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void success(View view, String response) {
        System.out.println(response);
        try {
            Contents contents = new JsonParser().fromJson(response, Contents.class);
            SharedPreferencesService.getInstance().putString(Config.JSON_CONTENTS, response);
            CardActivity.contents = contents;

            startCardActivity(view);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private class RecommendationsCallback implements Callback {

        private View view;

        public RecommendationsCallback(View view) {
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
                        recommendSuccess(view, responseStr);
                    }
                });
            }
        }
    }

    private class BookmarkCallback implements Callback {

        private View view;
        public BookmarkCallback(View view) {
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
                        bookmarkSuccess(view, responseStr);
                    }
                });
            }
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
