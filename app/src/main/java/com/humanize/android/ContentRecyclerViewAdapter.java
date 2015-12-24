package com.humanize.android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.humanize.android.content.data.Content;
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
import java.util.Set;

/**
 * Created by kamal on 12/22/15.
 */
public class ContentRecyclerViewAdapter extends RecyclerView.Adapter<ContentRecyclerViewAdapter.ContentViewHolder> {

    private List<Content> contents = null;
    private UserService userService;

    private static String TAG = ContentRecyclerViewAdapter.class.getSimpleName();

    public ContentRecyclerViewAdapter(List<Content> contents) {
        this.contents = contents;
        userService = new UserService();
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
                .fit().into(viewHolder.contentImage);
    }

    private void updateRecommendationButton(ContentViewHolder viewHolder) {
        if (userService.isBookmarked(viewHolder.contentId)) {
            viewHolder.recommendButton.setImageResource(R.drawable.ic_recomend_fill);
        } else {
            viewHolder.recommendButton.setImageResource(R.drawable.ic_recomend_fill);
        }
    }

    private void updateBookmarkButton(ContentViewHolder viewHolder) {
        if (userService.isBookmarked(viewHolder.contentId)) {
            viewHolder.bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled_green);
        } else {
            viewHolder.bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled);
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
            userService = new UserService();
            contentService = new ContentService();
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
            recommendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recommend();
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

        private void recommend() {
            userService.recommend(content);
            updateRecommendationButton();
            updateRecommendedCount();
        }

        private void bookmark() {
            userService.bookmark(content);
            updateBookmarkButton();
        }

        private void updateRecommendationButton() {
            if (userService.isRecommended(contentId)) {
                recommendButton.setImageResource(R.drawable.ic_recomend);
            } else {
                recommendButton.setImageResource(R.drawable.ic_recomend_fill);
            }
        }

        private void updateBookmarkButton() {
            if (userService.isBookmarked(contentId)) {
                bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled_green);
            } else {
                bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled);
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
            updateSharedCount();
            updateContent(content);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, contents.get(getAdapterPosition()).getUrl());
            ApplicationState.getAppContext().startActivity(shareIntent);
        }

        public void onClick(View view) {
            contentService.incrViewedCount(content);
            content.setViewedCount(content.getViewedCount() + 1);
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
                HttpUtil.getInstance().updateUser(Config.USER_UPDATE_URL, new JsonParser().toJson(content), new UpdateContentCallback());
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
