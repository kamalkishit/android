package com.humanize.android.fragment;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.humanize.android.R;
import com.humanize.android.config.Config;
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.User;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.helper.ApplicationState;
import com.humanize.android.service.GsonParserServiceImpl;
import com.humanize.android.service.SharedPreferencesService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NavigationDrawerFragment extends Fragment {

    @Bind(R.id.navHeaderContainer) RelativeLayout navHeaderContainer;
    @Bind(R.id.settings) LinearLayout settings;
    @Bind(R.id.updateCategories) LinearLayout updateCategories;
    @Bind(R.id.updatePaperTime) LinearLayout updatePaperTime;
    @Bind(R.id.updatePaperNotification) LinearLayout updatePaperNotification;
    @Bind(R.id.paper) LinearLayout paper;
    @Bind(R.id.historicPaper) LinearLayout historicPaper;
    @Bind(R.id.bookmarkedArticles) LinearLayout bookmarkedArticles;
    @Bind(R.id.submitArticle) LinearLayout suggestArticle;
    @Bind(R.id.inviteFriend) LinearLayout inviteFriend;
    @Bind(R.id.shareApp) LinearLayout shareApp;
    @Bind(R.id.aboutUs) LinearLayout aboutUs;
    @Bind(R.id.contactUs) LinearLayout contactUs;

    @Bind(R.id.rateUs) LinearLayout rateUs;

    Switch paperNotificationSwitch;
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

        paper.setVisibility(View.GONE);
        historicPaper.setVisibility(View.GONE);

        navHeaderContainer.getLayoutParams().height = (Config.SCREEN_HEIGHT*3)/10;

        TextView textViewSettings = (TextView) settings.findViewById(R.id.textView);
        textViewSettings.setText(StringConstants.SETTINGS);
        ImageView imageViewSettings = (ImageView) settings.findViewById(R.id.imageView);
        imageViewSettings.setImageResource(R.drawable.ic_settings_black);

        TextView textViewUpdateCategories = (TextView) updateCategories.findViewById(R.id.textView);
        textViewUpdateCategories.setText(StringConstants.CATEGORIES);
        ImageView imageViewUpdateCategories = (ImageView) updateCategories.findViewById(R.id.imageView);
        imageViewUpdateCategories.setImageResource(R.drawable.ic_update_categories_black);

        TextView textViewUpdatePaperTime = (TextView) updatePaperTime.findViewById(R.id.textView);
        textViewUpdatePaperTime.setText(StringConstants.PAPER_TIME);
        ImageView imageViewUpdatePaperTime = (ImageView) updatePaperTime.findViewById(R.id.imageView);
        imageViewUpdatePaperTime.setImageResource(R.drawable.ic_paper_time_black);

        TextView textViewUpdatePaperNotification = (TextView) updatePaperNotification.findViewById(R.id.textView);
        textViewUpdatePaperNotification.setText(StringConstants.PAPER_NOTIFICATION);
        ImageView imageViewUpdatePaperNotification = (ImageView) updatePaperNotification.findViewById(R.id.imageView);
        imageViewUpdatePaperNotification.setImageResource(R.drawable.ic_notification_black);
        paperNotificationSwitch = (Switch) updatePaperNotification.findViewById(R.id.paperNotificationSwitch);
        paperNotificationSwitch.setChecked(ApplicationState.getUser().isPaperNotification());

        TextView textViewPaper = (TextView) paper.findViewById(R.id.textView);
        textViewPaper.setText(StringConstants.PAPER);
        ImageView imageViewPaper = (ImageView) paper.findViewById(R.id.imageView);
        imageViewPaper.setImageResource(R.drawable.ic_paper_black);

        TextView textViewHistoricPaper = (TextView) historicPaper.findViewById(R.id.textView);
        textViewHistoricPaper.setText(StringConstants.HISTORIC_PAPER);
        ImageView imageViewHistoricPaper = (ImageView) historicPaper.findViewById(R.id.imageView);
        imageViewHistoricPaper.setImageResource(R.drawable.ic_historic_paper_black);

        TextView textViewBookmarkedArticles = (TextView) bookmarkedArticles.findViewById(R.id.textView);
        textViewBookmarkedArticles.setText(StringConstants.BOOKMARKED_ARTICLES);
        ImageView imageViewBookmarkedArticles = (ImageView) bookmarkedArticles.findViewById(R.id.imageView);
        imageViewBookmarkedArticles.setImageResource(R.drawable.ic_bookmark_black);

        TextView textViewSuggestArticle = (TextView) suggestArticle.findViewById(R.id.textView);
        textViewSuggestArticle.setText(StringConstants.SUGGEST_ARTICLE);
        ImageView imageViewSuggestArticle = (ImageView) suggestArticle.findViewById(R.id.imageView);
        imageViewSuggestArticle.setImageResource(R.drawable.ic_suggest_article_black);

        TextView textViewInviteFriend = (TextView) inviteFriend.findViewById(R.id.textView);
        textViewInviteFriend.setText(StringConstants.INVITE_FRIEND);
        ImageView imageViewInviteFriend = (ImageView) inviteFriend.findViewById(R.id.imageView);
        imageViewInviteFriend.setImageResource(R.drawable.ic_invite_user_black);

        TextView textViewShareApp = (TextView) shareApp.findViewById(R.id.textView);
        textViewShareApp.setText(StringConstants.SHARE_APP);
        ImageView imageViewShareApp = (ImageView) shareApp.findViewById(R.id.imageView);
        imageViewShareApp.setImageResource(R.drawable.ic_share);

        TextView textViewAboutUs = (TextView) aboutUs.findViewById(R.id.textView);
        textViewAboutUs.setText(StringConstants.ABOUT_US);
        ImageView imageViewAboutUs = (ImageView) aboutUs.findViewById(R.id.imageView);
        imageViewAboutUs.setImageResource(R.drawable.ic_about_us_black);

        TextView textViewContactUs = (TextView) contactUs.findViewById(R.id.textView);
        textViewContactUs.setText(StringConstants.CONTACT_US);
        ImageView imageViewContactUs = (ImageView) contactUs.findViewById(R.id.imageView);
        imageViewContactUs.setImageResource(R.drawable.ic_contact_us_black);

        TextView textViewRateUs = (TextView) rateUs.findViewById(R.id.textView);
        textViewRateUs.setText(StringConstants.RATE_US);
        ImageView imageViewRateUs = (ImageView) rateUs.findViewById(R.id.imageView);
        imageViewRateUs.setImageResource(R.drawable.ic_rate_us_black);
    }

    private void defaultStateDrawer() {
        updateCategories.setVisibility(View.GONE);
        updatePaperTime.setVisibility(View.GONE);
        updatePaperNotification.setVisibility(View.GONE);
        isSettingOpened = false;
    }

    private void configureListeners() {
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (isSettingOpened) {
                        updateCategories.setVisibility(View.GONE);
                        updatePaperTime.setVisibility(View.GONE);
                        updatePaperNotification.setVisibility(View.GONE);
                        isSettingOpened = false;
                    } else {
                        updateCategories.setVisibility(View.VISIBLE);
                        //updatePaperTime.setVisibility(View.VISIBLE);
                        //updatePaperNotification.setVisibility(View.VISIBLE);
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

        updatePaperTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityLauncher.startUpdatePaperTimeActivity();
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        paperNotificationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = ApplicationState.getUser();
                if (user.isPaperNotification()) {
                    paperNotificationSwitch.setChecked(false);
                    user.setPaperNotification(false);
                } else {
                    paperNotificationSwitch.setChecked(true);
                    user.setPaperNotification(true);
                }

                try {
                    SharedPreferencesService.getInstance().putString(Config.JSON_USER_DATA, new GsonParserServiceImpl().toJson(user));
                } catch (Exception exception) {

                }
            }
        });

        paper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityLauncher.startPaperActivity();
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        historicPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //activityLauncher.startHistoricPaperActivity();
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        bookmarkedArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityLauncher.startBookmarksActivity();
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

        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityLauncher.startAboutUsActivity();
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

    private void share() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, StringConstants.HUMANIZE);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "abcdef" + "\n" + StringConstants.HUMANIZE_SHARE_STR);
        ApplicationState.getAppContext().startActivity(shareIntent);
    }
}