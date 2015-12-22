package com.humanize.android.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.humanize.android.ContentRecyclerViewAdapter;
import com.humanize.android.R;
import com.humanize.android.common.StringConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookmarksActivity extends AppCompatActivity {

    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;

    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        toolbarText.setText(StringConstants.BOOKMARKED);

        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(null);
        recyclerView.setAdapter(contentRecyclerViewAdapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void configureListeners() {

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
}