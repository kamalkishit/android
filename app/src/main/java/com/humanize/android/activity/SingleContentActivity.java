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
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.humanize.android.service.ApiServiceImpl;
import com.humanize.android.helper.ContentRecyclerViewAdapter;
import com.humanize.android.service.GsonParserServiceImpl;
import com.humanize.android.service.JsonParserService;
import com.humanize.android.R;
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.ContentParams;
import com.humanize.android.data.Contents;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.service.ApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        //getContent();
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

        getContent();
    }

    private void getContent() {
        List<String> categories = new ArrayList<String>();
        categories.add(getIntent().getStringExtra(StringConstants.CATEGORY));
        ContentParams contentParams = new ContentParams();
        contentParams.setContentId("Know-The-Hero:-An-IAS-Officer-Who-Laid-His-Life-Fighting-Against-Oil-Mafia-1452985167862");
        circularProgressBar.setVisibility(View.VISIBLE);
        apiService.getContent(contentParams, new ContentCallback());
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
