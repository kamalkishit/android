package com.humanize.android.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.humanize.android.R;
import com.humanize.android.activity.WebBrowserActivity;
import com.humanize.android.config.ApiUrls;
import com.humanize.android.config.Constants;
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.Content;
import com.humanize.android.data.Contents;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.service.UserService;
import com.humanize.android.service.UserServiceImpl;
import com.humanize.android.config.Config;
import com.humanize.android.service.GsonParserServiceImpl;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

    protected boolean disableCategorySelection;

    private static String TAG = ContentRecyclerViewAdapter.class.getSimpleName();

    public ContentRecyclerViewAdapter(Activity activity, List<Content> contents) {
        this.activity = activity;
        this.contents = contents;
        userService = new UserServiceImpl();
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
    public void onBindViewHolder(ContentViewHolder viewHolder, int index) {
        Content content = contents.get(index);
        viewHolder.content = content;
        viewHolder.contentId = content.getId();

        viewHolder.contentTitle.setText(content.getTitle());
        viewHolder.contentDescription.setText(content.getDescription());
        DateFormat dateFormat = new DateFormat();
        viewHolder.contentDate.setText(dateFormat.format(StringConstants.DATE_FORMAT_STR, new java.sql.Date(new Timestamp(content.getCreatedDate()).getTime())));
        viewHolder.contentSource.setText(content.getSource());
        viewHolder.contentCategory.setText(content.getCategory());

        viewHolder.contentImage.getLayoutParams().width = Config.IMAGE_WIDTH;
        viewHolder.contentImage.getLayoutParams().height = Config.IMAGE_HEIGHT;

        updateBookmarkButton(viewHolder);

        Picasso.with(ApplicationState.getAppContext()).load(ApiUrls.URL_IMAGES + content.getImageId())
                .fit().into(viewHolder.contentImage);
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup viewGroup, int index) {
        View cardView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_card, viewGroup, false);
        viewHolder = new ContentRecyclerViewAdapter.ContentViewHolder(cardView);
        return viewHolder;
    }

    private void updateBookmarkButton(ContentViewHolder viewHolder) {
        if (userService.isBookmarked(viewHolder.contentId)) {
            viewHolder.bookmarkButton.setBackground(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_bookmark_filled_green));
        } else {
            viewHolder.bookmarkButton.setBackground(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_bookmark_filled));
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
        protected Button bookmarkButton;
        protected Button shareButton;

        public ContentViewHolder(View view) {
            super(view);
            contentCategory = (TextView) view.findViewById(R.id.contentCategory);
            contentSource = (TextView) view.findViewById(R.id.contentSource);
            contentTitle = (TextView) view.findViewById(R.id.contentTitle);
            contentDescription = (TextView) view.findViewById(R.id.contentDescription);
            contentDate = (TextView) view.findViewById(R.id.contentDate);
            contentImage = (ImageView) view.findViewById(R.id.contentImage);
            bookmarkButton = (Button) view.findViewById(R.id.bookmarkButton);
            shareButton = (Button) view.findViewById(R.id.shareButton);

            configureListeners();

            view.setOnClickListener(this);
            updateBookmarkButton();
        }

        private void configureListeners() {
            if (!disableCategorySelection) {
                contentCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refineByCategory(contentCategory.getText().toString());
                    }
                });
            } else {
                contentCategory.setOnClickListener(null);
            }

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    share(activity);
                }
            });

            bookmarkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bookmark();
                }
            });
        }

        private void refineByCategory(String category) {
            new ActivityLauncher().startSingleCategoryContentActivity(category);
        }

        private void bookmark() {
            if (userService.isBookmarked(content.getId())) {
                userService.bookmark(contentId);
                updateJson(Config.JSON_BOOKMARKED_CONTENTS, content, false);
                bookmarkButton.setBackground(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_bookmark_filled));
            } else {
                updateJson(Config.JSON_BOOKMARKED_CONTENTS, content, true);
                userService.bookmark(contentId);
                bookmarkButton.setBackground(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_bookmark_filled_green));
            }
        }

        private void updateBookmarkButton() {
            if (userService.isBookmarked(contentId)) {
                bookmarkButton.setBackground(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_bookmark_filled_green));
            } else {
                bookmarkButton.setBackground(ApplicationState.getAppContext().getResources().getDrawable(R.drawable.ic_bookmark_filled));
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

                json = new GsonParserServiceImpl().toJson(contents);
                SharedPreferencesService.getInstance().putString(jsonKey, json);
            } catch (Exception exception) {

            }
        }

        private void share(Activity activity) {
            contentImage.setDrawingCacheEnabled(true);
            /*if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(activity);
            } else {
                share();
            }*/

            share();
        }

        public void onClick(View view) {
            Intent intent = new Intent(ApplicationState.getAppContext(), WebBrowserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Config.URL, content.getOriginalUrl());
            intent.putExtra(Config.SOURCE, content.getSource());
            ApplicationState.getAppContext().startActivity(intent);
        }

        private void share() {
            Bitmap bitmap = Bitmap.createBitmap(contentImage.getDrawingCache());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, Constants.IMAGE_QUALITY_VALUE, byteArrayOutputStream);

            String filename = StringConstants.SHARED_IMAGE_NAME;
            File cacheFile = new File(ApplicationState.getAppContext().getCacheDir() + File.separator + filename);

            try {
                if (!cacheFile.exists()) {
                    cacheFile.createNewFile();
                }

                byte[] bytes = byteArrayOutputStream.toByteArray();
                FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);
                fileOutputStream.write(bytes);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception exception) {

            }

            Uri imageUri = FileProvider.getUriForFile(ApplicationState.getAppContext(), StringConstants.FILE_PROVIDER_URI, cacheFile);

            content.setSharedCount(content.getSharedCount() + 1);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, StringConstants.HUMANIZE);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_TEXT, content.getTitle() + "\n" + content.getShortUrl() + "\n" + StringConstants.HUMANIZE_SHARE_STR);
            ApplicationState.getAppContext().startActivity(shareIntent);
        }

        private void requestPermissions(Activity activity) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }
}
