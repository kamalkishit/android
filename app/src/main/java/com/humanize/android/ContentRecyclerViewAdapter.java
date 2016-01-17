package com.humanize.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.humanize.android.activity.WebBrowserActivity;
import com.humanize.android.common.Constants;
import com.humanize.android.data.Content;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by kamal on 12/22/15.
 */
public class ContentRecyclerViewAdapter extends RecyclerView.Adapter<ContentRecyclerViewAdapter.ContentViewHolder> {

    private List<Content> contents = null;
    private Activity activity;

    protected boolean disableCategorySelection;

    private static String TAG = ContentRecyclerViewAdapter.class.getSimpleName();

    public ContentRecyclerViewAdapter(Activity activity, List<Content> contents) {
        this.activity = activity;
        this.contents = contents;
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
        viewHolder.contentDate.setText(dateFormat.format("MMM dd yyyy", new java.sql.Date(new Timestamp(content.getCreatedDate()).getTime())));
        viewHolder.contentSource.setText(content.getSource());
        viewHolder.contentCategory.setText(content.getCategory());

        viewHolder.contentImage.getLayoutParams().width = Config.IMAGE_WIDTH;
        viewHolder.contentImage.getLayoutParams().height = Config.IMAGE_HEIGHT;

        System.out.println(ApiUrls.URL_IMAGES + content.getImageId());
        Picasso.with(ApplicationState.getAppContext()).load(ApiUrls.URL_IMAGES + content.getImageId())
                .fit().into(viewHolder.contentImage);
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
        protected TextView contentDate;
        protected ImageView contentImage;
        protected TextView contentSource;
        protected TextView contentCategory;
        protected Button shareButton;

        private View shareableContentView;

        public ContentViewHolder(View view) {
            super(view);
            this.shareableContentView = view;
            contentCategory = (TextView) view.findViewById(R.id.contentCategory);
            contentSource = (TextView) view.findViewById(R.id.contentSource);
            contentTitle = (TextView) view.findViewById(R.id.contentTitle);
            contentDescription = (TextView) view.findViewById(R.id.contentDescription);
            contentDate = (TextView) view.findViewById(R.id.contentDate);
            contentImage = (ImageView) view.findViewById(R.id.contentImage);
            shareButton = (Button) view.findViewById(R.id.shareButton);

            configureListeners();

            view.setOnClickListener(this);
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
                    share();
                }
            });
        }

        private void refineByCategory(String category) {
            new ActivityLauncher().startSingleCategoryContentActivity(category);
        }

        private void share() {
            shareableContentView.setDrawingCacheEnabled(true);
            contentImage.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(contentImage.getDrawingCache());
            shareableContentView.setDrawingCacheEnabled(false);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_QUALITY_VALUE, byteArrayOutputStream);
            String path = MediaStore.Images.Media.insertImage(ApplicationState.getAppContext().getContentResolver(), bitmap, "Title", null);
            Uri imageUri = Uri.parse(path);

            content.setSharedCount(content.getSharedCount() + 1);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "HUMANIZE");
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, content.getTitle() + "\n" + content.getShortUrl() + "\n" + " -via HUMANIZE");
            ApplicationState.getAppContext().startActivity(shareIntent);
        }

        public void onClick(View view) {
            Intent intent = new Intent(ApplicationState.getAppContext(), WebBrowserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Config.URL, content.getOriginalUrl());
            intent.putExtra(Config.SOURCE, content.getSource());
            ApplicationState.getAppContext().startActivity(intent);
        }
    }
}
