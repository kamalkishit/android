package com.humanize.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.humanize.android.common.Constants;
import com.humanize.android.ContentFragmentDrawerListener;
import com.humanize.android.ContentService;
import com.humanize.android.FragmentDrawer;
import com.humanize.android.JsonParser;
import com.humanize.android.R;
import com.humanize.android.UserService;
import com.humanize.android.common.StringConstants;
import com.humanize.android.content.data.Content;
import com.humanize.android.content.data.Contents;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CardActivity extends AppCompatActivity {

    public static Contents contents = null;
    private ContentService contentService;
    private UserService userService;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    private FragmentDrawer fragmentDrawer;
    private RecyclerViewAdapter recyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean doubleBackToExitPressedOnce;

    private static String TAG = CardActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.DOUBLE_BACK_EXIT_STR, Snackbar.LENGTH_SHORT);
        snackbar.show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, Constants.DOUBLE_EXIT_DELAY_TIME);
    }

    private void initialize() {
        contentService = new ContentService();
        userService = new UserService();
        doubleBackToExitPressedOnce = false;
        //swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (recyclerViewAdapter.contents.getContents().size() > 0) {
                    getNewContent(Long.toString(recyclerViewAdapter.contents.getContents().get(0).getCreatedDate()));
                }
            }
        });

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(contents);
        recyclerView.setAdapter(recyclerViewAdapter);

        toolbar.setCollapsible(true);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fragmentDrawer = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragmentNavigationDrawer);
        fragmentDrawer.setUp(R.id.fragmentNavigationDrawer, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar);
        fragmentDrawer.setDrawerListener(new ContentFragmentDrawerListener());
    }

    private void configureListeners() {
        recyclerView.setOnScrollListener(new EndlessRecyclerViewOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (recyclerViewAdapter.contents.getContents().size() > 0) {
                    getMoreContent(Long.toString(recyclerViewAdapter.contents.getContents().get(recyclerViewAdapter.contents.getContents().size() - 1).getCreatedDate()));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        if (id == R.id.actionSettings) {
            return true;
        } else if (id == R.id.actionCreate) {
            Intent intent = new Intent(getApplicationContext(), SubmitActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void openNavigationDrawer(MenuItem menuItem) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    private void getMoreContent(String startDate) {
        HttpUtil.getInstance().getMoreContents(startDate, new MoreContentCallback());
    }

    private void getNewContent(String endDate) {
        HttpUtil.getInstance().refreshContents(endDate, new NewContentCallback());
    }

    protected class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private Contents contents = null;

        public RecyclerViewAdapter(Contents contents) {
            this.contents = contents;
        }

        @Override
        public int getItemCount() {
            return contents.getContents().size();
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int index) {
            Content content = contents.getContents().get(index);
            viewHolder.content = content;
            viewHolder.id = content.getId();
            viewHolder.contentTitle.setText(content.getTitle());
            viewHolder.contentDescription.setText(content.getDescription());
            viewHolder.contentSource.setText(content.getSource());
            viewHolder.contentCategory.setText(content.getCategory());
            //viewHolder.imageView.setImageResource(R.drawable.background);
            viewHolder.contentImage.getLayoutParams().width = Config.IMAGE_WIDTH;
            viewHolder.contentImage.getLayoutParams().height = Config.IMAGE_HEIGHT;

            if (ApplicationState.getUser().getLikes().contains(viewHolder.id)) {
                Drawable drawableTop = getResources().getDrawable(R.drawable.recomended_selected);
                viewHolder.recommendButton.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
            } else {
                Drawable drawableTop = getResources().getDrawable(R.drawable.recomended);
                viewHolder.recommendButton.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
            }

            if (ApplicationState.getUser().getBookmarks().contains(viewHolder.id)) {
                Drawable drawableTop = getResources().getDrawable(R.drawable.bookmark_selected);
                viewHolder.bookmarkButton.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
            } else {
                Drawable drawableTop = getResources().getDrawable(R.drawable.bookmark);
                viewHolder.bookmarkButton.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
            }

            Picasso.with(ApplicationState.getAppContext())
                    .load(content.getOriginalImageURL())
                    .placeholder(R.drawable.background)
                    .fit().into(viewHolder.contentImage);
            /*Picasso.with(ApplicationState.getAppContext()).load("http://www.storypick.com/wp-content/uploads/2015/11/awesome-dad-cover.jpg")
                    .placeholder(R.drawable.background).resize(Config.IMAGE_WIDTH, Config.IMAGE_HEIGHT)
                    .into(viewHolder.imageView);*/
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int index) {
            View cardView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.content_card, viewGroup, false);

            return new RecyclerViewAdapter.ViewHolder(cardView);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            protected Content content;
            protected String id;
            protected TextView contentTitle;
            protected TextView contentDescription;
            protected ImageView contentImage;
            @Bind(R.id.contentSource) TextView contentSource;
            @Bind(R.id.contentCategory) TextView contentCategory;
            protected Button recommendButton;
            protected Button bookmarkButton;
            protected Button shareButton;

            private UserService userService;
            private ContentService contentService;
            private View shareableContentView;

            public ViewHolder(View view) {
                super(view);
                this.shareableContentView = view;
                userService = new UserService(ApplicationState.getUser());
                contentService = ContentService.getInstance();
                contentCategory = (TextView) view.findViewById(R.id.contentCategory);
                contentSource = (TextView) view.findViewById(R.id.contentSource);
                contentTitle = (TextView) view.findViewById(R.id.contentTitle);
                contentDescription = (TextView) view.findViewById(R.id.contentDescription);
                contentImage = (ImageView) view.findViewById(R.id.contentImage);
                recommendButton = (Button) view.findViewById(R.id.recommendButton);
                bookmarkButton = (Button) view.findViewById(R.id.bookmarkButton);
                shareButton = (Button) view.findViewById(R.id.shareButton);

                //ButterKnife.bind(this);

                configureListeners();

                //updateDefaultView();

                view.setOnClickListener(this);
            }

            private void updateDefaultView() {
                updateLikeButton();
                updateBookmarkButton();
            }

            private void configureListeners() {
                recommendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recommend();
                    }
                });

                bookmarkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //bookmark();
                    }
                });

                shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        share();
                    }
                });
            }

            private void recommend() {
                //userService.recommendContent(content.getId());

                if (ApplicationState.getUser().getLikes().contains(id)) {
                    ApplicationState.getUser().getLikes().remove(id);
                    userService.unrecommendContent(id);
                    contentService.decrRecommendationCount(id);
                } else {
                    ApplicationState.getUser().getLikes().add(id);
                    contentService.incrRecommendationCount(id);
                }

                content.setLikesCount(content.getLikesCount() + 1);
                updateContent(content);

                updateLikeButton();
            }

            private void bookmark() {
                userService.bookmarkContent(content.getId());
                updateBookmarkButton();
            }

            private void updateLikeButton() {
                if (ApplicationState.getUser().getLikes().contains(id)) {
                    recommendButton.setBackgroundResource(R.drawable.recomended_selected);
                    recommendButton.setText(StringConstants.RECOMMENDED);
                } else {
                    recommendButton.setBackgroundResource(R.drawable.recomended);
                    recommendButton.setText(StringConstants.RECOMMEND);
                }
            }

            private void updateBookmarkButton() {
                if (ApplicationState.getUser().getBookmarks().contains(id)) {
                    bookmarkButton.setBackgroundResource(R.drawable.bookmark_selected);
                } else {
                    bookmarkButton.setBackgroundResource(R.drawable.bookmark);
                }
            }

            private void share() {
                contentService.incrSharedCount(content.getId());
                shareableContentView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(shareableContentView.getDrawingCache());
                shareableContentView.setDrawingCacheEnabled(false);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_QUALITY_VALUE, byteArrayOutputStream);
                String path = MediaStore.Images.Media.insertImage(ApplicationState.getAppContext().getContentResolver(), bitmap, "Title", null);
                Uri imageUri = Uri.parse(path);

                content.setSharedCount(content.getSharedCount() + 1);
                updateContent(content);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, contents.getContents().get(getAdapterPosition()).getContentURL());
                ApplicationState.getAppContext().startActivity(shareIntent);
            }

            public void onClick(View view) {
                contentService.incrementViewCount(content.getId());
                content.setViewsCount(content.getViewsCount() + 1);
                updateContent(content);
                Intent intent = new Intent(ApplicationState.getAppContext(), WebBrowserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Config.CONTENT_URL, content.getContentURL());
                //View line = findViewById(R.id.line);
                ApplicationState.getAppContext().startActivity(intent);
            }
        }
    }

    public abstract class EndlessRecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {

        private int previousTotal = 0; // The total number of items in the dataset after the last load
        private boolean loading = true; // True if we are still waiting for the last set of data to load.
        private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
        int firstVisibleItem, visibleItemCount, totalItemCount;

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

    private void updateContent(Content content) {
        try {
            HttpUtil.getInstance().updateUser(Config.USER_UPDATE_URL, new JsonParser().toJson(content), new UpdateContentCallback());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private class MoreContentCallback implements Callback {
        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Contents contents = new JsonParser().fromJson(responseStr, Contents.class);

                            if (contents != null) {
                                recyclerViewAdapter.contents.getContents().addAll(contents.getContents());
                                recyclerViewAdapter.notifyDataSetChanged();
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
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Contents contents = new JsonParser().fromJson(responseStr, Contents.class);

                            if (contents != null && contents.getContents().size() >= Constants.DEFAULT_CONTENTS_SIZE) {
                                recyclerViewAdapter.contents = contents;
                                recyclerViewAdapter.notifyDataSetChanged();
                            } else if (contents != null) {
                                recyclerViewAdapter.contents.getContents().addAll(0, contents.getContents());
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
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

    private class UpdateContentCallback implements Callback {
        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }
    }
}
