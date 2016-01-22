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

import com.humanize.android.R;
import com.humanize.android.config.Config;
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.Contents;
import com.humanize.android.data.PaperParams;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.helper.ContentRecyclerViewAdapter;
import com.humanize.android.service.ApiService;
import com.humanize.android.service.ApiServiceImpl;
import com.humanize.android.service.GsonParserServiceImpl;
import com.humanize.android.service.JsonParserService;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.service.UserService;
import com.humanize.android.service.UserServiceImpl;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PaperActivity extends AppCompatActivity {

    public static Contents contents = null;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;
    @Bind(R.id.circularProgressBar) ProgressBar circularProgressBar;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    private UserService userService;
    private JsonParserService jsonParser;
    private ApiService apiService;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;

    private static String TAG = PaperActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper);
        overridePendingTransition(R.anim.slide_right_to_left, 0);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        userService = new UserServiceImpl();
        jsonParser = new GsonParserServiceImpl();
        apiService = new ApiServiceImpl();
        toolbarText.setText(StringConstants.PAPER);

        circularProgressBar.setVisibility(View.GONE);

        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(this, null);
        recyclerView.setAdapter(contentRecyclerViewAdapter);

        getPaper();
    }

    private void configureListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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

    private void getPaper() {
        circularProgressBar.setVisibility(View.VISIBLE);
        PaperParams paperParams = new PaperParams();
        paperParams.setDate(getCurrentDate());
        apiService.getPaper(paperParams, new PaperCallback());
    }

    private String getCurrentDate() {
        return null;
    }

    private void success(View view, String response) {
        System.out.println(response);
        try {
            Contents contents = jsonParser.fromJson(response, Contents.class);
            SharedPreferencesService.getInstance().putString(Config.JSON_PAPER, response);
            SharedPreferencesService.getInstance().putString(Config.LATEST_DATE, getCurrentDate());
            PaperActivity.contents = contents;
            contentRecyclerViewAdapter.setContents(PaperActivity.contents.getContents());
            contentRecyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private class PaperCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException exception) {
            exception.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    circularProgressBar.setVisibility(View.GONE);
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
                        circularProgressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(recyclerView, StringConstants.FAILURE_STR, Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        circularProgressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        success(recyclerView, responseStr);
                    }
                });
            }
        }
    }
}
