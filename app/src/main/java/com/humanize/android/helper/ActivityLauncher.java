package com.humanize.android.helper;

import android.content.Intent;

import com.humanize.android.activity.AboutUsActivity;
import com.humanize.android.activity.BookmarksActivity;
import com.humanize.android.activity.CreateArticleActivity;
import com.humanize.android.activity.ForgotPasswordActivity;
import com.humanize.android.activity.HomeActivity;
import com.humanize.android.activity.ContactUsActivity;
import com.humanize.android.activity.HistoricPaperActivity;
import com.humanize.android.activity.InviteFriendActivity;
import com.humanize.android.activity.LoginActivity;
import com.humanize.android.activity.PaperActivity;
import com.humanize.android.activity.SignupActivity;
import com.humanize.android.activity.UpdateCategoriesActivity;
import com.humanize.android.activity.SubmitArticleActivity;
import com.humanize.android.activity.SingleCategoryContentActivity;
import com.humanize.android.activity.UpdatePaperTimeActivity;
import com.humanize.android.activity.UpvotedActivity;
import com.humanize.android.activity.UserProfileActivity;
import com.humanize.android.config.StringConstants;

/**
 * Created by kamal on 12/9/15.
 */
public class ActivityLauncher {

    public void startAboutUsActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), AboutUsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startUserProfileActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), UserProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startLoginActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startSignupActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), SignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startForgotPasswordActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), ForgotPasswordActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startUpdateCategoriesActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), UpdateCategoriesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startBookmarksActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), BookmarksActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startUpvotedActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), UpvotedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startUpdatePaperTimeActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), UpdatePaperTimeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startHomeActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startPaperActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), PaperActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startHistoricPaperActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), HistoricPaperActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startSubmitArticleActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), SubmitArticleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startCreateArticleActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), CreateArticleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startInviteFriendActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), InviteFriendActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startSingleCategoryContentActivity(String category) {
        Intent intent = new Intent(ApplicationState.getAppContext(), SingleCategoryContentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(StringConstants.CATEGORY, category);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startContactUsActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), ContactUsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }
}
