package com.humanize.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.humanize.android.ContentRecyclerViewAdapter;
import com.humanize.android.JsonParser;
import com.humanize.android.R;
import com.humanize.android.UserService;
import com.humanize.android.common.StringConstants;
import com.humanize.android.content.data.Content;
import com.humanize.android.content.data.Contents;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecommendationsActivity extends AppCompatActivity {

    public static Contents contents = null;

    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;

    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        toolbarText.setText(StringConstants.RECOMMENDED);

        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        try {
            if (SharedPreferencesService.getInstance().getString(Config.JSON_RECOMMENDED_CONTENTS) != null) {
                RecommendationsActivity.contents = new JsonParser().fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_RECOMMENDED_CONTENTS), Contents.class);
                contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(contents.getContents());
                recyclerView.setAdapter(contentRecyclerViewAdapter);
            } else {
                HttpUtil.getInstance().getRecommendedContents(Config.BOOKMARK_FIND_URL, new UserService().getBookmarkIds(), new RecommendationsCallback());
            }
        } catch (Exception exception) {

        }
    }

    private void configureListeners() {

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
        if (id == R.id.actionRefresh) {
            //refresh();
        } else if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void recommendSuccess(String response) {
        try {
            Contents contents = new JsonParser().fromJson(response, Contents.class);
            SharedPreferencesService.getInstance().putString(Config.JSON_RECOMMENDED_CONTENTS, response);
            RecommendationsActivity.contents = contents;

            contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(contents.getContents());
            recyclerView.setAdapter(contentRecyclerViewAdapter);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private class RecommendationsCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            exception.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
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
                        Snackbar snackbar = Snackbar.make(recyclerView, StringConstants.FAILURE_STR, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        recommendSuccess(responseStr);
                    }
                });
            }
        }
    }
}

