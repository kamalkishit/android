package com.humanize.android.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
public class HomeFragment extends Fragment {

    public static Contents contents = null;

    @Bind(R.id.circularProgressBar) ProgressBar circularProgressBar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;

    private JsonParserService jsonParserService;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ApiService apiService;

    private static final String TAG = HomeFragment.class.getName();
    private static final LogService logService = new LogServiceImpl();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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
            if (SharedPreferencesService.getInstance().getString(Config.JSON_HOME_CONTENTS) != null) {
                HomeFragment.contents = jsonParserService.fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_HOME_CONTENTS), Contents.class);
                contentRecyclerViewAdapter.setContents(HomeFragment.contents.getContents());
                contentRecyclerViewAdapter.notifyDataSetChanged();

                if (contentRecyclerViewAdapter.getContents() != null && contentRecyclerViewAdapter.getContents().size() > 0 && AppUtils.isNetworkAvailable()) {
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                            getNewContent(contentRecyclerViewAdapter.getContents().get(0).getCreatedDate());
                        }
                    });
                }
            } else {
                getContent();
            }
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
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

    private void getContent() {
        circularProgressBar.setVisibility(View.VISIBLE);
        ContentSearchParams contentSearchParams = new ContentSearchParams();
        contentSearchParams.setCategories(ApplicationState.getGuestUser().getCategories());
        apiService.getContents(contentSearchParams, new ContentCallback());
    }

    private void getNewContent(long endDate) {
        ContentSearchParams contentSearchParams = new ContentSearchParams();
        contentSearchParams.setCategories(ApplicationState.getGuestUser().getCategories());
        contentSearchParams.setCreatedDate(endDate);
        contentSearchParams.setRefresh(true);
        apiService.getContents(contentSearchParams, new NewContentCallback());
    }

    private void getMoreContent(long startDate) {
        ContentSearchParams contentSearchParams = new ContentSearchParams();
        contentSearchParams.setCategories(ApplicationState.getGuestUser().getCategories());
        contentSearchParams.setCreatedDate(startDate);
        apiService.getContents(contentSearchParams, new MoreContentCallback());
    }

    private void success(View view, String response) {
        try {
            Contents contents = jsonParserService.fromJson(response, Contents.class);
            SharedPreferencesService.getInstance().putString(Config.JSON_HOME_CONTENTS, response);
            HomeFragment.contents = contents;
            contentRecyclerViewAdapter.setContents(HomeFragment.contents.getContents());
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
                    networkConnectionFailureFragment.setFragment(HomeFragment.this);
                    networkConnectionFailureFragment.show(HomeFragment.this.getActivity().getFragmentManager(), "");
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

    private class MoreContentCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException exception) {
            logService.e(TAG, exception.getMessage());
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
                            Contents contents = jsonParserService.fromJson(responseStr, Contents.class);

                            if (contents != null) {
                                contentRecyclerViewAdapter.getContents().addAll(contents.getContents());
                                contentRecyclerViewAdapter.notifyDataSetChanged();
                                try {
                                    SharedPreferencesService.getInstance().putString(Config.JSON_HOME_CONTENTS, jsonParserService.toJson(new Contents(contentRecyclerViewAdapter.getContents())));
                                } catch (Exception exception) {
                                    Log.e(TAG, exception.toString());
                                }
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
            logService.e(TAG, exception.getMessage());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            final String responseStr = response.body().string().toString();
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            //ServerException serverException = jsonParserService.fromJson(responseStr, ServerException.class);
                        } catch (Exception exception) {
                            logService.e(TAG, exception.getMessage());
                        }
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Contents contents = jsonParserService.fromJson(responseStr, Contents.class);

                            if (contents != null && contents.getContents().size() >= Constants.DEFAULT_CONTENTS_SIZE) {
                                contentRecyclerViewAdapter.setContents(contents.getContents());
                            } else if (contents != null) {
                                contentRecyclerViewAdapter.getContents().addAll(0, contents.getContents());
                            }

                            contentRecyclerViewAdapter.notifyDataSetChanged();

                            SharedPreferencesService.getInstance().putString(Config.JSON_HOME_CONTENTS, jsonParserService.toJson(new Contents(contentRecyclerViewAdapter.getContents())));
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

    public static class NetworkConnectionFailureFragment extends DialogFragment {

        private HomeFragment fragment;

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

        public void setFragment(HomeFragment fragment) {
            this.fragment = fragment;
        }

        private void dismissFragment() {
            this.dismiss();
        }

        private void retry() {
            this.dismiss();
            fragment.getContent();
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
}
