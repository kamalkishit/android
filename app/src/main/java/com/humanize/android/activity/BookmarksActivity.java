package com.humanize.android.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.humanize.android.data.Content;
import com.humanize.android.helper.ContentRecyclerViewAdapter;
import com.humanize.android.service.JsonParserService;
import com.humanize.android.R;
import com.humanize.android.service.GsonParserServiceImpl;

import com.humanize.android.config.StringConstants;
import com.humanize.android.data.Contents;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.config.Config;
import com.humanize.android.service.UserService;
import com.humanize.android.service.UserServiceImpl;

import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookmarksActivity extends AppCompatActivity {

    public static Contents contents = null;

    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;

    private JsonParserService jsonParserService;
    private UserService userService;
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
        userService = new UserServiceImpl();
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
                Contents contents = jsonParserService.fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_BOOKMARKED_CONTENTS), Contents.class);

                Iterator<Content> iterator = contents.getContents().iterator();
                while(iterator.hasNext()) {
                    Content content = iterator.next();
                    if (!userService.isBookmarked(content.getId())) {
                        iterator.remove();
                    }
                }

                if (contents.getContents().size() == 0) {
                    NoBookmarksFragment noBookmarksFragment = new NoBookmarksFragment();
                    noBookmarksFragment.show(BookmarksActivity.this.getFragmentManager(), "");
                }

                BookmarksActivity.contents = contents;
                contentRecyclerViewAdapter.setContents(BookmarksActivity.contents.getContents());
                contentRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                NoBookmarksFragment noBookmarksFragment = new NoBookmarksFragment();
                noBookmarksFragment.show(BookmarksActivity.this.getFragmentManager(), "");
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

    public static class NoBookmarksFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LinearLayout linearLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_no_bookmarks, null);
            Button submitButton = (Button) linearLayout.findViewById(R.id.submitButton);

            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismissFragment();
                }
            });

            builder.setView(linearLayout);
            return builder.create();
        }

        private void dismissFragment() {
            this.dismiss();
            (getActivity()).onBackPressed();
        }
    }
}