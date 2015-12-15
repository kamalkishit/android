package com.humanize.android.helper;

import android.content.Intent;

import com.humanize.android.NewLoginActivity;
import com.humanize.android.activity.BookmarksActivity;
import com.humanize.android.activity.CardActivity;
import com.humanize.android.activity.LikesActivity;
import com.humanize.android.activity.PaperActivity;
import com.humanize.android.util.ApplicationState;

/**
 * Created by kamal on 12/9/15.
 */
public class ActivityLauncher {

    public static void startCardActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), CardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public static void startPaperActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), PaperActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public static void startRecommendedActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), LikesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public static void startBookmarksActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), BookmarksActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }

    public static void startNewLoginActivity() {
        Intent intent = new Intent(ApplicationState.getAppContext(), NewLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationState.getAppContext().startActivity(intent);
    }



}
