package com.humanize.android.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.humanize.android.config.Config;
import com.humanize.android.service.ApiServiceImpl;
import com.humanize.android.helper.ContentRecyclerViewAdapter;
import com.humanize.android.service.ContentService;
import com.humanize.android.service.ContentServiceImpl;
import com.humanize.android.service.GsonParserServiceImpl;
import com.humanize.android.service.JsonParserService;
import com.humanize.android.R;
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.ContentParams;
import com.humanize.android.data.Contents;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.service.ApiService;
import com.humanize.android.service.LogService;
import com.humanize.android.service.LogServiceImpl;

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
    private ContentService contentService;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;

    private static final String TAG = SingleContentActivity.class.getSimpleName();
    private static final LogService logService = new LogServiceImpl();
    private String urlId;

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
            new ActivityLauncher().startHomeActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(0, R.anim.slide_left_to_right);
        new ActivityLauncher().startHomeActivity();
    }

    private void initialize() {
        jsonParserService = new GsonParserServiceImpl();
        apiService = new ApiServiceImpl();
        contentService = new ContentServiceImpl();
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

            String delims = "/";
            StringTokenizer stringTokenizer = new StringTokenizer(path, delims);
            urlId = "";
            while(stringTokenizer.hasMoreElements()) {
                urlId = (String) stringTokenizer.nextElement();
            }

            getContent(urlId);
        } else {
            urlId = getIntent().getStringExtra(Config.URL);
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
        try {
            Contents contents = jsonParserService.fromJson(response, Contents.class);
            SingleContentActivity.contents = contents;
            contentRecyclerViewAdapter.setContents(SingleContentActivity.contents.getContents());
            contentService.incrViewedCount(contents.getContents().get(0));
            contentRecyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    private class ContentCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException exception) {
            logService.e(TAG, exception.getMessage());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    circularProgressBar.setVisibility(View.GONE);
                    //Snackbar.make(recyclerView, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                    NetworkConnectionFailureFragment networkConnectionFailureFragment = new NetworkConnectionFailureFragment();
                    networkConnectionFailureFragment.setUrlId(urlId);
                    networkConnectionFailureFragment.show(SingleContentActivity.this.getFragmentManager(), "");
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

    public static class NetworkConnectionFailureFragment extends DialogFragment {

        private String urlId;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LinearLayout linearLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_network_connection_failure, null);
            Button cancelButton = (Button) linearLayout.findViewById(R.id.cancelButton);
            Button retryButton = (Button) linearLayout.findViewById(R.id.retryButton);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismissFragment();
                }
            });

            retryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retry();
                }
            });

            builder.setView(linearLayout);
            return builder.create();
        }

        public String getUrlId() {
            return urlId;
        }

        public void setUrlId(String urlId) {
            this.urlId = urlId;
        }

        private void dismissFragment() {
            this.dismiss();
        }

        private void retry() {
            this.dismiss();
            ((SingleContentActivity)getActivity()).getContent(urlId);
        }
    }
}
