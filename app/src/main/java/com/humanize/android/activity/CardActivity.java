package com.humanize.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.humanize.android.ContentRecyclerViewAdapter;
import com.humanize.android.ContentService;
import com.humanize.android.FragmentDrawer;
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

public class CardActivity extends AppCompatActivity {

    public static Contents contents = null;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.drawerLayout) DrawerLayout drawerLayout;

    private ContentService contentService;
    private UserService userService;
    private FragmentDrawer fragmentDrawer;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean doubleBackToExitPressedOnce;

    private static String TAG = CardActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    @Override
    public void onBackPressed() {
        drawerLayout.closeDrawer(Gravity.LEFT);

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.DOUBLE_BACK_EXIT_STR, Snackbar.LENGTH_SHORT);
        snackbar.show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, Constants.DOUBLE_EXIT_DELAY_TIME);
    }

    private void initialize() {
        contentService = new ContentService();
        userService = new UserService();
        doubleBackToExitPressedOnce = false;
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        toolbar.setCollapsible(true);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fragmentDrawer = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragmentNavigationDrawer);
        fragmentDrawer.setUp(R.id.fragmentNavigationDrawer, drawerLayout, toolbar);

        contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(null);
        recyclerView.setAdapter(contentRecyclerViewAdapter);

        try {
            if (SharedPreferencesService.getInstance().getString(Config.JSON_CONTENTS) != null) {
                CardActivity.contents = new JsonParser().fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_CONTENTS), Contents.class);
                contentRecyclerViewAdapter.setContents(CardActivity.contents.getContents());
                contentRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                getContent();
            }
        } catch (Exception exception) {

        }
    }

    private void configureListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (contentRecyclerViewAdapter.getContents() != null && contentRecyclerViewAdapter.getContents().size() > 0) {
                    getNewContent(Long.toString(contentRecyclerViewAdapter.getContents().get(0).getCreatedDate()));
                } else {
                    getContent();
                }
            }
        });

        recyclerView.setOnScrollListener(new EndlessRecyclerViewOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (contentRecyclerViewAdapter.getContents().size() > 0) {
                    getMoreContent(Long.toString(contentRecyclerViewAdapter.getContents().get(contentRecyclerViewAdapter.getContents().size() - 1).getCreatedDate()));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionSettings) {
            return true;
        } else if (id == R.id.actionCreate) {
            Intent intent = new Intent(getApplicationContext(), SubmitActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void openNavigationDrawer(MenuItem menuItem) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    private void getContent() {
        HttpUtil.getInstance().getContents(new ContentCallback());
    }

    private void getNewContent(String endDate) {
        HttpUtil.getInstance().refreshContents(endDate, new NewContentCallback());
    }

    private void getMoreContent(String startDate) {
        HttpUtil.getInstance().getMoreContents(startDate, new MoreContentCallback());
    }

    public abstract class EndlessRecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {

        int firstVisibleItem, visibleItemCount, totalItemCount;
        private int previousTotal = 0; // The total number of items in the dataset after the last load
        private boolean loading = true; // True if we are still waiting for the last set of data to load.
        private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
        private int current_page = 1;

        private LinearLayoutManager mLinearLayoutManager;

        public EndlessRecyclerViewOnScrollListener(LinearLayoutManager linearLayoutManager) {
            this.mLinearLayoutManager = linearLayoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                current_page++;

                onLoadMore(current_page);

                loading = true;
            }
        }

        public abstract void onLoadMore(int current_page);
    }

    private void success(View view, String response) {
        System.out.println(response);
        try {
            Contents contents = new JsonParser().fromJson(response, Contents.class);
            SharedPreferencesService.getInstance().putString(Config.JSON_CONTENTS, response);
            CardActivity.contents = contents;
            contentRecyclerViewAdapter.setContents(CardActivity.contents.getContents());
            contentRecyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private class ContentCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            exception.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    Snackbar snackbar = Snackbar.make(recyclerView, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG);
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
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar snackbar = Snackbar.make(recyclerView, StringConstants.FAILURE_STR, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        success(recyclerView, responseStr);
                    }
                });
            }
        }
    }

    private class MoreContentCallback implements Callback {
        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {

            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Contents contents = new JsonParser().fromJson(responseStr, Contents.class);

                            if (contents != null) {
                                contentRecyclerViewAdapter.getContents().addAll(contents.getContents());
                                contentRecyclerViewAdapter.notifyDataSetChanged();
                                try {
                                    SharedPreferencesService.getInstance().putString(Config.JSON_CONTENTS, new JsonParser().toJson(new Contents(contentRecyclerViewAdapter.getContents())));
                                } catch (Exception exception) {
                                    Log.e(TAG, exception.toString());
                                }
                            }
                        } catch (Exception exception) {
                            Log.e(TAG, exception.toString());
                        }
                    }
                });
            }
        }
    }

    private class NewContentCallback implements Callback {
        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
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
                                SharedPreferencesService.getInstance().putString(Config.JSON_CONTENTS, new JsonParser().toJson(new Contents(contentRecyclerViewAdapter.getContents())));
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
