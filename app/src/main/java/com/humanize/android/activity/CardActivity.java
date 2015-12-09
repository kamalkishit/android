package com.humanize.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
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
import android.widget.Toast;

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

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;

    private FragmentDrawer fragmentDrawer;
    private RecyclerViewAdapter recyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;

    private static String TAG = CardActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorAccent);
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
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_create) {
            Intent intent = new Intent(getApplicationContext(), SubmitActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_camera) {
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_search) {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
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

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        protected Contents contents = null;

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
            //viewHolder.source.setText(content.getSource());
            //viewHolder.category.setText(content.getCategory());
            //viewHolder.imageView.setImageResource(R.drawable.background);
            viewHolder.contentImage.getLayoutParams().width = Config.IMAGE_WIDTH;
            viewHolder.contentImage.getLayoutParams().height = Config.IMAGE_HEIGHT;

            /*if (ApplicationState.getUser().getLikes().contains(viewHolder.id)) {
                viewHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_white_24dp);
            } else {
                viewHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_border_white_24dp);
            }

            if (ApplicationState.getUser().getBookmarks().contains(viewHolder.id)) {
                viewHolder.bookmarkButton.setBackgroundResource(R.drawable.ic_bookmark_white_24dp);
            } else {
                viewHolder.bookmarkButton.setBackgroundResource(R.drawable.ic_bookmark_border_white_24dp);
            }*/

            Picasso.with(ApplicationState.getAppContext()).load(content.getOriginalImageURL())
                    .placeholder(R.drawable.background).resize(Config.IMAGE_WIDTH, Config.IMAGE_HEIGHT)
                    .into(viewHolder.contentImage);
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
            @Bind(R.id.contentTitle) TextView contentTitle;
            @Bind(R.id.contentDescription) TextView contentDescription;
            @Bind(R.id.contentImage) ImageView contentImage;
            //@Bind(R.id.contentSource) TextView contentSource;
            //@Bind(R.id.contentCategory) TextView contentCategory;
            @Bind(R.id.recommendButton) Button recommendButton;
            @Bind(R.id.bookmarkButton) Button bookmarkButton;
            @Bind(R.id.shareButton) Button shareButton;

            private UserService userService;
            private ContentService contentService;
            private View shareableContentView;

            public ViewHolder(View view) {
                super(view);
                this.shareableContentView = view;
                userService = new UserService(ApplicationState.getUser());
                contentService = ContentService.getInstance();

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
                        //like();
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

            private void like() {
                userService.like(content);
                if (userService.isLiked(id)) {
                    contentService.incrementLikeCount(id);
                } else {
                    contentService.decrementLikeCount(id);
                }

                updateLikeButton();
            }

            private void bookmark() {
                userService.bookmark(content);
                updateBookmarkButton();
            }

            private void updateLikeButton() {
                if (ApplicationState.getUser().getLikes().contains(id)) {
                    recommendButton.setBackgroundResource(R.drawable.ic_favorite_white_24dp);
                } else {
                    recommendButton.setBackgroundResource(R.drawable.ic_favorite_border_white_24dp);
                }
            }

            private void updateBookmarkButton() {
                if (ApplicationState.getUser().getBookmarks().contains(id)) {
                    bookmarkButton.setBackgroundResource(R.drawable.ic_bookmark_white_24dp);
                } else {
                    bookmarkButton.setBackgroundResource(R.drawable.ic_bookmark_border_white_24dp);
                }
            }

            private void share() {
                shareableContentView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(shareableContentView.getDrawingCache());
                shareableContentView.setDrawingCacheEnabled(false);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_QUALITY_VALUE, byteArrayOutputStream);
                String path = MediaStore.Images.Media.insertImage(ApplicationState.getAppContext().getContentResolver(), bitmap, "Title", null);
                Uri imageUri = Uri.parse(path);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, contents.getContents().get(getAdapterPosition()).getContentURL());
                ApplicationState.getAppContext().startActivity(shareIntent);
            }

            public void onClick(View view) {
                contentService.incrementViewCount(id);
                Intent intent = new Intent(ApplicationState.getAppContext(), WebBrowserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Config.CONTENT_URL, content.getContentURL());
                View line = findViewById(R.id.line);
                /*ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(CardActivity.this, (View)line, "webViewTransition");
                startActivity(intent, options.toBundle());*/
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

    private class MoreContentCallback implements Callback {
        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), StringConstants.NETWORK_CONNECTION_ERROR_STR, Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), StringConstants.FAILURE_STR, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), StringConstants.NETWORK_CONNECTION_ERROR_STR, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(), StringConstants.FAILURE_STR, Toast.LENGTH_LONG).show();
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
}
