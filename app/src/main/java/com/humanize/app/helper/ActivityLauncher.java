package com.humanize.app.helper;

import android.content.Intent;

import com.humanize.app.activity.AboutUsActivity;
import com.humanize.app.activity.BookmarksActivity;
import com.humanize.app.activity.CardActivity;
import com.humanize.app.activity.ContactUsActivity;
import com.humanize.app.activity.HistoricPaperActivity;
import com.humanize.app.activity.InviteFriendActivity;
import com.humanize.app.activity.PaperActivity;
import com.humanize.app.activity.SelectCategoriesActivity;
import com.humanize.app.activity.SubmitArticleActivity;
import com.humanize.app.activity.SingleCategoryContentActivity;
import com.humanize.app.activity.UpdatePaperTimeActivity;
import com.humanize.app.config.StringConstants;

/**
 * Created by kamal on 12/9/15.
 */
public class ActivityLauncher {

    public void startAboutUsActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), AboutUsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startUpdateCategoriesActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), SelectCategoriesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startBookmarksActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), BookmarksActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startUpdatePaperTimeActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), UpdatePaperTimeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public void startCardActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), CardActivity.class);
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

    public void startSuggestArticleActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), SubmitArticleActivity.class);
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
