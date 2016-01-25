package com.humanize.app.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.humanize.app.service.ApiServiceImpl;
import com.humanize.app.helper.ContentRecyclerViewAdapter;
import com.humanize.app.service.GsonParserServiceImpl;
import com.humanize.app.service.JsonParserService;
import com.humanize.app.R;
import com.humanize.app.config.StringConstants;
import com.humanize.app.data.ContentParams;
import com.humanize.app.data.Contents;
import com.humanize.app.helper.ActivityLauncher;
import com.humanize.app.service.ApiService;

import java.io.IOException;
import java.util.StringTokenizer;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SingleContentActivity extends AppCompatActivity {

    public static Contents contents = null;

    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;
    @Bind(R.id.circularProgressBar) ProgressBar circularProgressBar;

    private JsonParserService jsonParserService;
    private ApiService apiService;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_content);
        overridePendingTransition(R.anim.slide_right_to_left, 0);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();

            overridePendingTransition(0, R.anim.slide_left_to_right);
            new ActivityLauncher().startCardActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(0, R.anim.slide_left_to_right);
        new ActivityLauncher().startCardActivity();
    }

    private void initialize() {
        jsonParserService = new GsonParserServiceImpl();
        apiService = new ApiServiceImpl();
        circularProgressBar.setVisibility(View.GONE);
        toolbarText.setText(getIntent().getStringExtra(StringConstants.CATEGORY));

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

        Uri uri = getIntent().getData();
        if (uri != null) {
            String path = uri.getPath();
            System.out.println(path);

            String delims = "/";
            StringTokenizer stringTokenizer = new StringTokenizer(path, delims);
            String urlId = "";
            while(stringTokenizer.hasMoreElements()) {
                urlId = (String) stringTokenizer.nextElement();
            }

            getContent(urlId);
        }
    }

    private void configureListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getContent(String urlId) {
        ContentParams contentParams = new ContentParams();
        contentParams.setContentId(urlId);
        circularProgressBar.setVisibility(View.VISIBLE);
        apiService.getContent(urlId, new ContentCallback());
    }

    private void success(View view, String response) {
        System.out.println(response);
        try {
            Contents contents = jsonParserService.fromJson(response, Contents.class);
            SingleContentActivity.contents = contents;
            contentRecyclerViewAdapter.setContents(SingleContentActivity.contents.getContents());
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
}
