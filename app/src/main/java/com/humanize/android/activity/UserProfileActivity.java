package com.humanize.android.activity;

import android.os.Bundle;;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.humanize.android.R;
import com.humanize.android.helper.ApplicationState;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserProfileActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;
    @Bind(R.id.bookmarksCount) TextView bookmarksCount;
    @Bind(R.id.bookmarkedContent) TextView bookmarkedContent;
    @Bind(R.id.upvotesCount) TextView upvotesCount;
    @Bind(R.id.upvotedContent) TextView upvotedContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        overridePendingTransition(R.anim.slide_right_to_left, 0);

        ButterKnife.bind(this);

        initialize();

        /*String profilePhoto = "http://igcdn-photos-f-a.akamaihd.net/hphotos-ak-xpa1/t51.2885-19/929118_455075441302693_1605108410_a.jpg";

        Picasso.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.profile_image)
                .resize(80, 80)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(profileImageView); */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
            overridePendingTransition(0, R.anim.slide_left_to_right);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_left_to_right);
    }

    private void initialize() {
        toolbarText.setText("Profile");
        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (ApplicationState.getGuestUser().getBookmarks().size() == 1) {
            bookmarkedContent.setText("Bookmark");
        }

        if (ApplicationState.getGuestUser().getUpvotes().size() == 1) {
            upvotedContent.setText("Upvote");
        }

        bookmarksCount.setText("" + ApplicationState.getGuestUser().getBookmarks().size());
        upvotesCount.setText("" + ApplicationState.getGuestUser().getUpvotes().size());
    }
}
