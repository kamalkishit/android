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
import android.widget.TextView;

import com.humanize.android.ContentRecyclerViewAdapter;
import com.humanize.android.JsonParser;
import com.humanize.android.R;
import com.humanize.android.service.JsonParserImpl;
import com.humanize.android.service.UserService;
import com.humanize.android.common.Constants;
import com.humanize.android.common.StringConstants;
import com.humanize.android.data.Contents;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.service.UserServiceImpl;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecommendationsActivity extends AppCompatActivity {

    public static Contents contents = null;

    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;

    private UserService userService;
    private JsonParser jsonParser;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;

    private static String TAG = RecommendationsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        userService = new UserServiceImpl();
        jsonParser = new JsonParserImpl();
        toolbarText.setText(StringConstants.RECOMMENDED_ARTICLES);

        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(null);
        recyclerView.setAdapter(contentRecyclerViewAdapter);

        try {
            if (SharedPreferencesService.getInstance().getString(Config.JSON_RECOMMENDED_CONTENTS) != null) {
                RecommendationsActivity.contents = jsonParser.fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_RECOMMENDED_CONTENTS), Contents.class);
                contentRecyclerViewAdapter.setContents(RecommendationsActivity.contents.getContents());
                contentRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                getRecommendations();
            }
        } catch (Exception exception) {

        }
    }

    private void configureListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (contentRecyclerViewAdapter.getContents() != null && contentRecyclerViewAdapter.getContents().size() > 0) {
                    getNewRecommendations();
                } else {
                    getRecommendations();
                }
            }
        });
    }

    private void getRecommendations() {
        if (userService.getRecommendationsIds() != null && userService.getRecommendationsIds().size() > 0) {
            HttpUtil.getInstance().getRecommendedContents(Config.RECOMMENDATIONS_FIND_URL, userService.getRecommendationsIds(), new RecommendationsCallback());
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void getNewRecommendations() {
        if (userService.getNewRecommendationsIds(contentRecyclerViewAdapter.getContents().get(0).getId()) != null
            && userService.getNewRecommendationsIds(contentRecyclerViewAdapter.getContents().get(0).getId()).size() > 0) {
            HttpUtil.getInstance().getBookmarkedContents(Config.RECOMMENDATIONS_FIND_URL, userService.getNewRecommendationsIds(contentRecyclerViewAdapter.getContents().get(0).getId()), new NewRecommendationsCallback());
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void getMoreRecommendations() {
        if (userService.getMoreRecommendationsIds(contentRecyclerViewAdapter.getContents().get(contentRecyclerViewAdapter.getContents().size() -1).getId()) != null
            && userService.getMoreRecommendationsIds(contentRecyclerViewAdapter.getContents().get(contentRecyclerViewAdapter.getContents().size() - 1).getId()).size() > 0) {
            HttpUtil.getInstance().getBookmarkedContents(Config.RECOMMENDATIONS_FIND_URL, userService.getMoreRecommendationsIds(contentRecyclerViewAdapter.getContents().get(contentRecyclerViewAdapter.getContents().size() - 1).getId()), new MoreRecommendationsCallback());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recommendations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void recommendSuccess(String response) {
        try {
            Contents contents = jsonParser.fromJson(response, Contents.class);
            SharedPreferencesService.getInstance().putString(Config.JSON_RECOMMENDED_CONTENTS, response);
            RecommendationsActivity.contents = contents;

            contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(contents.getContents());
            recyclerView.setAdapter(contentRecyclerViewAdapter);
            contentRecyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private class NewRecommendationsCallback implements Callback {

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
                            Contents contents = jsonParser.fromJson(responseStr, Contents.class);

                            if (contents != null && contents.getContents().size() >= Constants.DEFAULT_CONTENTS_SIZE) {
                                contentRecyclerViewAdapter.setContents(contents.getContents());
                            } else if (contents != null) {
                                contentRecyclerViewAdapter.getContents().addAll(0, contents.getContents());
                            }

                            contentRecyclerViewAdapter.notifyDataSetChanged();

                            try {
                                SharedPreferencesService.getInstance().putString(Config.JSON_RECOMMENDED_CONTENTS, jsonParser.toJson(new Contents(contentRecyclerViewAdapter.getContents())));
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

    private class MoreRecommendationsCallback implements Callback {

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
                            Contents contents = jsonParser.fromJson(responseStr, Contents.class);

                            if (contents != null && contents.getContents().size() >= Constants.DEFAULT_CONTENTS_SIZE) {
                                contentRecyclerViewAdapter.setContents(contents.getContents());
                            } else if (contents != null) {
                                contentRecyclerViewAdapter.getContents().addAll(0, contents.getContents());
                            }

                            contentRecyclerViewAdapter.notifyDataSetChanged();

                            try {
                                SharedPreferencesService.getInstance().putString(Config.JSON_RECOMMENDED_CONTENTS, jsonParser.toJson(new Contents(contentRecyclerViewAdapter.getContents())));
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

    private class RecommendationsCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            exception.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(recyclerView, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
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
                        Snackbar.make(recyclerView, StringConstants.FAILURE_STR, Snackbar.LENGTH_LONG).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        recommendSuccess(responseStr);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }
    }
}

