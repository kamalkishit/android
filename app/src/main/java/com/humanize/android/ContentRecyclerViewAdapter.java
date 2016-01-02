package com.humanize.android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.humanize.android.activity.WebBrowserActivity;
import com.humanize.android.common.Constants;
import com.humanize.android.data.Content;
import com.humanize.android.data.Contents;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.service.ContentService;
import com.humanize.android.service.ContentServiceImpl;
import com.humanize.android.service.JsonParserImpl;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.service.UserService;
import com.humanize.android.service.UserServiceImpl;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by kamal on 12/22/15.
 */
public class ContentRecyclerViewAdapter extends RecyclerView.Adapter<ContentRecyclerViewAdapter.ContentViewHolder> {

    private List<Content> contents = null;
    private UserService userService;

    private static String TAG = ContentRecyclerViewAdapter.class.getSimpleName();

    public ContentRecyclerViewAdapter(List<Content> contents) {
        this.contents = contents;
        userService = new UserServiceImpl();
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    @Override
    public int getItemCount() {
        if (contents != null) {
            return contents.size();
        }

        return 0;
    }

    @Override
    public void onBindViewHolder(ContentViewHolder viewHolder, int index) {
        Content content = contents.get(index);
        viewHolder.content = content;
        viewHolder.contentId = content.getId();
        viewHolder.contentTitle.setText(content.getTitle());
        viewHolder.contentDescription.setText(content.getDescription());
        viewHolder.contentSource.setText(content.getSource());
        viewHolder.contentCategory.setText(content.getCategory());

        if (content.getViewedCount() > 0) {
            viewHolder.contentViewedCount.setText("" + content.getViewedCount());
        } else {
            viewHolder.contentViewedCount.setText("0");
        }

        if (content.getSharedCount() > 0) {
            viewHolder.contentSharedCount.setText("" + content.getSharedCount());
        } else {
            viewHolder.contentSharedCount.setText("0");
        }

        if (content.getRecommendedCount() > 0) {
            viewHolder.contentRecommendedCount.setText("" + content.getRecommendedCount());
        } else {
            viewHolder.contentRecommendedCount.setText("0");
        }

        //viewHolder.imageView.setImageResource(R.drawable.background);
        viewHolder.contentImage.getLayoutParams().width = Config.IMAGE_WIDTH;
        viewHolder.contentImage.getLayoutParams().height = Config.IMAGE_HEIGHT;

        updateRecommendationButton(viewHolder);
        updateBookmarkButton(viewHolder);

        Picasso.with(ApplicationState.getAppContext()).load(content.getOriginalImageURL()).placeholder(R.drawable.background)
        //System.out.println(Config.IMAGES_URL + "?imageName=" + content.getImageURL());
        //Picasso.with(ApplicationState.getAppContext()).load(Config.IMAGES_URL + content.getImageURL())
                .fit().into(viewHolder.contentImage);
    }

    private void updateRecommendationButton(ContentViewHolder viewHolder) {
        if (userService.isRecommended(viewHolder.contentId)) {
            viewHolder.recommendButton.setImageResource(R.drawable.ic_recomend_filled_green);
        } else {
            viewHolder.recommendButton.setImageResource(R.drawable.ic_recomend_filled_grey);
        }
    }

    private void updateBookmarkButton(ContentViewHolder viewHolder) {
        if (userService.isBookmarked(viewHolder.contentId)) {
            viewHolder.bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled_green);
        } else {
            viewHolder.bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled_grey);
        }
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup viewGroup, int index) {
        View cardView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_card, viewGroup, false);

        return new ContentRecyclerViewAdapter.ContentViewHolder(cardView);
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected Content content;
        protected String contentId;
        protected TextView contentTitle;
        protected TextView contentDescription;
        protected ImageView contentImage;
        protected TextView contentSource;
        protected TextView contentCategory;
        protected TextView contentViewedCount;
        protected TextView contentRecommendedCount;
        protected TextView contentSharedCount;
        protected ImageButton recommendButton;
        protected ImageButton bookmarkButton;
        protected ImageButton shareButton;

        private UserService userService;
        private ContentService contentService;
        private View shareableContentView;

        public ContentViewHolder(View view) {
            super(view);
            this.shareableContentView = view;
            userService = new UserServiceImpl();
            contentService = new ContentServiceImpl();
            contentCategory = (TextView) view.findViewById(R.id.contentCategory);
            contentSource = (TextView) view.findViewById(R.id.contentSource);
            contentTitle = (TextView) view.findViewById(R.id.contentTitle);
            contentDescription = (TextView) view.findViewById(R.id.contentDescription);
            contentImage = (ImageView) view.findViewById(R.id.contentImage);
            contentViewedCount = (TextView) view.findViewById(R.id.viewsCount);
            contentRecommendedCount = (TextView) view.findViewById(R.id.recommendationsCount);
            contentSharedCount = (TextView) view.findViewById(R.id.sharedCount);
            recommendButton = (ImageButton) view.findViewById(R.id.recommendButton);
            bookmarkButton = (ImageButton) view.findViewById(R.id.bookmarkButton);
            shareButton = (ImageButton) view.findViewById(R.id.shareButton);

            //ButterKnife.bind(this);

            configureListeners();

            updateDefaultView();

            view.setOnClickListener(this);
        }

        private void updateDefaultView() {
            updateRecommendationButton();
            updateBookmarkButton();
        }

        private void updateViewedCount() {
            if (content.getViewedCount() > 0) {
                contentViewedCount.setText("" + content.getViewedCount());
            } else {
                contentViewedCount.setText("0");
            }
        }

        private void updateRecommendedCount() {
            if (content.getRecommendedCount() > 0) {
                contentRecommendedCount.setText("" + content.getRecommendedCount());
            } else {
                contentRecommendedCount.setText("0");
            }
        }

        private void updateSharedCount() {
            if (content.getSharedCount() > 0) {
                contentSharedCount.setText("" + content.getSharedCount());
            } else {
                contentSharedCount.setText("0");
            }
        }

        private void configureListeners() {
            contentCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadByCategory(contentCategory.getText().toString());
                }
            });

            recommendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recommend(view);
                }
            });

            bookmarkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bookmark(view);
                }
            });

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    share();
                }
            });
        }

        private void loadByCategory(String category) {
            new ActivityLauncher().startSingleCategoryContentActivity(category);
        }

        private void recommend(View view) {
            userService.recommend(content);
            updateRecommendationButton();
            updateRecommendedCount();
            updateRecommendedJson(content);

            /*if (userService.isRecommended(content.getId())) {
                Snackbar.make(view, "Recommended", 1000).show();
            } else {
                Snackbar.make(view, "Recommendation removed", Snackbar.LENGTH_SHORT).show();
            }*/

            ContentRecyclerViewAdapter.this.notifyDataSetChanged();
        }

        private void bookmark(View view) {
            userService.bookmark(content);
            updateBookmarkButton();
            updateBookmarksJson(content);

            /*if (userService.isBookmarked(content.getId())) {
                Snackbar.make(view, "Bookmarked", Snackbar.LENGTH_SHORT).setDuration(1000).show();
            } else {
                Snackbar.make(view, "Bookmark removed", Snackbar.LENGTH_SHORT).show();
            }*/

            ContentRecyclerViewAdapter.this.notifyDataSetChanged();
        }

        private void updateBookmarksJson(Content content) {
            if (userService.isBookmarked(content.getId())) {
                updateJson(Config.JSON_BOOKMARKED_CONTENTS, content, true);
            } else {
                updateJson(Config.JSON_BOOKMARKED_CONTENTS, content, false);
            }
        }

        private void updateRecommendedJson(Content content) {
            if (userService.isRecommended(content.getId())) {
                updateJson(Config.JSON_RECOMMENDED_CONTENTS, content, true);
            } else {
                updateJson(Config.JSON_RECOMMENDED_CONTENTS, content, false);
            }
        }

        private void updateJson(String jsonKey, Content content, boolean add) {
            try {
                String json = SharedPreferencesService.getInstance().getString(jsonKey);
                Contents contents = new JsonParserImpl().fromJson(json, Contents.class);

                if (add) {
                    contents.addContent(content);
                } else {
                    contents.removeContent(content);
                }

                json = new JsonParserImpl().toJson(contents);
                SharedPreferencesService.getInstance().putString(jsonKey, json);
            } catch (Exception exception) {

            }
        }

        private void updateRecommendationButton() {
            if (userService.isRecommended(contentId)) {
                recommendButton.setImageResource(R.drawable.ic_recomend_filled_green);
            } else {
                recommendButton.setImageResource(R.drawable.ic_recomend_filled_grey);
            }
        }

        private void updateBookmarkButton() {
            if (userService.isBookmarked(contentId)) {
                bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled_green);
            } else {
                bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled_grey);
            }
        }

        private void share() {
            contentService.incrSharedCount(content);
            shareableContentView.setDrawingCacheEnabled(true);
            contentImage.setDrawingCacheEnabled(true);
            //Bitmap bitmap = Bitmap.createBitmap(shareableContentView.getDrawingCache());
            Bitmap bitmap = Bitmap.createBitmap(contentImage.getDrawingCache());
            shareableContentView.setDrawingCacheEnabled(false);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_QUALITY_VALUE, byteArrayOutputStream);
            String path = MediaStore.Images.Media.insertImage(ApplicationState.getAppContext().getContentResolver(), bitmap, "Title", null);
            Uri imageUri = Uri.parse(path);

            content.setSharedCount(content.getSharedCount() + 1);
            //updateSharedCount();
            updateContent(content);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, content.getTitle());
            //shareIntent.putExtra(Intent.EXTRA_TEXT, contents.get(getAdapterPosition()).getUrl());
            ApplicationState.getAppContext().startActivity(shareIntent);
        }

        public void onClick(View view) {
            contentService.incrViewedCount(content);
            //content.setViewedCount(content.getViewedCount() + 1);
            updateViewedCount();
            updateContent(content);
            Intent intent = new Intent(ApplicationState.getAppContext(), WebBrowserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Config.CONTENT_URL, content.getUrl());
            //View line = findViewById(R.id.line);
            ApplicationState.getAppContext().startActivity(intent);
        }

        private void updateContent(Content content) {
            try {
                HttpUtil.getInstance().updateUser(Config.USER_UPDATE_URL, new JsonParserImpl().toJson(content), new UpdateContentCallback());
            } catch (Exception exception) {
                exception.printStackTrace();
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
