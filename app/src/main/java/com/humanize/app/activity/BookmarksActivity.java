package com.humanize.app.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.humanize.app.helper.ContentRecyclerViewAdapter;
import com.humanize.app.service.JsonParserService;
import com.humanize.app.R;
import com.humanize.app.service.GsonParserServiceImpl;

import com.humanize.app.config.StringConstants;
import com.humanize.app.data.Contents;
import com.humanize.app.service.SharedPreferencesService;
import com.humanize.app.config.Config;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookmarksActivity extends AppCompatActivity {

    public static Contents contents = null;

    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;

    private JsonParserService jsonParserService;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;

    private static String TAG = BookmarksActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        overridePendingTransition(R.anim.slide_right_to_left, 0);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
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
            overridePendingTransition(0, R.anim.slide_left_to_right);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_left_to_right);
    }

    private void initialize() {
        jsonParserService = new GsonParserServiceImpl();
        toolbarText.setText(StringConstants.BOOKMARKED_ARTICLES);

        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(this, null);
        recyclerView.setAdapter(contentRecyclerViewAdapter);

        try {
            if (SharedPreferencesService.getInstance().getString(Config.JSON_BOOKMARKED_CONTENTS) != null) {
                BookmarksActivity.contents = jsonParserService.fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_BOOKMARKED_CONTENTS), Contents.class);
                contentRecyclerViewAdapter.setContents(BookmarksActivity.contents.getContents());
                contentRecyclerViewAdapter.notifyDataSetChanged();
            }
        } catch (Exception exception) {

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
}