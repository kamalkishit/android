package com.humanize.android.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.humanize.android.fragment.OneFragment;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.service.ApiServiceImpl;
import com.humanize.android.helper.ContentRecyclerViewAdapter;
import com.humanize.android.data.ContentSearchParams;
import com.humanize.android.fragment.NavigationDrawerFragment;
import com.humanize.android.service.JsonParserService;
import com.humanize.android.R;
import com.humanize.android.service.ApiService;
import com.humanize.android.helper.ApplicationState;
import com.humanize.android.service.GsonParserServiceImpl;
import com.humanize.android.config.Constants;
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.Contents;
import com.humanize.android.service.LogService;
import com.humanize.android.service.LogServiceImpl;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.config.Config;
import com.humanize.android.utils.AppUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    public static Contents contents = null;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tabs) TabLayout tabLayout;
    @Bind(R.id.viewpager) ViewPager viewPager;
    @Bind(R.id.circularProgressBar) ProgressBar circularProgressBar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.drawerLayout) DrawerLayout drawerLayout;

    private JsonParserService jsonParserService;
    private NavigationDrawerFragment navigationDrawerFragment;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean doubleBackToExitPressedOnce;
    private ApiService apiService;

    private static final String TAG = HomeActivity.class.getName();
    private static final LogService logService = new LogServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Snackbar.make(coordinatorLayout, StringConstants.DOUBLE_BACK_EXIT_STR, Snackbar.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, Constants.DOUBLE_EXIT_DELAY_TIME);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionCreate) {
            new ActivityLauncher().startSubmitArticleActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        jsonParserService = new GsonParserServiceImpl();
        apiService = new ApiServiceImpl();
        circularProgressBar.setVisibility(View.GONE);
        doubleBackToExitPressedOnce = false;

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        toolbar.setCollapsible(true);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavigationDrawer);
        navigationDrawerFragment.getView().getLayoutParams().width = Config.NAV_DRAWER_WIDTH;
        navigationDrawerFragment.setUp(R.id.fragmentNavigationDrawer, drawerLayout, toolbar);
        navigationDrawerFragment.setActivity(this);

        contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(this, null);
        recyclerView.setAdapter(contentRecyclerViewAdapter);

        //tabLayout.setVisibility(View.GONE);

        try {
            if (SharedPreferencesService.getInstance().getString(Config.JSON_CONTENTS) != null) {
                HomeActivity.contents = jsonParserService.fromJson(SharedPreferencesService.getInstance().getString(Config.JSON_CONTENTS), Contents.class);
                contentRecyclerViewAdapter.setContents(HomeActivity.contents.getContents());
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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "HOME");
        adapter.addFragment(new OneFragment(), "TRENDING");
        viewPager.setAdapter(adapter);
    }

    private void configureListeners() {
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentRecyclerViewAdapter.resetIndex();
                contentRecyclerViewAdapter.notifyDataSetChanged();
            }
        });

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

    public void openNavigationDrawer(MenuItem menuItem) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    private void getContent() {
        circularProgressBar.setVisibility(View.VISIBLE);
        ContentSearchParams contentSearchParams = new ContentSearchParams();
        contentSearchParams.setCategories(ApplicationState.getUser().getCategories());
        apiService.getContents(contentSearchParams, new ContentCallback());
    }

    private void getNewContent(long endDate) {
        ContentSearchParams contentSearchParams = new ContentSearchParams();
        contentSearchParams.setCategories(ApplicationState.getUser().getCategories());
        contentSearchParams.setCreatedDate(endDate);
        contentSearchParams.setRefresh(true);
        apiService.getContents(contentSearchParams, new NewContentCallback());
    }

    private void getMoreContent(long startDate) {
        ContentSearchParams contentSearchParams = new ContentSearchParams();
        contentSearchParams.setCategories(ApplicationState.getUser().getCategories());
        contentSearchParams.setCreatedDate(startDate);
        apiService.getContents(contentSearchParams, new MoreContentCallback());
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

    private void success(View view, String response) {
        try {
            Contents contents = jsonParserService.fromJson(response, Contents.class);
            SharedPreferencesService.getInstance().putString(Config.JSON_CONTENTS, response);
            HomeActivity.contents = contents;
            contentRecyclerViewAdapter.setContents(HomeActivity.contents.getContents());
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
                    networkConnectionFailureFragment.show(HomeActivity.this.getFragmentManager(), "");
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
                                    SharedPreferencesService.getInstance().putString(Config.JSON_CONTENTS, jsonParserService.toJson(new Contents(contentRecyclerViewAdapter.getContents())));
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

                            SharedPreferencesService.getInstance().putString(Config.JSON_CONTENTS, jsonParserService.toJson(new Contents(contentRecyclerViewAdapter.getContents())));
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

        private void dismissFragment() {
            this.dismiss();
        }

        private void retry() {
            this.dismiss();
            ((HomeActivity)getActivity()).getContent();
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}
