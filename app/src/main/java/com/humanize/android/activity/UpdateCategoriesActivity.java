package com.humanize.android.activity;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.humanize.android.service.JsonParserService;
import com.humanize.android.R;
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.User;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.service.GsonParserServiceImpl;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.helper.ApplicationState;
import com.humanize.android.config.Config;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UpdateCategoriesActivity extends AppCompatActivity {

    @Bind(R.id.selectAllCheckbox) CheckBox selectAllCheckbox;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;
    @Bind(R.id.submitButton) Button submitButton;

    private ActivityLauncher activityLauncher;
    private JsonParserService jsonParserService;
    private Set<String> categories;
    private int selectedCategoriesCount;

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

    private static final String TAG = UpdateCategoriesActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_categories);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        activityLauncher = new ActivityLauncher();
        jsonParserService = new GsonParserServiceImpl();
        categories = new HashSet<>();
        selectedCategoriesCount = 0;

        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);

        toolbarText.setText("Update Categories");
        submitButton.setText("UPDATE");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categories = new HashSet<>();

        /*achievers = new Category();
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
        sports.button = (Button)findViewById(R.id.sports); */

        updateView();
    }

    private void configureListeners() {
        submitButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (selectedCategoriesCount < 1) {
                    Snackbar.make(view, "Select at least 1 category", Snackbar.LENGTH_LONG).show();
                } else {
                    List<String> categoriesList = new ArrayList<>();
                    categoriesList.addAll(categories);
                    ApplicationState.getUser().setCategories(categoriesList);

                    SharedPreferencesService.getInstance().delete(Config.JSON_CONTENTS);
                    activityLauncher.startCardActivity();

                    /*try {
                        String userdataJson = jsonParserService.toJson(ApplicationState.getUser());
                        System.out.println(Config.USER_UPDATE_URL);
                        System.out.println(userdataJson);

                        if (userdataJson != null) {
                            HttpUtil.getInstance().updateUser(Config.USER_UPDATE_URL, userdataJson, new UserUpdationCallback(coordinatorLayout));
                        }
                    } catch (Exception exception) {

                    }*/
                }
            }
        });

        /*selectAllCheckbox.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (selectAllCheckbox.isChecked()) {
                    selectAllCategories();
                } else {
                    unselectAllCategories();
                }
            }
        });

        achievers.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (achievers.isSelected) {
                    unselectCategory(achievers, R.drawable.ic_achievers_green);
                } else {
                    selectCategory(achievers, R.drawable.ic_achievers_white);
                }
            }
        });

        beautiful.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (beautiful.isSelected) {
                    unselectCategory(beautiful, R.drawable.ic_beautiful_green);
                } else {
                    selectCategory(beautiful, R.drawable.ic_beautiful_white);
                }
            }
        });

        education.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (education.isSelected) {
                    unselectCategory(education, R.drawable.ic_education_green);
                } else {
                    selectCategory(education, R.drawable.ic_education_white);
                }
            }
        });

        empowerment.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (empowerment.isSelected) {
                    unselectCategory(empowerment, R.drawable.ic_empowerment_green);
                } else {
                    selectCategory(empowerment, R.drawable.ic_empowerment_white);
                }
            }
        });

        environment.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (environment.isSelected) {
                    unselectCategory(environment, R.drawable.ic_environment_green);
                } else {
                    selectCategory(environment, R.drawable.ic_environment_white);
                }
            }
        });

        governance.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (governance.isSelected) {
                    unselectCategory(governance, R.drawable.ic_governance_green);
                } else {
                    selectCategory(governance, R.drawable.ic_governance_white);
                }
            }
        });

        health.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (health.isSelected) {
                    unselectCategory(health, R.drawable.ic_health_green);
                } else {
                    selectCategory(health, R.drawable.ic_health_white);
                }
            }
        });

        humanity.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (humanity.isSelected) {
                    unselectCategory(humanity, R.drawable.ic_humanity_green);
                } else {
                    selectCategory(humanity, R.drawable.ic_humanity_white);
                }
            }
        });

        lawAndJustice.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (lawAndJustice.isSelected) {
                    unselectCategory(lawAndJustice, R.drawable.ic_law_justice_green);
                } else {
                    selectCategory(lawAndJustice, R.drawable.ic_law_justice_white);
                }
            }
        });

        realHeroes.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (realHeroes.isSelected) {
                    unselectCategory(realHeroes, R.drawable.ic_real_heros_green);
                } else {
                    selectCategory(realHeroes, R.drawable.ic_real_heros_white);
                }
            }
        });

        scienceAndTech.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (scienceAndTech.isSelected) {
                    unselectCategory(scienceAndTech, R.drawable.ic_science_tech_green);
                } else {
                    selectCategory(scienceAndTech, R.drawable.ic_science_tech_white);
                }
            }
        });

        sports.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (sports.isSelected) {
                    unselectCategory(sports, R.drawable.ic_sports_green);
                } else {
                    selectCategory(sports, R.drawable.ic_sports_white);
                }
            }
        }); */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateView() {
        if (ApplicationState.getUser() != null && ApplicationState.getUser().getCategories() != null) {
            User user = ApplicationState.getUser();

            /*if (user.getCategories().contains("Achievers")) {
                selectCategory(achievers, R.drawable.ic_achievers_white);
            }

            if (user.getCategories().contains("Beautiful")) {
                selectCategory(beautiful, R.drawable.ic_beautiful_white);
            }

            if (user.getCategories().contains("Education")) {
                selectCategory(education, R.drawable.ic_education_white);
            }

            if (user.getCategories().contains("Empowerment")) {
                selectCategory(empowerment, R.drawable.ic_empowerment_white);
            }

            if (user.getCategories().contains("Environment")) {
                selectCategory(environment, R.drawable.ic_environment_white);
            }

            if (user.getCategories().contains("Governance")) {
                selectCategory(governance, R.drawable.ic_governance_white);
            }

            if (user.getCategories().contains("Health")) {
                selectCategory(health, R.drawable.ic_health_white);
            }

            if (user.getCategories().contains("Humanity")) {
                selectCategory(humanity, R.drawable.ic_humanity_white);
            }

            if (user.getCategories().contains("Real Heroes")) {
                selectCategory(realHeroes, R.drawable.ic_real_heros_white);
            }

            if (user.getCategories().contains("Law and Justice")) {
                selectCategory(lawAndJustice, R.drawable.ic_law_justice_white);
            }

            if (user.getCategories().contains("Science and Tech")) {
                selectCategory(scienceAndTech, R.drawable.ic_science_tech_white);
            }

            if (user.getCategories().contains("Sports")) {
                selectCategory(sports, R.drawable.ic_sports_white);
            } */
        }

        if (selectedCategoriesCount == 12) {
            selectAllCheckbox.setChecked(true);
        }
    }

    private void selectCategory(Category category, int drawableResourceId) {
        category.isSelected = true;
        Drawable drawableTop = getResources().getDrawable(drawableResourceId);
        category.button.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        category.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
        category.button.setTextColor(getResources().getColor(R.color.colorWhite));
        //category.button.setTypeface(null, Typeface.BOLD);
        if (category.button.getText().equals("Science & Tech")) {
            categories.add("Science and Tech");
        } else if (category.button.getText().equals("Law & Justice")) {
            categories.add("Law and Justice");
        } else {
            categories.add(category.button.getText().toString());
        }

        selectedCategoriesCount++;
    }

    private void unselectCategory(Category category, int drawableResourceId) {
        category.isSelected = false;
        Drawable drawableTop = getResources().getDrawable(drawableResourceId);
        category.button.setBackgroundResource(R.drawable.border_button_selector);
        category.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
        category.button.setTextColor(getResources().getColor(R.color.colorAccent));
        category.button.setTypeface(null, Typeface.NORMAL);

        if (category.button.getText().equals("Science & Tech")) {
            categories.remove("Science and Tech");
        } else if (category.button.getText().equals("Law & Justice")) {
            categories.remove("Law and Justice");
        } else {
            categories.remove(category.button.getText().toString());
        }

        selectedCategoriesCount--;
    }

    /* private void selectAllCategories() {
        selectCategory(achievers, R.drawable.ic_achievers_white);
        selectCategory(beautiful, R.drawable.ic_beautiful_white);
        selectCategory(education, R.drawable.ic_education_white);
        selectCategory(empowerment, R.drawable.ic_empowerment_white);
        selectCategory(environment, R.drawable.ic_environment_white);
        selectCategory(governance, R.drawable.ic_governance_white);
        selectCategory(health, R.drawable.ic_health_white);
        selectCategory(humanity, R.drawable.ic_humanity_white);
        selectCategory(lawAndJustice, R.drawable.ic_law_justice_white);
        selectCategory(realHeroes, R.drawable.ic_real_heros_white);
        selectCategory(scienceAndTech, R.drawable.ic_science_tech_white);
        selectCategory(sports, R.drawable.ic_sports_white);

        selectedCategoriesCount = 12;
    }

    private void unselectAllCategories() {
        unselectCategory(achievers, R.drawable.ic_achivers_green);
        unselectCategory(beautiful, R.drawable.ic_beautiful_green);
        unselectCategory(education, R.drawable.ic_education_green);
        unselectCategory(empowerment, R.drawable.ic_empowerment_green);
        unselectCategory(environment, R.drawable.ic_environment_green);
        unselectCategory(governance, R.drawable.ic_governance_green);
        unselectCategory(health, R.drawable.ic_health_green);
        unselectCategory(humanity, R.drawable.ic_humanity_green);
        unselectCategory(lawAndJustice, R.drawable.ic_law_justice_green);
        unselectCategory(realHeroes, R.drawable.ic_real_heros_green);
        unselectCategory(scienceAndTech, R.drawable.ic_science_tech_green);
        unselectCategory(sports, R.drawable.ic_sports_green);

        selectedCategoriesCount = 0;
    } */

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
        public void onFailure(Call call, IOException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(view, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
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
                            User user = jsonParserService.fromJson(responseStr, User.class);
                            ApplicationState.setUser(user);
                            //startPaperTimeSelectorActivity();
                            SharedPreferencesService.getInstance().delete(Config.JSON_CONTENTS);
                            activityLauncher.startCardActivity();
                        } catch (Exception exception) {

                        }
                    }
                });
            }
        }
    }
}
