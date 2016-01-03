package com.humanize.android.activity;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.view.Gravity;
import android.widget.TextView;

import com.humanize.android.JsonParser;
import com.humanize.android.R;
import com.humanize.android.common.StringConstants;
import com.humanize.android.data.User;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.util.JsonParserImpl;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

// Referenced classes of package com.humanize.android.activity:
//            PaperReminderActivity

public class  SelectCategoriesActivity extends AppCompatActivity {

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.selectAllCheckbox) CheckBox selectAllCheckbox;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;
    @Bind(R.id.submitButton) Button submitButton;

    private ActivityLauncher activityLauncher;
    private JsonParser jsonParser;
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

    private static final String TAG = SelectCategoriesActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_categories);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        activityLauncher = new ActivityLauncher();
        jsonParser = new JsonParserImpl();
        categories = new HashSet<>();
        selectedCategoriesCount = 0;

        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);

        User user = ApplicationState.getUser();

        if (ApplicationState.getUser() != null && ApplicationState.getUser().getIsConfigured()) {
            toolbarText.setText("UPDATE CATEGORIES");
            submitButton.setText("UPDATE");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            toolbarText.setText("SELECT CATEGORIES");
            submitButton.setText("SAVE");
            toolbarText.setGravity(Gravity.CENTER);
        }

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

        updateView();
    }

    private void configureListeners() {
        submitButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (selectedCategoriesCount < 3) {
                    Snackbar.make(view, "Select minimum 3 categories", Snackbar.LENGTH_LONG).show();
                } else {
                    List<String> categoriesList = new ArrayList<>();
                    categoriesList.addAll(categories);
                    ApplicationState.getUser().setCategories(categoriesList);
                    ApplicationState.getUser().setIsConfigured(true);

                    try {
                        String userdataJson = jsonParser.toJson(ApplicationState.getUser());
                        System.out.println(Config.USER_UPDATE_URL);
                        System.out.println(userdataJson);

                        if (userdataJson != null) {
                            HttpUtil.getInstance().updateUser(Config.USER_UPDATE_URL, userdataJson, new UserUpdationCallback(coordinatorLayout));
                        }
                    } catch (Exception exception) {

                    }
                }
            }
        });

        selectAllCheckbox.setOnClickListener(new android.view.View.OnClickListener() {
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
                    unselectCategory(achievers, R.drawable.ic_achivers_black);
                } else {
                    selectCategory(achievers, R.drawable.ic_achievers_filled_green);
                }
            }
        });

        beautiful.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (beautiful.isSelected) {
                    unselectCategory(beautiful, R.drawable.ic_beautiful_black);
                } else {
                    selectCategory(beautiful, R.drawable.ic_beautiful_filled_green);
                }
            }
        });

        education.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (education.isSelected) {
                    selectCategory(education, R.drawable.ic_education_black);
                } else {
                    selectCategory(education, R.drawable.ic_education_filled_green);
                }
            }
        });

        empowerment.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                //handleButtonSelection(empowerment);

                if (empowerment.isSelected) {
                    unselectCategory(empowerment, R.drawable.ic_empowerment_black);
                } else {
                    selectCategory(empowerment, R.drawable.ic_empowerment_filled_green);
                }
            }
        });

        environment.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                //handleButtonSelection(environment);

                if (environment.isSelected) {
                    unselectCategory(environment, R.drawable.ic_environment_black);
                } else {
                    selectCategory(environment, R.drawable.ic_environment_filled_green);
                }
            }
        });

        governance.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                //handleButtonSelection(governance);

                if (governance.isSelected) {
                    unselectCategory(governance, R.drawable.ic_governance_black);
                } else {
                    selectCategory(governance, R.drawable.ic_governance_filled_green);
                }
            }
        });

        health.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                //handleButtonSelection(health);

                if (health.isSelected) {
                    unselectCategory(health, R.drawable.ic_health_black);
                } else {
                    selectCategory(health, R.drawable.ic_health_filled_green);
                }
            }
        });

        humanity.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                //handleButtonSelection(humanity);

                if (humanity.isSelected) {
                    unselectCategory(humanity, R.drawable.ic_humanity_black);
                } else {
                    selectCategory(humanity, R.drawable.ic_humanity_filled_green);
                }
            }
        });

        lawAndJustice.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                //handleButtonSelection(lawAndJustice);

                if (lawAndJustice.isSelected) {
                    unselectCategory(lawAndJustice, R.drawable.ic_law_justice_black);
                } else {
                    selectCategory(lawAndJustice, R.drawable.ic_law_justice_filled_green);
                }
            }
        });

        realHeroes.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                //handleButtonSelection(realHeroes);

                if (realHeroes.isSelected) {
                    unselectCategory(realHeroes, R.drawable.ic_real_heroes_black);
                } else {
                    selectCategory(realHeroes, R.drawable.ic_real_heros_filled_green);
                }
            }
        });

        scienceAndTech.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                //handleButtonSelection(scienceAndTech);

                if (scienceAndTech.isSelected) {
                    unselectCategory(scienceAndTech, R.drawable.ic_science_tech_black);
                } else {
                    selectCategory(scienceAndTech, R.drawable.ic_science_tech_filled_green);
                }
            }
        });

        sports.button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                //handleButtonSelection(sports);

                if (sports.isSelected) {
                    unselectCategory(sports, R.drawable.ic_sports_black);
                } else {
                    selectCategory(sports, R.drawable.ic_sports_filled_green);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (ApplicationState.getUser() != null && ApplicationState.getUser().getIsConfigured()) {
            getMenuInflater().inflate(R.menu.menu_contact_us, menu);
        }

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

            if (user.getCategories().contains("Achievers")) {
                selectCategory(achievers, R.drawable.ic_achievers_filled_green);
            }

            if (user.getCategories().contains("Beautiful")) {
                selectCategory(beautiful, R.drawable.ic_beautiful_filled_green);
            }

            if (user.getCategories().contains("Education")) {
                selectCategory(education, R.drawable.ic_education_filled_green);
            }

            if (user.getCategories().contains("Empowerment")) {
                selectCategory(empowerment, R.drawable.ic_empowerment_filled_green);
            }

            if (user.getCategories().contains("Environment")) {
                selectCategory(environment, R.drawable.ic_environment_filled_green);
            }

            if (user.getCategories().contains("Governance")) {
                selectCategory(governance, R.drawable.ic_governance_filled_green);
            }

            if (user.getCategories().contains("Health")) {
                selectCategory(health, R.drawable.ic_health_filled_green);
            }

            if (user.getCategories().contains("Humanity")) {
                selectCategory(humanity, R.drawable.ic_humanity_filled_green);
            }

            if (user.getCategories().contains("Real Heroes")) {
                selectCategory(realHeroes, R.drawable.ic_real_heros_filled_green);
            }

            if (user.getCategories().contains("Law and Justice")) {
                selectCategory(lawAndJustice, R.drawable.ic_law_justice_filled_green);
            }

            if (user.getCategories().contains("Science and Tech")) {
                selectCategory(scienceAndTech, R.drawable.ic_science_tech_filled_green);
            }

            if (user.getCategories().contains("Sports")) {
                selectCategory(sports, R.drawable.ic_sports_filled_green);
            }
        }
    }

    private void selectCategory(Category category, int drawableResourceId) {
        category.isSelected = true;
        Drawable drawableTop = getResources().getDrawable(drawableResourceId);
        category.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
        category.button.setTextColor(getResources().getColor(R.color.colorAccent));
        category.button.setTypeface(null, Typeface.BOLD);
        categories.add(category.button.getText().toString());
        selectedCategoriesCount++;
    }

    private void unselectCategory(Category category, int drawableResourceId) {
        category.isSelected = false;
        Drawable drawableTop = getResources().getDrawable(drawableResourceId);
        category.button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
        category.button.setTextColor(getResources().getColor(R.color.colorBlack));
        category.button.setTypeface(null, Typeface.NORMAL);
        categories.remove(category.button.getText().toString());
        selectedCategoriesCount--;
    }

    private void selectAllCategories() {
        selectCategory(achievers, R.drawable.ic_achievers_filled_green);
        selectCategory(beautiful, R.drawable.ic_beautiful_filled_green);
        selectCategory(education, R.drawable.ic_education_filled_green);
        selectCategory(empowerment, R.drawable.ic_empowerment_filled_green);
        selectCategory(environment, R.drawable.ic_environment_filled_green);
        selectCategory(governance, R.drawable.ic_governance_filled_green);
        selectCategory(health, R.drawable.ic_health_filled_green);
        selectCategory(humanity, R.drawable.ic_humanity_filled_green);
        selectCategory(lawAndJustice, R.drawable.ic_law_justice_filled_green);
        selectCategory(realHeroes, R.drawable.ic_real_heros_filled_green);
        selectCategory(scienceAndTech, R.drawable.ic_science_tech_filled_green);
        selectCategory(sports, R.drawable.ic_sports_filled_green);

        selectedCategoriesCount = 12;
    }

    private void unselectAllCategories() {
        unselectCategory(achievers, R.drawable.ic_achivers_black);
        unselectCategory(beautiful, R.drawable.ic_beautiful_black);
        unselectCategory(education, R.drawable.ic_education_black);
        unselectCategory(empowerment, R.drawable.ic_empowerment_black);
        unselectCategory(environment, R.drawable.ic_environment_black);
        unselectCategory(governance, R.drawable.ic_governance_black);
        unselectCategory(health, R.drawable.ic_health_black);
        unselectCategory(humanity, R.drawable.ic_humanity_black);
        unselectCategory(lawAndJustice, R.drawable.ic_law_justice_black);
        unselectCategory(realHeroes, R.drawable.ic_real_heroes_black);
        unselectCategory(scienceAndTech, R.drawable.ic_science_tech_black);
        unselectCategory(sports, R.drawable.ic_sports_black);

        selectedCategoriesCount = 0;
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
                            User user = jsonParser.fromJson(responseStr, User.class);
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
