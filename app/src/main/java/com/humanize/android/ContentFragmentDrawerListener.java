package com.humanize.android;

import android.content.Intent;
import android.view.View;

import com.google.gson.Gson;
import com.humanize.android.activity.BookmarksActivity;
import com.humanize.android.activity.LikesActivity;
import com.humanize.android.activity.PaperActivity;
import com.humanize.android.activity.PaperLauncherActivity;
import com.humanize.android.content.data.Contents;
import com.humanize.android.service.BookmarkService;
import com.humanize.android.service.LikeService;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.humanize.android.service.SharedPreferencesService;

/**
 * Created by Kamal on 8/23/15.
 */
public class ContentFragmentDrawerListener implements  FragmentDrawer.FragmentDrawerListener {
    public void onDrawerItemSelected(View view, int position) {

        HttpUtil httpUtil = HttpUtil.getInstance();
        Intent intent = new Intent();
        switch(position){
            case 1:
                if (SharedPreferencesService.getInstance().getString(Config.JSON_PAPER) != null) {
                    Contents contents = new Gson().fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_PAPER), Contents.class);
                    PaperActivity.PaperAdapter.contents = contents.getContents();
                    intent = new Intent(ApplicationState.getAppContext(), PaperActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ApplicationState.getAppContext().startActivity(intent);
                } else {
                    intent = new Intent(ApplicationState.getAppContext(), PaperLauncherActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ApplicationState.getAppContext().startActivity(intent);
                }

                break;

            case 2:
                BookmarksActivity.BookmarksAdapter.contents = BookmarkService.getInstance().getBookmarks().getContents();
                intent = new Intent(ApplicationState.getAppContext(), BookmarksActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ApplicationState.getAppContext().startActivity(intent);
                break;

            case 3:
                LikesActivity.LikesAdapter.contents = LikeService.getInstance().getLikes().getContents();
                intent = new Intent(ApplicationState.getAppContext(), LikesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ApplicationState.getAppContext().startActivity(intent);
                break;
        }
    }
}
