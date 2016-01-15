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

import com.humanize.android.ApiImpl;
import com.humanize.android.ApiUrls;
import com.humanize.android.ContentRecyclerViewAdapter;
import com.humanize.android.JsonParser;
import com.humanize.android.R;
import com.humanize.android.common.Constants;
import com.humanize.android.common.StringConstants;
import com.humanize.android.data.ContentSearchParams;
import com.humanize.android.data.Contents;
import com.humanize.android.util.Api;
import com.humanize.android.util.JsonParserImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SingleCategoryContent extends AppCompatActivity {

    public static Contents contents = null;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText)
    TextView toolbarText;

    private JsonParser jsonParser;
    private Api api;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;

    private static String TAG = SingleCategoryContent.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_category_content);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        jsonParser = new JsonParserImpl();
        api = new ApiImpl();
        toolbarText.setText(getIntent().getStringExtra("Category"));

        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(this, null);
        recyclerView.setAdapter(contentRecyclerViewAdapter);

        getContent();
    }

    private void configureListeners() {
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
        if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getContent() {
        List<String> categories = new ArrayList<String>();
        categories.add(getIntent().getStringExtra("Category"));
        ContentSearchParams contentSearchParams = new ContentSearchParams();
        contentSearchParams.setCategories(categories);
        api.getContents(contentSearchParams, new ContentCallback());
    }

    private void getNewContent(long endDate) {
        List<String> categories = new ArrayList<String>();
        categories.add(getIntent().getStringExtra("Category"));
        ContentSearchParams contentSearchParams = new ContentSearchParams();
        contentSearchParams.setCategories(categories);
        contentSearchParams.setCreatedDate(endDate);
        contentSearchParams.setRefresh(true);
        api.getContents(contentSearchParams, new NewContentCallback());
    }

    private void getMoreContent(long startDate) {
        List<String> categories = new ArrayList<String>();
        categories.add(getIntent().getStringExtra("Category"));
        ContentSearchParams contentSearchParams = new ContentSearchParams();
        contentSearchParams.setCategories(categories);
        contentSearchParams.setCreatedDate(startDate);
        contentSearchParams.setRefresh(true);
        api.getContents(contentSearchParams, new MoreContentCallback());
    }

    private void success(View view, String response) {
        System.out.println(response);
        try {
            Contents contents = jsonParser.fromJson(response, Contents.class);
            SingleCategoryContent.contents = contents;
            contentRecyclerViewAdapter.setContents(SingleCategoryContent.contents.getContents());
            contentRecyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
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

    private class ContentCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException exception) {
            exception.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
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
                        Snackbar.make(recyclerView, StringConstants.FAILURE_STR, Snackbar.LENGTH_LONG).show();
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
