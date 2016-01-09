package com.humanize.android;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.humanize.android.activity.LoginActivity;
import com.humanize.android.fragment.LoginFragment;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FragmentDrawer extends Fragment {

    @Bind(R.id.emailId) TextView emailId;
    @Bind(R.id.login) LinearLayout login;
    @Bind(R.id.preferences) LinearLayout preferences;
    @Bind(R.id.bookmarkedArticles) LinearLayout bookmarkedArticles;
    @Bind(R.id.recommendedArticles) LinearLayout recommendedArticles;
    @Bind(R.id.recommendAnArticle) LinearLayout recommendAnArticle;
    @Bind(R.id.contactUs) LinearLayout contactUs;
    @Bind(R.id.inviteFriend) LinearLayout inviteFriend;
    //@Bind(R.id.aboutUs) LinearLayout aboutUs;
    @Bind(R.id.rateUs) LinearLayout rateUs;
    //@Bind(R.id.termsOfUsage) LinearLayout termsOfUsage;
    //@Bind(R.id.privacyPolicy) LinearLayout privacyPolicy;
    @Bind(R.id.line) View line;
    @Bind(R.id.logout) LinearLayout logout;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private View containerView;
    private ActivityLauncher activityLauncher;
    private Activity activity;

    private static String TAG = FragmentDrawer.class.getSimpleName();

    public FragmentDrawer() {

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

        if (SharedPreferencesService.getInstance().getBoolean(Config.IS_LOGGED_IN)) {
            login.setVisibility(View.GONE);
            emailId.setText(ApplicationState.getUser().getEmailId());
        } else {
            line.setVisibility(View.GONE);
            logout.setVisibility(View.GONE);
        }

        // TBD: to use butterknife and a single method for initializing all side nav bar items
        TextView textViewLogin = (TextView) login.findViewById(R.id.textView);
        textViewLogin.setText("Login");
        ImageView imageViewLogin = (ImageView) login.findViewById(R.id.imageView);
        imageViewLogin.setImageResource(R.drawable.ic_profile_black);

        TextView textViewPreferences = (TextView) preferences.findViewById(R.id.textView);
        textViewPreferences.setText("Settings");
        ImageView imageViewPreferences = (ImageView) preferences.findViewById(R.id.imageView);
        imageViewPreferences.setImageResource(R.drawable.ic_settings_black);

        TextView textViewBookmarkedArticles = (TextView) bookmarkedArticles.findViewById(R.id.textView);
        textViewBookmarkedArticles.setText("Bookmarked Articles");
        ImageView imageViewBookmarkedArticles = (ImageView) bookmarkedArticles.findViewById(R.id.imageView);
        imageViewBookmarkedArticles.setImageResource(R.drawable.ic_bookmark_black);

        TextView textViewRecommendedArticles = (TextView) recommendedArticles.findViewById(R.id.textView);
        textViewRecommendedArticles.setText("Recommended Articles");
        ImageView imageViewRecommendedArticle = (ImageView) recommendedArticles.findViewById(R.id.imageView);
        imageViewRecommendedArticle.setImageResource(R.drawable.ic_recomend_black);

        TextView textViewRecommendAnArticle = (TextView) recommendAnArticle.findViewById(R.id.textView);
        textViewRecommendAnArticle.setText("Suggest an Article");
        ImageView imageViewRecommendAnArticle = (ImageView) recommendAnArticle.findViewById(R.id.imageView);
        imageViewRecommendAnArticle.setImageResource(R.drawable.ic_suggest_article_black);

        TextView textViewContactUs = (TextView) contactUs.findViewById(R.id.textView);
        textViewContactUs.setText("Contact Us");
        ImageView imageViewContactUs = (ImageView) contactUs.findViewById(R.id.imageView);
        imageViewContactUs.setImageResource(R.drawable.ic_contact_us_black);

        TextView textViewInviteFriend = (TextView) inviteFriend.findViewById(R.id.textView);
        textViewInviteFriend.setText("Invite a Friend");
        ImageView imageViewInviteFriend = (ImageView) inviteFriend.findViewById(R.id.imageView);
        imageViewInviteFriend.setImageResource(R.drawable.ic_invite_user_black);

        TextView textViewRateUs = (TextView) rateUs.findViewById(R.id.textView);
        textViewRateUs.setText("Rate Us");
        ImageView imageViewRateUs = (ImageView) rateUs.findViewById(R.id.imageView);
        imageViewRateUs.setImageResource(R.drawable.ic_rate_us_black);

        /*TextView textViewAboutUs = (TextView) aboutUs.findViewById(R.id.textView);
        textViewAboutUs.setText("About Us");
        ImageView imageViewAboutUs = (ImageView) aboutUs.findViewById(R.id.imageView);
        imageViewAboutUs.setImageResource(R.drawable.ic_about_us_black);

        TextView textViewTermsOfUsage = (TextView) termsOfUsage.findViewById(R.id.textView);
        textViewTermsOfUsage.setText("Terms of Usage");
        ImageView imageViewTermsOfUsage = (ImageView) termsOfUsage.findViewById(R.id.imageView);
        imageViewTermsOfUsage.setImageResource(R.drawable.ic_terms_of_usage_black);

        TextView textViewPrivacyPolicy = (TextView) privacyPolicy.findViewById(R.id.textView);
        textViewPrivacyPolicy.setText("Privacy Policy");
        ImageView imageViewPrivacyPolicy = (ImageView) privacyPolicy.findViewById(R.id.imageView);
        imageViewPrivacyPolicy.setImageResource(R.drawable.ic_privacy_black);*/

        TextView textViewLogout = (TextView) logout.findViewById(R.id.textView);
        textViewLogout.setText("Logout");
        ImageView imageViewLogout = (ImageView) logout.findViewById(R.id.imageView);
        imageViewLogout.setImageResource(R.drawable.ic_logout_black);
    }

    private void configureListeners() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn()) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    activityLauncher.startLoginActivity();
                }

            }
        });

        preferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn()) {
                    //activityLauncher.startSettingsActivity();
                    activityLauncher.startSelectCategoriesActivity();
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    loginPrompt();
                }

            }
        });

        bookmarkedArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn()) {
                    activityLauncher.startBookmarksActivity();
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    loginPrompt();
                }
            }
        });


        recommendedArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn()) {
                    activityLauncher.startRecommendationsActivity();
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    loginPrompt();
                }
            }
        });

        recommendAnArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn()) {
                    activityLauncher.startRecommendAnArticleActivity();
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    loginPrompt();
                }
            }
        });

        inviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn()) {
                    activityLauncher.startInviteFriendActivity();
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    loginPrompt();
                }
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn()) {
                    activityLauncher.startContactUsActivity();
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    loginPrompt();
                }
            }
        });


        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn()) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    loginPrompt();
                }
            }
        });

        /*aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityLauncher.startAboutUsActivity();
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        termsOfUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityLauncher.startUsageActivity();
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityLauncher.startPrivacyActivity();
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        }); */

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn()) {
                    logoutUser();
                    activityLauncher.startLoginActivityWithClearStack();
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    loginPrompt();
                }
            }
        });
    }

    private boolean isLoggedIn() {
        return SharedPreferencesService.getInstance().getBoolean(Config.IS_LOGGED_IN);
    }

    private void loginPrompt() {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.show(activity.getFragmentManager(), "");
    }

    private void logoutUser() {
        SharedPreferencesService.getInstance().delete(Config.IS_LOGGED_IN);
        SharedPreferencesService.getInstance().delete(Config.JSON_USER_DATA);
        SharedPreferencesService.getInstance().delete(Config.TOKEN);
        SharedPreferencesService.getInstance().delete(Config.JSON_CONTENTS);
        SharedPreferencesService.getInstance().delete(Config.JSON_BOOKMARKED_CONTENTS);
        SharedPreferencesService.getInstance().delete(Config.JSON_RECOMMENDED_CONTENTS);
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