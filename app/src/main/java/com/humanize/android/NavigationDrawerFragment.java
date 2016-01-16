package com.humanize.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.humanize.android.common.StringConstants;
import com.humanize.android.helper.ActivityLauncher;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NavigationDrawerFragment extends Fragment {

    @Bind(R.id.settings) LinearLayout settings;
    @Bind(R.id.updateCategories) LinearLayout updateCategories;
    @Bind(R.id.suggestArticle) LinearLayout suggestArticle;
    @Bind(R.id.aboutUs) LinearLayout aboutUs;
    @Bind(R.id.contactUs) LinearLayout contactUs;
    @Bind(R.id.inviteFriend) LinearLayout inviteFriend;
    @Bind(R.id.rateUs) LinearLayout rateUs;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private View containerView;
    private ActivityLauncher activityLauncher;
    private Activity activity;
    private boolean isSettingOpened;

    private static String TAG = NavigationDrawerFragment.class.getSimpleName();

    public NavigationDrawerFragment() {

    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initialize() {
        activityLauncher = new ActivityLauncher();
        isSettingOpened = false;
        defaultStateDrawer();

        TextView textViewSettings = (TextView) settings.findViewById(R.id.textView);
        textViewSettings.setText(StringConstants.SETTINGS);
        ImageView imageViewSettings = (ImageView) settings.findViewById(R.id.imageView);
        imageViewSettings.setImageResource(R.drawable.ic_settings_black);

        TextView textViewUpdateCategories = (TextView) updateCategories.findViewById(R.id.textView);
        textViewUpdateCategories.setText(StringConstants.UPDATE_CATEGORIES);
        ImageView imageViewUpdateCategories = (ImageView) updateCategories.findViewById(R.id.imageView);
        imageViewUpdateCategories.setImageResource(R.drawable.ic_update_categories_black);

        TextView textViewSuggestArticle = (TextView) suggestArticle.findViewById(R.id.textView);
        textViewSuggestArticle.setText(StringConstants.SUGGEST_ARTICLE);
        ImageView imageViewSuggestArticle = (ImageView) suggestArticle.findViewById(R.id.imageView);
        imageViewSuggestArticle.setImageResource(R.drawable.ic_suggest_article_black);

        TextView textViewAboutUs = (TextView) aboutUs.findViewById(R.id.textView);
        textViewAboutUs.setText(StringConstants.ABOUT_US);
        ImageView imageViewAboutUs = (ImageView) aboutUs.findViewById(R.id.imageView);
        imageViewAboutUs.setImageResource(R.drawable.ic_about_us_black);

        TextView textViewContactUs = (TextView) contactUs.findViewById(R.id.textView);
        textViewContactUs.setText(StringConstants.CONTACT_US);
        ImageView imageViewContactUs = (ImageView) contactUs.findViewById(R.id.imageView);
        imageViewContactUs.setImageResource(R.drawable.ic_contact_us_black);

        TextView textViewInviteFriend = (TextView) inviteFriend.findViewById(R.id.textView);
        textViewInviteFriend.setText(StringConstants.INVITE_FRIEND);
        ImageView imageViewInviteFriend = (ImageView) inviteFriend.findViewById(R.id.imageView);
        imageViewInviteFriend.setImageResource(R.drawable.ic_invite_user_black);

        TextView textViewRateUs = (TextView) rateUs.findViewById(R.id.textView);
        textViewRateUs.setText(StringConstants.RATE_US);
        ImageView imageViewRateUs = (ImageView) rateUs.findViewById(R.id.imageView);
        imageViewRateUs.setImageResource(R.drawable.ic_rate_us_black);
    }

    private void defaultStateDrawer() {
        updateCategories.setVisibility(View.GONE);
        isSettingOpened = false;
    }

    private void configureListeners() {
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (isSettingOpened) {
                        updateCategories.setVisibility(View.GONE);
                        isSettingOpened = false;
                    } else {
                        updateCategories.setVisibility(View.VISIBLE);
                        isSettingOpened = true;
                    }
            }
        });

        updateCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    activityLauncher.startUpdateCategoriesActivity();
                    drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        suggestArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    activityLauncher.startSuggestArticleActivity();
                    drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        inviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    activityLauncher.startInviteFriendActivity();
                    drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    activityLauncher.startContactUsActivity();
                    drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });


        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        ButterKnife.bind(this, layout);
        initialize();
        configureListeners();

        return layout;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        this.drawerLayout = drawerLayout;

        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                defaultStateDrawer();
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });
    }
}