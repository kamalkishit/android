package com.humanize.android.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.humanize.android.ContentRecyclerViewAdapter;
import com.humanize.android.JsonParser;
import com.humanize.android.R;
import com.humanize.android.UserService;
import com.humanize.android.common.Constants;
import com.humanize.android.common.StringConstants;
import com.humanize.android.content.data.Contents;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookmarksActivity extends AppCompatActivity {

    public static Contents contents = null;

    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;

    UserService userService;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;

    private static String TAG = BookmarksActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        userService = new UserService();
        toolbarText.setText(StringConstants.BOOKMARKED);

        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(null);
        recyclerView.setAdapter(contentRecyclerViewAdapter);

        try {
            if (SharedPreferencesService.getInstance().getString(Config.JSON_BOOKMARKED_CONTENTS) != null) {
                BookmarksActivity.contents = new JsonParser().fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_BOOKMARKED_CONTENTS), Contents.class);
                contentRecyclerViewAdapter.setContents(BookmarksActivity.contents.getContents());
                contentRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                HttpUtil.getInstance().getBookmarkedContents(Config.BOOKMARK_FIND_URL, new UserService().getBookmarkIds(), new BookmarkCallback());
            }
        } catch (Exception exception) {

        }
    }

    private void configureListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (contentRecyclerViewAdapter.getContents() != null && contentRecyclerViewAdapter.getContents().size() > 0) {
                    getNewBookmarks();
                } else {
                    getBookmarks();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bookmarks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionRefresh) {
            //refresh();
        } else if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getBookmarks() {
        HttpUtil.getInstance().getRecommendedContents(null, userService.getRecommendationsIds(), new BookmarkCallback());
    }

    private void getNewBookmarks() {
        HttpUtil.getInstance().refreshRecommendations(new BookmarkCallback());
    }

    private void bookmarkSuccess(View view, String response) {
        try {
            Contents contents = new JsonParser().fromJson(response, Contents.class);
            SharedPreferencesService.getInstance().putString(Config.JSON_BOOKMARKED_CONTENTS, response);
            BookmarksActivity.contents = contents;
            contentRecyclerViewAdapter.setContents(BookmarksActivity.contents.getContents());
            contentRecyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private class BookmarkCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            exception.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(recyclerView, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(recyclerView, StringConstants.FAILURE_STR, Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        bookmarkSuccess(recyclerView, responseStr);
                    }
                });
            }
        }
    }

    private class NewBookmarkCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            exception.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Contents contents = new JsonParser().fromJson(responseStr, Contents.class);

                            if (contents != null && contents.getContents().size() >= Constants.DEFAULT_CONTENTS_SIZE) {
                                contentRecyclerViewAdapter.setContents(contents.getContents());
                            } else if (contents != null) {
                                contentRecyclerViewAdapter.getContents().addAll(0, contents.getContents());
                            }

                            contentRecyclerViewAdapter.notifyDataSetChanged();

                            try {
                                SharedPreferencesService.getInstance().putString(Config.JSON_BOOKMARKED_CONTENTS, new JsonParser().toJson(new Contents(contentRecyclerViewAdapter.getContents())));
                            } catch (Exception exception) {
                                Log.e(TAG, exception.toString());
                            }
                        } catch (Exception exception) {
                            Log.e(TAG, exception.toString());
                        } finally {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        }
    }

    private class MoreBookmarkCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            exception.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Contents contents = new JsonParser().fromJson(responseStr, Contents.class);

                            if (contents != null && contents.getContents().size() >= Constants.DEFAULT_CONTENTS_SIZE) {
                                contentRecyclerViewAdapter.setContents(contents.getContents());
                            } else if (contents != null) {
                                contentRecyclerViewAdapter.getContents().addAll(0, contents.getContents());
                            }

                            contentRecyclerViewAdapter.notifyDataSetChanged();

                            try {
                                SharedPreferencesService.getInstance().putString(Config.JSON_BOOKMARKED_CONTENTS, new JsonParser().toJson(new Contents(contentRecyclerViewAdapter.getContents())));
                            } catch (Exception exception) {
                                Log.e(TAG, exception.toString());
                            }
                        } catch (Exception exception) {
                            Log.e(TAG, exception.toString());
                        } finally {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        }
    }
}