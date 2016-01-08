package com.humanize.android.helper;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.humanize.android.JsonParser;
import com.humanize.android.activity.AboutUsActivity;
import com.humanize.android.activity.AppLauncherActivity;
import com.humanize.android.activity.BookmarksActivity;
import com.humanize.android.activity.CardActivity;
import com.humanize.android.activity.ContactUsActivity;
import com.humanize.android.activity.ForgotPasswordActivity;
import com.humanize.android.activity.InviteFriendActivity;
import com.humanize.android.activity.LoginActivity;
import com.humanize.android.activity.PaperActivity;
import com.humanize.android.activity.PrivacyActivity;
import com.humanize.android.activity.RecommendArticleActivity;
import com.humanize.android.activity.RecommendationsActivity;
import com.humanize.android.activity.ResetPasswordActivity;
import com.humanize.android.activity.SelectCategoriesActivity;
import com.humanize.android.activity.SettingsActivity;
import com.humanize.android.activity.SignupActivity;
import com.humanize.android.activity.SingleCategoryContent;
import com.humanize.android.activity.UsageActivity;
import com.humanize.android.util.ApplicationState;

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

    public void startCardActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), CardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startBookmarksActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), BookmarksActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startRecommendationsActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), RecommendationsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startRecommendAnArticleActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), RecommendArticleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startInviteFriendActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), InviteFriendActivity.class);
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

    public void startSingleCategoryContentActivity(String category) {
        Intent intent = new Intent(ApplicationState.getAppContext(), SingleCategoryContent.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Category", category);
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
}
