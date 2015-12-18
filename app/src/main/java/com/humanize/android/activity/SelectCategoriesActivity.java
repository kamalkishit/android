package com.humanize.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.view.Gravity;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.humanize.android.JsonParser;
import com.humanize.android.R;
import com.humanize.android.common.StringConstants;
import com.humanize.android.data.User;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

// Referenced classes of package com.humanize.android.activity:
//            PaperReminderActivity

public class SelectCategoriesActivity extends AppCompatActivity {

    private Set<String> categories;
    private int selectedCategoriesCount;

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private Category achievers;
    private Category beautiful;
    private Category education;
    private Category empowerment;
    private Category environment;
    private Category governance;
    private Category health;
    private Category humanity;
    private Category lawAndJustice;
    private Category realHeroes;
    private Category scienceAndTech;
    private Category sports;
    private Button submitButton;

    private ActivityLauncher activityLauncher;

    private static final String TAG = SelectCategoriesActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_categories);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        selectedCategoriesCount = 0;
        categories = new HashSet<>();
        activityLauncher = new ActivityLauncher();

        submitButton = (Button)findViewById(R.id.nextButton);
        submitButton.setEnabled(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);

        categories = new HashSet<>();

        achievers = new Category();
        achievers.button = (Button)findViewById(R.id.achievers);

        beautiful = new Category();
        beautiful.button = (Button)findViewById(R.id.beautiful);

        education = new Category();
        education.button = (Button)findViewById(R.id.education);

        empowerment = new Category();
        empowerment.button = (Button)findViewById(R.id.empowerment);

        environment = new Category();
        environment.button = (Button)findViewById(R.id.environment);

        governance = new Category();
        governance.button = (Button)findViewById(R.id.governance);

        health = new Category();
        health.button = (Button)findViewById(R.id.health);

        humanity = new Category();
        humanity.button = (Button)findViewById(R.id.humanity);

        lawAndJustice = new Category();
        lawAndJustice.button = (Button)findViewById(R.id.lawAndJustice);

        realHeroes = new Category();
        realHeroes.button = (Button)findViewById(R.id.realHeroes);

        scienceAndTech = new Category();
        scienceAndTech.button = (Button)findViewById(R.id.scienceAndTech);

        sports = new Category();
        sports.button = (Button)findViewById(R.id.sports);

        //selectAllCategories();
    }

    private void configureListeners() {
        submitButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                List<String> categoriesList = new ArrayList<>();
                categoriesList.addAll(categories);
                ApplicationState.getUser().setCategories(categoriesList);
                ApplicationState.getUser().setIsConfigured(true);

                try {
                    String userdataJson = new JsonParser().toJson(ApplicationState.getUser());
                    System.out.println(Config.USER_UPDATE_URL);
                    System.out.println(userdataJson);

                    if (userdataJson != null) {
                        HttpUtil.getInstance().updateUser(Config.USER_UPDATE_URL, userdataJson, new UserUpdationCallback(coordinatorLayout));
                    }
                } catch (Exception exception) {

                }
            }
        });

        achievers.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                handleButtonSelection(achievers);
                if (achievers.isSelected) {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.achivers_white);
                    achievers.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                } else {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.achivers_green);
                    achievers.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                }
            }
        });

        beautiful.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                handleButtonSelection(beautiful);
                if (beautiful.isSelected) {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.beautiful_white);
                    beautiful.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                } else {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.beautiful_green);
                    beautiful.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                }
            }
        });

        education.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                handleButtonSelection(education);

                if (education.isSelected) {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.education_white);
                    education.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                } else {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.education_green);
                    education.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                }
            }
        });

        empowerment.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                handleButtonSelection(empowerment);

                if (empowerment.isSelected) {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.empowerment_white);
                    empowerment.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                } else {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.empowerment_green);
                    empowerment.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                }
            }
        });

        environment.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                handleButtonSelection(environment);

                if (environment.isSelected) {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.environment_white);
                    environment.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                } else {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.environment_green);
                    environment.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                }
            }
        });

        governance.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                handleButtonSelection(governance);

                if (governance.isSelected) {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.governence_white);
                    governance.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                } else {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.governence_green);
                    governance.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                }
            }
        });

        health.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                handleButtonSelection(health);

                if (health.isSelected) {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.health_white);
                    health.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                } else {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.health_green);
                    health.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                }
            }
        });

        humanity.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                handleButtonSelection(humanity);

                if (humanity.isSelected) {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.humanity_white);
                    humanity.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                } else {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.humanity_green);
                    humanity.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                }
            }
        });

        lawAndJustice.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                handleButtonSelection(lawAndJustice);

                if (lawAndJustice.isSelected) {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.law_white);
                    lawAndJustice.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                } else {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.law_green);
                    lawAndJustice.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                }
            }
        });

        realHeroes.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                handleButtonSelection(realHeroes);

                if (realHeroes.isSelected) {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.hero_white);
                    realHeroes.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                } else {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.hero_green);
                    realHeroes.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                }
            }
        });

        scienceAndTech.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                handleButtonSelection(scienceAndTech);

                if (scienceAndTech.isSelected) {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.science_white);
                    scienceAndTech.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                } else {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.science_green);
                    scienceAndTech.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                }
            }
        });

        sports.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                handleButtonSelection(sports);

                if (sports.isSelected) {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.sports_white);
                    sports.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                } else {
                    Drawable drawableTop = getResources().getDrawable(R.drawable.sports_green);
                    sports.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
                }
            }
        });
    }

    private void selectAllCategories() {
        achievers.isSelected = true;
        Drawable drawableTop = getResources().getDrawable(R.drawable.achivers_green);
        achievers.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
        achievers.button.setTextColor(getResources().getColor(R.color.colorWhite));
        achievers.button.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        categories.add(achievers.button.getText().toString());

        beautiful.isSelected = true;
        drawableTop = getResources().getDrawable(R.drawable.beautiful_green);
        beautiful.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
        beautiful.button.setTextColor(getResources().getColor(R.color.colorWhite));
        beautiful.button.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        categories.add(beautiful.button.getText().toString());

        education.isSelected = true;
        drawableTop = getResources().getDrawable(R.drawable.education_green);
        education.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
        education.button.setTextColor(getResources().getColor(R.color.colorWhite));
        education.button.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        categories.add(education.button.getText().toString());

        empowerment.isSelected = true;
        drawableTop = getResources().getDrawable(R.drawable.empowerment_green);
        empowerment.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
        empowerment.button.setTextColor(getResources().getColor(R.color.colorWhite));
        empowerment.button.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        categories.add(empowerment.button.getText().toString());

        environment.isSelected = true;
        drawableTop = getResources().getDrawable(R.drawable.environment_green);
        environment.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
        environment.button.setTextColor(getResources().getColor(R.color.colorWhite));
        environment.button.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        categories.add(environment.button.getText().toString());

        governance.isSelected = true;
        drawableTop = getResources().getDrawable(R.drawable.governence_green);
        governance.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
        governance.button.setTextColor(getResources().getColor(R.color.colorWhite));
        governance.button.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        categories.add(governance.button.getText().toString());

        health.isSelected = true;
        drawableTop = getResources().getDrawable(R.drawable.health_green);
        health.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
        health.button.setTextColor(getResources().getColor(R.color.colorWhite));
        health.button.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        categories.add(health.button.getText().toString());

        humanity.isSelected = true;
        drawableTop = getResources().getDrawable(R.drawable.humanity_green);
        humanity.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
        humanity.button.setTextColor(getResources().getColor(R.color.colorWhite));
        humanity.button.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        categories.add(humanity.button.getText().toString());

        lawAndJustice.isSelected = true;
        drawableTop = getResources().getDrawable(R.drawable.law_green);
        lawAndJustice.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
        lawAndJustice.button.setTextColor(getResources().getColor(R.color.colorWhite));
        lawAndJustice.button.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        categories.add(lawAndJustice.button.getText().toString());

        realHeroes.isSelected = true;
        drawableTop = getResources().getDrawable(R.drawable.hero_green);
        realHeroes.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
        realHeroes.button.setTextColor(getResources().getColor(R.color.colorWhite));
        realHeroes.button.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        categories.add(realHeroes.button.getText().toString());

        scienceAndTech.isSelected = true;
        drawableTop = getResources().getDrawable(R.drawable.science_green);
        scienceAndTech.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
        scienceAndTech.button.setTextColor(getResources().getColor(R.color.colorWhite));
        scienceAndTech.button.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        categories.add(scienceAndTech.button.getText().toString());

        sports.isSelected = true;
        drawableTop = getResources().getDrawable(R.drawable.sports_green);
        sports.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
        sports.button.setTextColor(getResources().getColor(R.color.colorWhite));
        sports.button.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        categories.add(sports.button.getText().toString());

        selectedCategoriesCount = 12;

        submitButton.setEnabled(true);
    }

    private void handleButtonSelection(Category category) {
        if (!category.isSelected) {
            selectedCategoriesCount = selectedCategoriesCount + 1;
            if (selectedCategoriesCount >= 3) {
                submitButton.setEnabled(true);
            }

            category.button.setTextColor(getResources().getColor(R.color.colorWhite));
            category.button.setBackgroundColor(getResources().getColor(R.color.colorSecondary));

            categories.add(category.button.getText().toString());
            category.isSelected = true;
        } else {
            selectedCategoriesCount = selectedCategoriesCount - 1;
            if (selectedCategoriesCount < 3) {
                submitButton.setEnabled(false);
            }

            category.button.setTextColor(getResources().getColor(R.color.colorSecondary));
            //category.button.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            category.button.setBackground(getResources().getDrawable(R.drawable.border_button_selector));
            categories.remove(category.button.getText().toString());
            category.isSelected = false;
        }
    }

    private void startPaperTimeSelectorActivity() {

        Point p = new Point();
        p.x = 100;
        p.y = 100;
        //showPopup(this, p);
        categories.toArray();
        //ApplicationState.getUser().setCategories((ArrayList<String>)(categories.toArray()));
        Intent intent = new Intent(getApplicationContext(), PaperReminderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showPopup(final Activity context, Point p) {
        int popupWidth = 800;
        int popupHeight = 1400;

        // Inflate the popup_layout.xml
        RelativeLayout viewGroup = (RelativeLayout) context.findViewById(R.id.paperTimeSelection);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.paper_time_selection, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        // Getting a reference to Close button, and close the popup when clicked.
    }

    class Category {

        private Button button;
        private boolean isSelected;

        protected Category() {
            this.button = null;
            this.isSelected = false;
        }
    }

    private class UserUpdationCallback implements Callback {

        private View view;

        public UserUpdationCallback(View view) {
            this.view = view;
        }
        @Override
        public void onFailure(Request request, IOException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(view, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(view, StringConstants.FAILURE_STR, Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            User user = new JsonParser().fromJson(responseStr, User.class);
                            ApplicationState.setUser(user);
                            startPaperTimeSelectorActivity();
                            //activityLauncher.startCardActivity(view);
                        } catch (Exception exception) {

                        }
                    }
                });
            }
        }
    }
}
