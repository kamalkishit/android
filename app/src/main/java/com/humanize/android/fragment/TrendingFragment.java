package com.humanize.android.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.humanize.android.R;
import com.humanize.android.config.Config;
import com.humanize.android.config.Constants;
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.ContentSearchParams;
import com.humanize.android.data.Contents;
import com.humanize.android.helper.ApplicationState;
import com.humanize.android.helper.ContentRecyclerViewAdapter;
import com.humanize.android.service.ApiService;
import com.humanize.android.service.ApiServiceImpl;
import com.humanize.android.service.GsonParserServiceImpl;
import com.humanize.android.service.JsonParserService;
import com.humanize.android.service.LogService;
import com.humanize.android.service.LogServiceImpl;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.utils.AppUtils;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by kamal on 2/18/16.
 */
public class TrendingFragment extends Fragment {

    public static Contents contents = null;

    @Bind(R.id.circularProgressBar) ProgressBar circularProgressBar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;

    private JsonParserService jsonParserService;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ApiService apiService;

    private static final String TAG = TrendingFragment.class.getName();
    private static final LogService logService = new LogServiceImpl();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);

        ButterKnife.bind(this, view);

        initialize();
        configureListeners();

        return view;
    }

    private void initialize() {
        jsonParserService = new GsonParserServiceImpl();
        apiService = new ApiServiceImpl();
        circularProgressBar.setVisibility(View.GONE);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(getActivity(), null);
        recyclerView.setAdapter(contentRecyclerViewAdapter);

        try {
            if (SharedPreferencesService.getInstance().getString(Config.JSON_TRENDING_CONTENTS) != null) {
                TrendingFragment.contents = jsonParserService.fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_TRENDING_CONTENTS), Contents.class);
                contentRecyclerViewAdapter.setContents(TrendingFragment.contents.getContents());
                contentRecyclerViewAdapter.notifyDataSetChanged();

                if (contentRecyclerViewAdapter.getContents() != null && contentRecyclerViewAdapter.getContents().size() > 0 && AppUtils.isNetworkAvailable()) {
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                            getTrends();
                        }
                    });
                }
            } else {
                if (AppUtils.isNetworkAvailable()) {
                    getTrends();
                } else {
                    NetworkConnectionFailureFragment networkConnectionFailureFragment = new NetworkConnectionFailureFragment();
                    networkConnectionFailureFragment.show(TrendingFragment.this.getActivity().getFragmentManager(), "");
                }
            }
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    private void configureListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppUtils.isNetworkAvailable()) {
                    getTrends();
                } else if (TrendingFragment.contents == null){
                    swipeRefreshLayout.setRefreshing(false);
                    NetworkConnectionFailureFragment networkConnectionFailureFragment = new NetworkConnectionFailureFragment();
                    networkConnectionFailureFragment.setFragment(TrendingFragment.this);
                    networkConnectionFailureFragment.show(TrendingFragment.this.getActivity().getFragmentManager(), "");
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void getTrends() {
        circularProgressBar.setVisibility(View.VISIBLE);
        ContentSearchParams contentSearchParams = new ContentSearchParams();
        contentSearchParams.setCategories(ApplicationState.getUser().getCategories());
        apiService.getTrends(new ContentCallback());
    }

    private void success(View view, String response) {
        try {
            Contents contents = jsonParserService.fromJson(response, Contents.class);
            SharedPreferencesService.getInstance().putString(Config.JSON_TRENDING_CONTENTS, response);
            TrendingFragment.contents = contents;
            contentRecyclerViewAdapter.setContents(TrendingFragment.contents.getContents());
            contentRecyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    private class ContentCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException exception) {
            logService.e(TAG, exception.getMessage());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    circularProgressBar.setVisibility(View.GONE);
                }
            });

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    circularProgressBar.setVisibility(View.GONE);
                    //Snackbar.make(recyclerView, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    circularProgressBar.setVisibility(View.GONE);
                }
            });

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

        private TrendingFragment fragment;

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

        public void setFragment(TrendingFragment fragment) {
            this.fragment = fragment;
        }

        private void dismissFragment() {
            this.dismiss();
        }

        private void retry() {
            this.dismiss();
            this.fragment.getTrends();
        }
    }
}
