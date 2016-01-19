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
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.humanize.android.ApiImpl;
import com.humanize.android.ContentRecyclerViewAdapter;
import com.humanize.android.data.ContentSearchParams;
import com.humanize.android.NavigationDrawerFragment;
import com.humanize.android.JsonParser;
import com.humanize.android.R;
import com.humanize.android.util.Api;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.GsonParserImpl;
import com.humanize.android.common.Constants;
import com.humanize.android.common.StringConstants;
import com.humanize.android.data.Contents;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.util.Config;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CardActivity extends AppCompatActivity {

    public static Contents contents = null;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;
    @Bind(R.id.circularProgressBar) ProgressBar circularProgressBar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.drawerLayout) DrawerLayout drawerLayout;

    private JsonParser jsonParser;
    private NavigationDrawerFragment navigationDrawerFragment;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean doubleBackToExitPressedOnce;
    private Api api;

    private static String TAG = CardActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        //overridePendingTransition(R.anim.slide_right_to_left, R.anim.slide_right_to_left);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    @Override
    public void onBackPressed() {
        drawerLayout.closeDrawer(Gravity.LEFT);

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            //overridePendingTransition(0, R.anim.slide_left_to_right);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Snackbar.make(coordinatorLayout, StringConstants.DOUBLE_BACK_EXIT_STR, Snackbar.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, Constants.DOUBLE_EXIT_DELAY_TIME);
    }

    private void initialize() {
        jsonParser = new GsonParserImpl();
        api = new ApiImpl();
        circularProgressBar.setVisibility(View.GONE);
        doubleBackToExitPressedOnce = false;

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        toolbar.setCollapsible(true);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavigationDrawer);
        navigationDrawerFragment.getView().getLayoutParams().width = Config.NAV_DRAWER_WIDTH;
        navigationDrawerFragment.setUp(R.id.fragmentNavigationDrawer, drawerLayout, toolbar);
        navigationDrawerFragment.setActivity(this);

        contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(this, null);
        recyclerView.setAdapter(contentRecyclerViewAdapter);

        try {
            if (SharedPreferencesService.getInstance().getString(Config.JSON_CONTENTS) != null) {
                CardActivity.contents = jsonParser.fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_CONTENTS), Contents.class);
                contentRecyclerViewAdapter.setContents(CardActivity.contents.getContents());
                contentRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                getContent();
            }
        } catch (Exception exception) {

        }
    }

    private void configureListeners() {
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("hi");
                contentRecyclerViewAdapter.resetIndex();
                contentRecyclerViewAdapter.notifyDataSetChanged();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (contentRecyclerViewAdapter.getContents() != null && contentRecyclerViewAdapter.getContents().size() > 0) {
                    getNewContent(contentRecyclerViewAdapter.getContents().get(0).getCreatedDate());
                } else {
                    getContent();
                }
            }
        });

        recyclerView.setOnScrollListener(new EndlessRecyclerViewOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (contentRecyclerViewAdapter.getContents().size() > 0) {
                    getMoreContent(contentRecyclerViewAdapter.getContents().get(contentRecyclerViewAdapter.getContents().size() - 1).getCreatedDate());
                }
            }
        });
    }

    public void openNavigationDrawer(MenuItem menuItem) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    private void getContent() {
        circularProgressBar.setVisibility(View.VISIBLE);
        ContentSearchParams contentSearchParams = new ContentSearchParams();
        contentSearchParams.setCategories(ApplicationState.getUser().getCategories());
        api.getContents(contentSearchParams, new ContentCallback());
    }

    private void getNewContent(long endDate) {
        ContentSearchParams contentSearchParams = new ContentSearchParams();
        contentSearchParams.setCategories(ApplicationState.getUser().getCategories());
        contentSearchParams.setCreatedDate(endDate);
        contentSearchParams.setRefresh(true);
        api.getContents(contentSearchParams, new NewContentCallback());
    }

    private void getMoreContent(long startDate) {
        ContentSearchParams contentSearchParams = new ContentSearchParams();
        contentSearchParams.setCategories(ApplicationState.getUser().getCategories());
        contentSearchParams.setCreatedDate(startDate);
        api.getContents(contentSearchParams, new MoreContentCallback());
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
            Contents contents = jsonParser.fromJson(response, Contents.class);
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
        public void onFailure(Call call, IOException exception) {
            exception.printStackTrace();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    circularProgressBar.setVisibility(View.GONE);
                    Snackbar.make(recyclerView, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {

            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        circularProgressBar.setVisibility(View.GONE);
                        Snackbar.make(recyclerView, StringConstants.FAILURE_STR, Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        circularProgressBar.setVisibility(View.GONE);
                        success(recyclerView, responseStr);
                    }
                });
            }
        }
    }

    private class MoreContentCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException exception) {
            Log.e(TAG, exception.toString());
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            if (!response.isSuccessful()) {

            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Contents contents = jsonParser.fromJson(responseStr, Contents.class);

                            if (contents != null) {
                                contentRecyclerViewAdapter.getContents().addAll(contents.getContents());
                                contentRecyclerViewAdapter.notifyDataSetChanged();
                                try {
                                    SharedPreferencesService.getInstance().putString(Config.JSON_CONTENTS, jsonParser.toJson(new Contents(contentRecyclerViewAdapter.getContents())));
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
        public void onFailure(Call call, IOException exception) {
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            final String responseStr = response.body().string().toString();
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            //ServerException serverException = jsonParser.fromJson(responseStr, ServerException.class);
                            //System.out.println(serverException);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Contents contents = jsonParser.fromJson(responseStr, Contents.class);

                            if (contents != null && contents.getContents().size() >= Constants.DEFAULT_CONTENTS_SIZE) {
                                contentRecyclerViewAdapter.setContents(contents.getContents());
                            } else if (contents != null) {
                                contentRecyclerViewAdapter.getContents().addAll(0, contents.getContents());
                            }

                            contentRecyclerViewAdapter.notifyDataSetChanged();

                            SharedPreferencesService.getInstance().putString(Config.JSON_CONTENTS, jsonParser.toJson(new Contents(contentRecyclerViewAdapter.getContents())));
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
