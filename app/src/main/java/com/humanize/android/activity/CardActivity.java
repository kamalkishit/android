package com.humanize.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.humanize.android.ContentFragmentDrawerListener;
import com.humanize.android.ContentService;
import com.humanize.android.FragmentDrawer;
import com.humanize.android.HttpResponseCallback;
import com.humanize.android.R;
import com.humanize.android.UserService;
import com.humanize.android.content.data.Content;
import com.humanize.android.content.data.Contents;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class CardActivity extends AppCompatActivity {

    public static Contents contents = null;
    private Toolbar toolbar;
    private FragmentDrawer fragmentDrawer;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        initialize();
    }

    private void initialize() {

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContents();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(contents);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.setOnScrollListener(new EndlessRecyclerViewOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (recyclerViewAdapter.contents.getContents().size() > 0) {
                    getMoreContents(Long.toString(recyclerViewAdapter.contents.getContents().get(recyclerViewAdapter.contents.getContents().size() - 1).getCreatedDate()));
                }
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setCollapsible(true);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fragmentDrawer = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        fragmentDrawer.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        fragmentDrawer.setDrawerListener(new ContentFragmentDrawerListener());
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
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    private void getMoreContents(String startDate) {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getMoreContents(startDate, new HttpResponseCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    System.out.println("got new content");
                    Contents contents = new Gson().fromJson(response, Contents.class);

                    if (contents != null) {
                        recyclerViewAdapter.contents.getContents().addAll(contents.getContents());
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                System.out.println(errorMsg);
                System.out.println("why failure");
            }
        });
    }

    private void failure(String msg) {

    }

    private void success(String response) {
        try {
            /*JSONObject jsonObject = new JSONObject(response);
            ArrayList<Content> contents = JSONObjectParser.parseContents(
                    jsonObject);*/

            Contents contents = new Gson().fromJson(response, Contents.class);
            recyclerViewAdapter.contents.getContents().addAll(contents.getContents());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshContents() {
        if (recyclerViewAdapter.contents.getContents().size() > 0) {
            String endDate = Long.toString(recyclerViewAdapter.contents.getContents().get(0).getCreatedDate());
            HttpUtil httpUtil = HttpUtil.getInstance();

            httpUtil.refreshContents(endDate, new HttpResponseCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        Contents contents = new Gson().fromJson(response, Contents.class);

                        if (contents != null && contents.getContents().size() >= 20) {
                            recyclerViewAdapter.contents = contents;
                            recyclerViewAdapter.notifyDataSetChanged();
                        } else if (contents != null) {
                            recyclerViewAdapter.contents.getContents().addAll(0, contents.getContents());
                            recyclerViewAdapter.notifyDataSetChanged();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(String errorMsg) {
                    System.out.println("failure");
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {

        }
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
            viewHolder.title.setText(content.getTitle());
            viewHolder.description.setText(content.getDescription());
            viewHolder.source.setText(content.getSource());
            System.out.println(content.getCategory());
            String data = content.getLikesCount() + "likes, " + content.getViewsCount() + "views, " + content.getSharedCount() + "shares";
            //viewHolder.miscContent.setText(data);
            viewHolder.category.setText(content.getCategory());
            //viewHolder.imageView.setImageResource(R.drawable.background);
            viewHolder.imageView.getLayoutParams().width = Config.IMAGE_WIDTH;
            viewHolder.imageView.getLayoutParams().height = Config.IMAGE_HEIGHT;

            if (ApplicationState.getUser().getLikes().contains(viewHolder.id)) {
                viewHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_white_24dp);
            } else {
                viewHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_border_white_24dp);
            }

            if (ApplicationState.getUser().getBookmarks().contains(viewHolder.id)) {
                viewHolder.bookmarkButton.setBackgroundResource(R.drawable.ic_bookmark_white_24dp);
            } else {
                viewHolder.bookmarkButton.setBackgroundResource(R.drawable.ic_bookmark_border_white_24dp);
            }

            Picasso.with(ApplicationState.getAppContext()).load(Config.SERVER_URL + "/images/" + content.getImageURL())
                    .placeholder(R.drawable.background).resize(Config.IMAGE_WIDTH, Config.IMAGE_HEIGHT)
                    .into(viewHolder.imageView);
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
            protected TextView title;
            protected TextView description;
            protected ImageView imageView;
            protected TextView source;
            protected TextView miscContent;
            protected TextView category;
            protected Button likeButton;
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
                title = (TextView) view.findViewById(R.id.content_title);
                description = (TextView) view.findViewById(R.id.content_description);
                imageView = (ImageView) view.findViewById(R.id.content_image);
                source = (TextView) view.findViewById(R.id.content_source);
                //miscContent = (TextView) view.findViewById(R.id.content_misc);
                category = (TextView) view.findViewById(R.id.content_category);
                likeButton= (Button) view.findViewById(R.id.image_button_like);
                bookmarkButton = (Button) view.findViewById(R.id.image_button_bookmark);
                shareButton = (Button) view.findViewById(R.id.image_button_share);

                configureListeners();

                update();

                view.setOnClickListener(this);
            }

            private void update() {
                updateLikeButton();
                updateBookmarkButton();
            }

            private void configureListeners() {
                likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        like();
                    }
                });

                bookmarkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bookmark();
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
                    likeButton.setBackgroundResource(R.drawable.ic_favorite_white_24dp);
                } else {
                    likeButton.setBackgroundResource(R.drawable.ic_favorite_border_white_24dp);
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
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
                View line = (View) findViewById(R.id.line);
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
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached

                // Do something
                current_page++;

                onLoadMore(current_page);

                loading = true;
            }
        }

        public abstract void onLoadMore(int current_page);
    }
}
