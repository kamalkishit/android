package com.humanize.android.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.humanize.android.R;
import com.humanize.android.activity.WebBrowserActivity;
import com.humanize.android.config.ApiUrls;
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.Content;
import com.humanize.android.data.Contents;
import com.humanize.android.service.ContentService;
import com.humanize.android.service.ContentServiceImpl;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.service.UserService;
import com.humanize.android.service.UserServiceImpl;
import com.humanize.android.config.Config;
import com.humanize.android.service.GsonParserServiceImpl;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Callback;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by kamal on 12/22/15.
 */
public class ContentRecyclerViewAdapter extends RecyclerView.Adapter<ContentRecyclerViewAdapter.ContentViewHolder> {

    private List<Content> contents = null;
    private Activity activity;
    private ContentViewHolder viewHolder;

    private UserService userService;
    private ContentService contentService;

    protected boolean disableCategorySelection;

    private static String TAG = ContentRecyclerViewAdapter.class.getSimpleName();

    public ContentRecyclerViewAdapter(Activity activity, List<Content> contents) {
        this.activity = activity;
        this.contents = contents;
        userService = new UserServiceImpl();
        contentService = new ContentServiceImpl();
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public void resetIndex() {
        bindViewHolder(viewHolder, 0);
    }

    @Override
    public int getItemCount() {
        if (contents != null) {
            return contents.size();
        }

        return 0;
    }

    public void disableCategorySelection() {
        disableCategorySelection = true;
    }

    @Override
    public void onBindViewHolder(final ContentViewHolder viewHolder, int index) {
        final Content content = contents.get(index);
        viewHolder.content = content;
        viewHolder.contentId = content.getId();

        viewHolder.contentTitle.setText(content.getTitle());
        viewHolder.contentDescription.setText(content.getDescription());
        DateFormat dateFormat = new DateFormat();
        viewHolder.contentDate.setText(dateFormat.format(StringConstants.DATE_FORMAT_STR, new java.sql.Date(new Timestamp(content.getCreatedDate()).getTime())));
        viewHolder.contentSource.setText(content.getSource());
        viewHolder.contentCategory.setText(content.getCategory().toUpperCase());

        viewHolder.contentImage.getLayoutParams().width = Config.IMAGE_WIDTH;
        viewHolder.contentImage.getLayoutParams().height = Config.IMAGE_HEIGHT;

        updateBookmarkButton(viewHolder);
        updateUpvoteButton(viewHolder);

        if (content.getViewedCount() > 0) {
            viewHolder.contentViewedCount.setText("" + content.getViewedCount());
        } else {
            viewHolder.contentViewedCount.setText("0");
        }

        if (content.getUpvotedCount() > 0) {
            viewHolder.contentUpvotedCount.setText("" + content.getUpvotedCount());
        } else {
            viewHolder.contentUpvotedCount.setText("0");
        }

        if (content.getSharedCount() > 0) {
            viewHolder.contentSharedCount.setText("" + content.getSharedCount());
        } else {
            viewHolder.contentSharedCount.setText("0");
        }

        Picasso.with(ApplicationState.getAppContext()).load(ApiUrls.URL_IMAGES + content.getImageId())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .fit().centerCrop()
                .into(viewHolder.contentImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        // Try again online if cache failed
                        Picasso.with(ApplicationState.getAppContext()).load(ApiUrls.URL_IMAGES + content.getImageId())
                                .fit().centerCrop()
                                .into(viewHolder.contentImage);
                    }
                });
            }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup viewGroup, int index) {
        View cardView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_card, viewGroup, false);
        viewHolder = new ContentRecyclerViewAdapter.ContentViewHolder(cardView);
        return viewHolder;
    }

    private void updateBookmarkButton(ContentViewHolder viewHolder) {
        if (userService.isBookmarked(viewHolder.contentId)) {
            viewHolder.bookmarkButton.setImageDrawable(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_bookmark_filled_green));
        } else {
            viewHolder.bookmarkButton.setImageDrawable(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_bookmark_filled_grey));
        }
    }

    private void updateUpvoteButton(ContentViewHolder viewHolder) {
        if (userService.isUpvoted(viewHolder.contentId)) {
            viewHolder.upvoteButton.setImageDrawable(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_recomend_filled_green));
        } else {
            viewHolder.upvoteButton.setImageDrawable(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_recomend_filled_grey));
        }
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected Content content;
        protected String contentId;
        protected TextView contentTitle;
        protected TextView contentDescription;
        protected TextView contentDate;
        protected ImageView contentImage;
        protected TextView contentSource;
        protected TextView contentCategory;
        protected LinearLayout linearLayoutCounts;
        protected TextView contentViewedCount;
        protected TextView contentUpvotedCount;
        protected TextView contentSharedCount;
        protected ImageButton bookmarkButton;
        protected ImageButton upvoteButton;
        protected ImageButton shareButton;

        public ContentViewHolder(View view) {
            super(view);
            contentCategory = (TextView) view.findViewById(R.id.contentCategory);
            contentSource = (TextView) view.findViewById(R.id.contentSource);
            contentTitle = (TextView) view.findViewById(R.id.contentTitle);
            contentDescription = (TextView) view.findViewById(R.id.contentDescription);
            contentDate = (TextView) view.findViewById(R.id.contentDate);
            contentImage = (ImageView) view.findViewById(R.id.contentImage);
            linearLayoutCounts = (LinearLayout) view.findViewById(R.id.counts);
            contentViewedCount = (TextView) view.findViewById(R.id.viewsCount);
            contentUpvotedCount = (TextView) view.findViewById(R.id.upvotesCount);
            contentSharedCount = (TextView) view.findViewById(R.id.sharedCount);
            bookmarkButton = (ImageButton) view.findViewById(R.id.bookmarkButton);
            upvoteButton = (ImageButton) view.findViewById(R.id.upvoteButton);
            shareButton = (ImageButton) view.findViewById(R.id.shareButton);

            linearLayoutCounts.setVisibility(View.GONE);
            configureListeners();

            view.setOnClickListener(this);
            updateBookmarkButton();
            updateUpvotebutton();
        }

        private void configureListeners() {
            if (!disableCategorySelection) {
                contentCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refineByCategory(toOriginalCategory(contentCategory.getText().toString()));
                    }
                });
            } else {
                contentCategory.setOnClickListener(null);
            }

            bookmarkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bookmark();
                }
            });

            upvoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    upvote();
                }
            });

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    share();
                }
            });
        }

        private String toOriginalCategory(String category) {
            if (category.equals("ACHIEVERS")) {
                return new String("Achievers");
            } else if (category.equals("BEAUTIFUL")) {
                return new String("Beautiful");
            } else if (category.equals("CHANGEMAKERS")) {
                return new String("Changemakers");
            } else if (category.equals("EDUCATION")) {
                return new String("Education");
            } else if (category.equals("EMPOWERMENT")) {
                return new String("Empowerment");
            } else if (category.equals("ENCOURAGING")) {
                return new String("Encouraging");
            } else if (category.equals("ENVIRONMENT")) {
                return new String("Environment");
            } else if (category.equals("GOVERNANCE")) {
                return new String("Governance");
            } else if (category.equals("HEALTH")) {
                return new String("Health");
            } else if (category.equals("HUMANITY")) {
                return new String("Humanity");
            } else if (category.equals("INSPIRING")) {
                return new String("Inspiring");
            } else if (category.equals("LAW AND JUSTICE")) {
                return new String("Law and Justice");
            } else if (category.equals("REAL HEROES")) {
                return new String("Real Heroes");
            } else if (category.equals("SCIENCE AND TECH")) {
                return new String("Science and Tech");
            } else if (category.equals("SPORTS")) {
                return new String("Sports");
            }

            return category;
        }

        private void refineByCategory(String category) {
            new ActivityLauncher().startSingleCategoryContentActivity(category);
        }

        private void bookmark() {
            if (userService.isBookmarked(content.getId())) {
                userService.unbookmark(contentId);
                updateJson(Config.JSON_BOOKMARKED_CONTENTS, content, false);
                bookmarkButton.setImageDrawable(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_bookmark_filled_grey));
            } else {
                updateJson(Config.JSON_BOOKMARKED_CONTENTS, content, true);
                userService.bookmark(contentId);
                bookmarkButton.setImageDrawable(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_bookmark_filled_green));
            }
        }

        private void updateBookmarkButton() {
            if (userService.isBookmarked(contentId)) {
                bookmarkButton.setImageDrawable(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_bookmark_filled_green));
            } else {
                bookmarkButton.setImageDrawable(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_bookmark_filled_grey));
            }
        }

        private void upvote() {
            if (userService.isUpvoted(content.getId())) {
                userService.downvote(contentId);
                contentService.decrUpvotedCount(content);
                updateJson(Config.JSON_UPVOTED_CONTENTS, content, false);
                upvoteButton.setImageDrawable(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_recomend_filled_grey));
            } else {
                updateJson(Config.JSON_UPVOTED_CONTENTS, content, true);
                userService.upvote(contentId);
                contentService.incrUpvotedCount(content);
                upvoteButton.setImageDrawable(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_recomend_filled_green));
            }

            updateUpvotedCount();
        }

        private void updateUpvotebutton() {
            if (userService.isUpvoted(contentId)) {
                upvoteButton.setImageDrawable(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_recomend_filled_green));
            } else {
                upvoteButton.setImageDrawable(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_recomend_filled_grey));
            }
        }

        private void updateJson(String jsonKey, Content content, boolean add) {
            try {
                String json = SharedPreferencesService.getInstance().getString(jsonKey);
                Contents contents;
                if (json == null) {
                    contents = new Contents();
                } else {
                    contents = new GsonParserServiceImpl().fromJson(json, Contents.class);
                }

                if (add) {
                    contents.addContent(content);
                } else {
                    contents.removeContent(content);
                }

                String contentJson = new GsonParserServiceImpl().toJson(contents);
                SharedPreferencesService.getInstance().putString(jsonKey, contentJson);
                String userDataJson = new GsonParserServiceImpl().toJson(ApplicationState.getGuestUser());
                SharedPreferencesService.getInstance().putString(Config.JSON_GUEST_USER_DATA, userDataJson);
            } catch (Exception exception) {

            }
        }

        public void onClick(View view) {
            contentService.incrViewedCount(content);
            updateViewedCount();
            Intent intent = new Intent(ApplicationState.getAppContext(), WebBrowserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Config.URL, content.getOriginalUrl());
            intent.putExtra(Config.SOURCE, content.getSource());
            ApplicationState.getAppContext().startActivity(intent);
        }

        private void share() {
            contentService.incrSharedCount(content);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, StringConstants.HUMANIZE);
            shareIntent.putExtra(Intent.EXTRA_TEXT, content.getShortUrl());
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ApplicationState.getAppContext().startActivity(shareIntent);
        }

        private void updateViewedCount() {
            if (content.getViewedCount() > 0) {
                contentViewedCount.setText("" + content.getViewedCount());
            } else {
                contentViewedCount.setText("0");
            }
        }

        private void updateUpvotedCount() {
            if (content.getUpvotedCount() > 0) {
                contentUpvotedCount.setText("" + content.getUpvotedCount());
            } else {
                contentUpvotedCount.setText("0");
            }
        }

        private void requestPermissions(Activity activity) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }
}
